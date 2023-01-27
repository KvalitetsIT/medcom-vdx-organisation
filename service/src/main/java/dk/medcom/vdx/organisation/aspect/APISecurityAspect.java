package dk.medcom.vdx.organisation.aspect;

import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.controller.exception.UnauthorizedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class APISecurityAspect {
	private static final Logger logger = LoggerFactory.getLogger(APISecurityAspect.class);
	@Autowired
	UserContextService userService;

	@Before("@annotation(aPISecurityAnnotation)")
	public void APISecurityAnnotation(JoinPoint joinPoint, APISecurityAnnotation aPISecurityAnnotation) throws Throwable {
		UserRole[] allowedUserRoles = aPISecurityAnnotation.value();
		List<UserRole> allowed = Arrays.asList(allowedUserRoles);

		if (!userService.getUserContext().hasAnyNumberOfRoles(allowed)) {
			logger.info("User does not have required role. Required: {}, Actual role: {}.", allowed, userService.getUserContext().getUserRoles());
			throw new UnauthorizedException();
		}
    }
}