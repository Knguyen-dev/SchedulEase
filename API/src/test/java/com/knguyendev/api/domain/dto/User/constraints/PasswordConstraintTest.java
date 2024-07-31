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

public class PasswordConstraintTest {
    private Validator validator;

    private static class TestDTO {
        @PasswordConstraint
        String password;
    }

    @BeforeEach()
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidPasswordThenNoViolations() {
        String[] validPasswords = {
                "P@ssw0rd!",
                "SecureP@ssw0rd!2024",
                "R3li@bleP@ssw0rd",
                "Str0ngP@ssw0rd!",
                "P$ssword123"
        };

        // Iterate through all usernames and test to see that there are no violations
        for (String password: validPasswords) {
            TestDTO testDTO = new TestDTO();
            testDTO.password = password;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Password '%s' should be valid. Violations: %s", password,
                            violations.stream()
                                    .map(v -> String.format("Field: '%s', Message: '%s'",
                                            v.getPropertyPath(), v.getMessage()))
                                    .collect(Collectors.toList()))
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidPasswordThenViolations() {
        String[] invalidPasswords = {
                "",                   // Empty string
                "   ",                // Just white space
                null,                 // Null value
                "short",              // Too short (less than 8 characters)
                "aA1#".repeat(10) + "3",  // Has lower, upper, num, and special characters. However, it's too long (41 characters, exceeding the maximum length of 40)
                "asdfdijedjs",        // Lowercased letters only (no uppercase, numbers, or symbols)
                "ASDFKLEJF",          // Uppercased letters only (no lowercase, numbers, or symbols)
                "12398773",           // Numbers only (no letters or symbols)
                "!@#$%^&*",         // symbols only (no letters or numbers)
                "My B4#nd number",    // Contains whitespace (whitespace isn't allowed),
                "P4ssword",           // Upper and lowercased letters, and numbers. However it doesn't have special character which should make it fail
                "38%*82#@029",       // Numbers and special characters only; no letters so it should fail
                "$ec&re#eiId"       // Special characters and upper and lower letters.
        };
        for (String password: invalidPasswords) {
            TestDTO testDTO = new TestDTO();
            testDTO.password = password;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Expected violations for password: '%s'.", password)
                    .isNotEmpty();
        }
    }

}
