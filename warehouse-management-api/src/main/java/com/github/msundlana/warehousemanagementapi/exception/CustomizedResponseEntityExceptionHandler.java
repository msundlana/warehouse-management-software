package com.github.msundlana.warehousemanagementapi.exception;

import com.github.msundlana.warehousemanagementapi.models.ExceptionResponse;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handlerBadCredentials(BadCredentialsException ex, WebRequest request) {
        logger.error("Credentials Invalid: {}", ex.getMessage());
        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(exceptionResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> handlerForbiddenException(ForbiddenException ex, WebRequest request) {
        logger.error("Forbidden access: {}", ex.getMessage());
        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(exceptionResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(ConstraintViolationException ex, WebRequest request) {
        logger.error("Constraint violation error occurred: {}", ex.getMessage());
        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(exceptionResponse);
    }

    @ExceptionHandler({ArticleNotFoundException.class,ProductNotFoundException.class, UsernameNotFoundException.class})
    public final ResponseEntity<ExceptionResponse> handleResourceNotFoundException
            (Exception ex, WebRequest request)  {

        logger.error("Error occurred while extracting resource: {}", ex.getMessage());

        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(exceptionResponse);
    }

    @ExceptionHandler({CallNotPermittedException.class, ResourceAccessException.class})
    public ResponseEntity<ExceptionResponse> handleServiceUnavailableException(Exception ex, WebRequest request) {
        logger.error("Service Unavailable Error occurred while extracting productInventory: {}", ex.getMessage());

        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message("No products available.")
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(exceptionResponse);
    }

    @ExceptionHandler(BulkheadFullException.class )
    public ResponseEntity<ExceptionResponse> handleBulkheadFullException(Exception ex, WebRequest request) {
        logger.error("Service struggling to handle to many concurrent request: {}", ex.getMessage());

        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.BANDWIDTH_LIMIT_EXCEEDED)
                .body(exceptionResponse);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ExceptionResponse> handleRequestNotPermitted(Exception ex, WebRequest request) {
        logger.error("Maximum request limit reach : {}", ex.getMessage());

        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(exceptionResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ExceptionResponse> handleAllException(Exception ex, WebRequest request) {

        logger.error("Internal Server Error occurred while extracting products inventory: {}", ex.getMessage());

        var exceptionResponse = ExceptionResponse.builder()
                .timestamp(new Date())
                .message(ex.getMessage())
                .details(request.getDescription(false))
                .build();

        return ResponseEntity.internalServerError()
                .body(exceptionResponse);
    }

}
