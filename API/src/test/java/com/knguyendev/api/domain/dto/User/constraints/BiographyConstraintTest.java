package com.knguyendev.api.domain.dto.User.constraints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

public class BiographyConstraintTest {
    private Validator validator;

    private static class TestDTO {
        @BiographyConstraint
        String biography;
    }

    @BeforeEach()
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidBiographyThenNoViolations() {
        String[] validBiographies = {
                null,
                "",                     // min length
                "A sample biography",   // Some sample text
                "a".repeat(150)   // max length
        };

        for (String biography : validBiographies) {
            TestDTO testDTO = new TestDTO();
            testDTO.biography = biography;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Biography '%s' should be valid. Violations: %s", biography,
                            violations.stream()
                                    .map(v -> String.format("Field: '%s', Message: '%s'",
                                            v.getPropertyPath(), v.getMessage()))
                                    .collect(Collectors.toList()))
                    .isEmpty();
        }
    }


    @Test
    void whenInvalidBiographyThenViolations() {
        String[] invalidBiographies = {
                "a".repeat(151) // Exceeds maximum limit
        };
        for (String biography : invalidBiographies) {
            TestDTO testDTO = new TestDTO();
            testDTO.biography = biography;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Expected violations for biography: '%s'.", biography)
                    .isNotEmpty();
        }
    }
}
