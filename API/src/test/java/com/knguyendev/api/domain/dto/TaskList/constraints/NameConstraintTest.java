package com.knguyendev.api.domain.dto.TaskList.constraints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;

public class NameConstraintTest {
    private Validator validator;
    private static class TestDTO {
        @NameConstraint
        String name;
    }

    @BeforeEach()
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidNameThenNoViolations() {
        String[] validNames = {
                "A",                           // Minimum length (1 character)
                "Short Name",                  // Typical short name
                "This is a slightly longer name", // Valid name with spaces and punctuation
                "Name with numbers 123",       // Name with numbers
                "Name_with_underscores",        // Name with underscores
                "Name-with-dashes",             // Name with dashes
                "Name.with.dots",               // Name with dots
                "Name&with@spec{}al&chars!",      // Name with special characters allowed
                "a".repeat(100) // maximum length
        };
        for (String name: validNames) {
            TestDTO testDTO = new TestDTO();
            testDTO.name = name;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Name '%s' should be valid. Violations: %s", name, violations.stream().map(v -> String.format("Field: '%s', Message: '%s'",
                                    v.getPropertyPath(), v.getMessage()))
                            .collect(Collectors.toList()))
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidNamesThenViolations() {
        String[] invalidNames = {
                null,
                "",                          // Empty string
                "     ",
                "This name is definitely way too long to be valid because it exceeds one hundred characters which is the limit for this constraint.", // Exceeds max length
                "Invalid character #",       // Contains disallowed special character
                "Another@Invalid*Char|",      // Contains disallowed special character
                "Name with newline\nchar",   // Contains newline character
                "Name with \u0000null char", // Contains null character
                "Name with tabs\t",          // Contains tab character
        };
        for (String name: invalidNames) {
            TestDTO testDTO = new TestDTO();
            testDTO.name = name;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Expected violations for name: '%s'.", name)
                    .isNotEmpty();
        }
    }
}

