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

public class HexCodeConstraintTest {
    private Validator validator;
    private static class TestDTO {
        @HexCodeConstraint
        String hexCode;
    }
    @BeforeEach()
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidHexCodeThenViolations() {
        String[] validHexCodes = {
                "#FFFFFF", // White
                "#000000", // Black
                "#FF0000", // Red
                "#00FF00", // Green
                "#0000FF", // Blue
                "#FFFF00", // Yellow
                "#00FFFF", // Cyan
                "#FF00FF", // Magenta
                "#C0C0C0", // Silver
                "#808080", // Gray
                "#800000", // Maroon
                "#008000", // Dark Green
                "#000080", // Navy
                "#808000", // Olive
                "#800080", // Purple
                "#008080"  // Teal
        };
        for (String hexCode: validHexCodes) {
            TestDTO testDTO = new TestDTO();
            testDTO.hexCode = hexCode;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Expected violations for hexCode: '%s'.", hexCode)
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidHexCodeThenNoViolations() {
        String[] invalidHexCodes = {
                null,
                "",
                "   ",
                "#FFF",         // Only 3 digits (should be 6)
                "#FFFFFFF",     // 7 digits (should be 6)
                "FFFFFF",       // Missing '#' prefix
                "#ZZZZZZ",      // Invalid characters (Z is not a valid hex character)
                "#12345G",      // Contains non-hexadecimal character (G)
                "#1234567",     // 7 digits (should be 6)
                "#ABCD",        // Only 4 digits (should be 6)
                "#1234567G",    // 7 digits and invalid character (G)
                "123456",       // Missing '#' prefix
                "#12345",       // Only 5 digits (should be 6)
                "#A1B2C3D",      // 7 digits (should be 6)
        };
        for (String hexCode: invalidHexCodes) {
            TestDTO testDTO = new TestDTO();
            testDTO.hexCode = hexCode;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("hexCode '%s' should be valid. Violations: %s", hexCode, violations.stream().map(v -> String.format("Field: '%s', Message: '%s'",
                                    v.getPropertyPath(), v.getMessage()))
                            .collect(Collectors.toList()))
                    .isNotEmpty();
        }
    }



}
