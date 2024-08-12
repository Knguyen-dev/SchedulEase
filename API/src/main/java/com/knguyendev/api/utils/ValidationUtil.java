package com.knguyendev.api.utils;

import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;


import java.util.Set;
import java.util.stream.Collectors;

public class ValidationUtil {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    public static <T> void validate(T object) throws IllegalArgumentException {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            // Collect all validation error messages
            String errorMessages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));

            // Throw an exception with detailed error messages
            throw new IllegalArgumentException("Validation failed: " + errorMessages);
        }
    }
}
