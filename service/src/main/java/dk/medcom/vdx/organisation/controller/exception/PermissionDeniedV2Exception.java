package dk.medcom.vdx.organisation.controller.exception;

import org.springframework.http.HttpStatus;

public class PermissionDeniedV2Exception extends AbstractApiException {
    public PermissionDeniedV2Exception(String errorMessage) {
        super(HttpStatus.FORBIDDEN, errorMessage);
    }
}
