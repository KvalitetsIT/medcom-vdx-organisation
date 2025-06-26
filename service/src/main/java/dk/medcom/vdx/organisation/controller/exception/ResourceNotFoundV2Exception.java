package dk.medcom.vdx.organisation.controller.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundV2Exception extends AbstractApiException {
    public ResourceNotFoundV2Exception(String errorMessage) {
        super(HttpStatus.NOT_FOUND, errorMessage);
    }
}
