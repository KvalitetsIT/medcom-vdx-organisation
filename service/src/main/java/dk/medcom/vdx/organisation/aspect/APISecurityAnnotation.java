package dk.medcom.vdx.organisation.aspect;


import dk.medcom.vdx.organisation.context.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface APISecurityAnnotation {
	UserRole[] value() default UserRole.UNDEFINED;
}