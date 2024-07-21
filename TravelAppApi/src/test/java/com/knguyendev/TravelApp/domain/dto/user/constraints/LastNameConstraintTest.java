package com.knguyendev.TravelApp.domain.dto.user.constraints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

public class LastNameConstraintTest {
    private Validator validator;

    private static class TestDTO {
        @LastNameConstraint
        String lastName;
    }

    @BeforeEach()
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidLastNameNoViolations() {
        String[] validLastNames = {
                "Johnson",      // Upper and lower case
                "May",          // Upper case only
                "lowercased",   // Lowercase only
                "m",            // minimum length
                "a".repeat(32), // max length
        };
        for (String lastName: validLastNames) {
            TestDTO testDTO = new TestDTO();
            testDTO.lastName = lastName;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Last name '%s' should be valid. Violations: %s", lastName, violations.stream().map(v -> String.format("Field: '%s', Message: '%s'",
                            v.getPropertyPath(), v.getMessage()))
                    .collect(Collectors.toList()))
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidLastNameViolations() {
        String[] invalidLastNames = {
                "",                   // Empty string
                "   ",                // Just white space
                null,                 // Null value
                "With spaces",        // Letters, but has white space
                "Mil3s",              // Has numbers
                "A%l*ngton$",          // Has special characters
                "a".repeat(33) // Has 33 characters (exceeds limit of 32)
        };
        for (String lastName: invalidLastNames) {
            TestDTO testDTO = new TestDTO();
            testDTO.lastName = lastName;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Expected violations for last name: '%s'.", lastName)
                    .isNotEmpty();
        }
    }
}
