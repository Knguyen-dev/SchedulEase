package com.knguyendev.api.domain.dto.ItemColor.constraints;

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
                "Red",                // 3 characters
                "Green 20",           // Letters, numbers, spaces
                "Light Blue",         // 9 characters (including space)
                "Dark",               // 4 characters
                "Midnight Black",     // 14 characters (including space)
                "Charcoal Grey",      // 12 characters (including space)
                "Turquoise Blue",     // 14 characters (including space)
                "Slate Gray",         // 10 characters (including space)
        };
        for (String name: validNames) {
            TestDTO testDTO = new TestDTO();
            testDTO.name = name;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("name '%s' should be valid. Violations: %s", name, violations.stream().map(v -> String.format("Field: '%s', Message: '%s'",
                                    v.getPropertyPath(), v.getMessage()))
                            .collect(Collectors.toList()))
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidNamesThenViolations() {
        String[] invalidNames = {
                "",                   // Empty string
                "   ",                // Just white space
                null,                 // Null value
                "a".repeat(33) // Has 33 characters (exceeds limit of 32)
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
