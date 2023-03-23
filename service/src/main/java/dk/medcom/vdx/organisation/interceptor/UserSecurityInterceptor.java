package dk.medcom.vdx.organisation.interceptor;

import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.controller.exception.PermissionDeniedException;
import dk.medcom.vdx.organisation.controller.exception.UnauthorizedException;
import dk.medcom.vdx.organisation.dao.jpa.OrganisationRepository;
import dk.medcom.vdx.organisation.dao.jpa.entity.Organisation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserSecurityInterceptor implements HandlerInterceptor {
	
	private static Logger logger = LoggerFactory.getLogger(UserSecurityInterceptor.class);
	
	@Autowired
	private UserContextService userService;
	
	@Autowired
	private OrganisationRepository organisationRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		logger.debug("Entry of preHandle method");

		var userContext = userService.getUserContext();

		if(userContext == null) {
			logger.warn("No user context found");
			if(logger.isDebugEnabled()) {
				logger.debug(request.getRequestURI());
			}
			throw new PermissionDeniedException();
		}

		String userOrganisationId = userContext.getUserOrganisation();
		Organisation organisation = null;
		if ((userOrganisationId != null) && (!userOrganisationId.isEmpty())) {
			organisation = organisationRepository.findByOrganisationId(userOrganisationId);
			if (organisation == null) {
				logger.debug("organisation is not found using findByOrganisationId(userOrganisationId). userOrganisationId: " + userOrganisationId );
				throw new PermissionDeniedException();
			}			
		}
		
		String userEmail = userService.getUserContext().getUserEmail();
		
		if (userService.getUserContext().hasNoLegalRoles()) {
			logger.debug("userRole is not valid or not set");
			throw new UnauthorizedException();
		}
				
		if (!userService.getUserContext().hasOnlyRole(UserRole.PROVISIONER)) {
			if ((userEmail == null ) || (userEmail.isEmpty()) || (userOrganisationId == null) || (userOrganisationId.isEmpty())) {
				logger.debug("Email or user are not valid: userEmail: " + userEmail + ", userOrganisationId = " + userOrganisationId);
				throw new UnauthorizedException();
			}
		}

		String organisationId = null;
		if(organisation != null) {
			organisationId = organisation.getOrganisationId();
		}
		logger.info("User information: organisation: {}, email: {}, roles: {}", organisationId, userEmail, userService.getUserContext().getUserRoles());

		logger.debug("Exit of preHandle method: Usermail: " + userEmail + " UserRole: " + userService.getUserContext().getUserRoles() + " Organisation: " + organisation);
		return true;
	}
}