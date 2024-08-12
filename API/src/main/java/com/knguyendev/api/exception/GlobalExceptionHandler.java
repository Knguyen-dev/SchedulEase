package com.knguyendev.api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    // Handles 'ServiceException' exceptions
    @ExceptionHandler(value = {ServiceException.class})
    public ResponseEntity<ExceptionDetails> handleServiceException(ServiceException e) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message(e.getMessage())
                .httpStatus(e.getHttpStatus())
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        return new ResponseEntity<>(exceptionDetails, e.getHttpStatus());
    }

    /**
     * Handles 'MethodArgumentNotValidException' exceptions. It should be noted that 'ResponseEntityExceptionHandler'
     * provides its own '@ExceptionHandler' we have to get creative. We can override one of its methods 'handleMethodArgumentNotValid'
     * and just return our own ResponseEntity
     */
    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message("Input validation failed!")
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .fieldErrors(fieldErrors)
                .build();
        return new ResponseEntity<>(exceptionDetails, exceptionDetails.getHttpStatus());
    }

    // Handles login exceptions
    @ExceptionHandler(value={UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<ExceptionDetails> handleLoginException(Exception e) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message("Username or password is incorrect!")
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        return new ResponseEntity<>(exceptionDetails, exceptionDetails.getHttpStatus());
    }

    // Handles 'AuthenticationException' exceptions
    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<ExceptionDetails> handleAuthenticationException(AuthenticationException e) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message("Authentication Exception: " + e.getMessage())
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
    }

    // Handles 'AuthorizationDeniedException' exceptions
    @ExceptionHandler(value = {AuthorizationDeniedException.class})
    public ResponseEntity<ExceptionDetails> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message("Access Denied! You don't have the permission to access this resource!")
                .httpStatus(HttpStatus.FORBIDDEN)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * For handling any 'accessed-denied' exceptions that will happen when people access routes or do things that they
     * aren't authorized to do!
     */
    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<ExceptionDetails> handleAccessDeniedException(AccessDeniedException e) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message(e.getMessage())
                .httpStatus(HttpStatus.FORBIDDEN)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * For handling general exceptions
     * NOTE: Similar ordering to a try/catch, as the more specific error handlers are put on top, whilst the more general
     * ones, such as this one, is on the bottom.
     */
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ExceptionDetails> handleGlobalException(Exception e) {
        ExceptionDetails exceptionDetails = ExceptionDetails.builder()
                .message(e.getMessage())
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        return new ResponseEntity<>(exceptionDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
