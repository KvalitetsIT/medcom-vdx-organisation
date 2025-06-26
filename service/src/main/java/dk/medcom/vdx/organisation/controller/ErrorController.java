package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.controller.exception.AbstractApiException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.openapitools.model.BasicError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<BasicError> handleValidationException(ValidationException e, HttpServletRequest request) {
        logger.debug("Handling ValidationException.", e);

        var error = getBasicBadRequestErrorMessage(request, e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BasicError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.debug("Handling MethodArgumentNotValidException.", e);

        var error = getBasicBadRequestErrorMessage(request, validationErrorToString(e));
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AbstractApiException.class)
    public ResponseEntity<BasicError> handleApiException(AbstractApiException e, HttpServletRequest request) {
        logger.debug("Handling ApiException: {}", e.getMessage());
        var error = new BasicError()
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .status(e.getHttpStatus().value())
                .statusText(e.getHttpStatus().getReasonPhrase())
                .error(e.getMessage());

        return ResponseEntity.status(e.getHttpStatus().value()).body(error);
    }

    private BasicError getBasicBadRequestErrorMessage(HttpServletRequest request, String errorMessage) {
        return new BasicError()
                .path(request.getRequestURI())
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .statusText(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .error(errorMessage);
    }

    private String validationErrorToString(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getFieldError();
        return fieldError == null ? e.getMessage()
                : fieldError.getField() +
                ": " + fieldError.getDefaultMessage();
    }
}
