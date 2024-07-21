package com.knguyendev.TravelApp.config;

import com.knguyendev.TravelApp.domain.dto.error.CustomErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors when validating function parameters.
     * This method triggers when a MethodArgumentNotValidException is thrown, which typically occurs
     * when validation on an argument annotated with @Valid fails.
     *
     * + Annotations:
     * 1. ExceptionHandler: Used to define a method that will handle specific exceptions. In this case,
     *                      a 'MethodArgumentNotValidException', which happens when we validate a function parameter.
     *
     * @param ex the MethodArgumentNotValidException thrown when validation fails
     * @return a ResponseEntity containing a map of field names to error messages and HTTP status code 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();

        StringBuilder concatenatedErrors = new StringBuilder();

        // Retrieves all validation errors, gets the field names and error message and places them in the map
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMap.put(fieldName, errorMessage);
            concatenatedErrors.append(errorMessage).append(" ");
        });

        // Create a single string message
        String finalErrorMessage = concatenatedErrors.toString().trim();

        // Create our custom error object that we want our application to return
        CustomErrorDTO customErrorDTO = CustomErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(finalErrorMessage)
                .errors(errorMap)
                .build();

        // Returns the error map and an error code 400
        return new ResponseEntity<>(customErrorDTO, HttpStatus.BAD_REQUEST);
    }
}
