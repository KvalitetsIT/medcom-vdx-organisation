package dk.medcom.vdx.organisation.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class PermissionDeniedException extends Exception {
	private static final long serialVersionUID = 1L;
}
