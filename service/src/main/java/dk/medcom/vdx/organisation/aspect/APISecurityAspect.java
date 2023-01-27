package dk.medcom.vdx.organisation.aspect;

import dk.medcom.vdx.organisation.context.UserContextService;
import dk.medcom.vdx.organisation.context.UserRole;
import dk.medcom.vdx.organisation.controller.exception.UnauthorizedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class APISecurityAspect {
	
	@Autowired
	UserContextService userService;

	// TODO Jonas bliver denne metode kaldt?
	@Before("@annotation(aPISecurityAnnotation)")
	public void APISecurityAnnotation(JoinPoint joinPoint, APISecurityAnnotation aPISecurityAnnotation) throws Throwable {
		UserRole[] allowedUserRoles = aPISecurityAnnotation.value();
		List<UserRole> allowed = Arrays.asList(allowedUserRoles);

		if (!userService.getUserContext().hasAnyNumberOfRoles(allowed)) {
			throw new UnauthorizedException();
		}
    }
}