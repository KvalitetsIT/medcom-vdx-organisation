package dk.medcom.vdx.organisation.interceptor;

import dk.medcom.vdx.organisation.context.UserContextImpl;
import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.context.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OauthInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(OauthInterceptor.class);

    @Autowired
    UserContextService userService;

    @Value("${userservice_token_attribute_organisation}")
    private String userServiceTokenAttributeOrganisation;

    @Value("${userservice_token_attribute_email}")
    private String userServiceTokenAttributeEmail;

    private static final Map<String, UserRole> roleAttToUserRole = new HashMap<>();
    static {
        roleAttToUserRole.put("ROLE_ATT_meeting-user", UserRole.USER);
        roleAttToUserRole.put("ROLE_ATT_meeting-admin", UserRole.ADMIN);
        roleAttToUserRole.put("ROLE_ATT_meeting-planner", UserRole.MEETING_PLANNER);
        roleAttToUserRole.put("ROLE_ATT_meeting-provisioner", UserRole.PROVISIONER);
        roleAttToUserRole.put("ROLE_ATT_meeting-provisioner-user", UserRole.PROVISIONER_USER);
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.debug("Validating access for endpoint {}", request.getServletPath());
        if (!(handler instanceof HandlerMethod handlerMethod) || handlerMethod.getBeanType().equals(BasicErrorController.class)) {
            return true; // Show 404 instead of 403 on URL's that does exist.
        }
        var annotation = handlerMethod.getMethod().getAnnotation(Oauth.class);
        if(annotation != null) {
            logger.debug("Request is OAuth request.");
            handleContextParsing();
        }

        logger.debug("OAuth context handled.");
        return true;
    }

    private void handleContextParsing() {
        logger.debug("Handle oauth context parsing.");
        var cred = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        var email = (String) cred.getClaim(userServiceTokenAttributeEmail);
        var organisationId = (String) cred.getClaim(userServiceTokenAttributeOrganisation);

        var userRoles = handleRoleAttUserRoleAuthParsing();

        var userContext = new UserContextImpl(organisationId, email, userRoles);
        userService.setUserContext(userContext);
    }

    private List<UserRole> handleRoleAttUserRoleAuthParsing() {
        logger.debug("Handle role attribute to user role parsing.");
        var res = new ArrayList<UserRole>();

        var auth = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        var asList = auth.stream().toList();
        for (var i : asList) {
            var role = roleAttToUserRole.get(i.getAuthority());

            res.add(role);
        }

        return res;
    }
}
