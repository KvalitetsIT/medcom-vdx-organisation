package dk.medcom.vdx.organisation.controller;

import dk.medcom.vdx.organisation.controller.exception.AbstractApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.openapitools.model.BasicError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

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
}
