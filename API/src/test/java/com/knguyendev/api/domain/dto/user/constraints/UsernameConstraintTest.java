package com.knguyendev.api.domain.dto.user.constraints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Now the only thing we need to do is that a username should be
 * lowercased! So in the username constraint we should try to lowercase
 * things if possible
 *
 *
 */





public class UsernameConstraintTest {
    private Validator validator;

    private static class TestDTO {
        @UsernameConstraint
        String username;
    }

    @BeforeEach()
    void setup() {
        /*
         * - ValidatorFactory: Used to create a 'Validator' instance.
         *
         * - Validator: Responsible for validating objects against constraints. It checks if the object's properties
         * comply with any constraints defined by annotations, such as '@UsernameConstraint', and provides feedback in the
         * form of constraint violations.
         *
         * - Usage: TestDTO are annotated with validation constraints. We then use the Validator instances to validate these
         * 'TestDTO' objects that have those validation rules on them. Even though we don't reconfigure the Validator isntance
         * between tests, doing this tearup and teardown thing is a common thing in tests in general and ensures isolation.
         *
         */
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidUsernameThenNoViolations() {
        // Create a list of valid usernames
        String[] validUsernames = {
                "knguyen",        // all letters
                "knguyen129",     // lowercased letters and numbers
                "Knguyen",        // Upper and lowercased
                "Knguyen129",     // upper and lower cased, with numbers
                "a".repeat(6), // min amount is 6 characters
                "a".repeat(32) // max amount of characters is 32
        };

        // Iterate through all usernames and test to see that there are no violations
        for (String username: validUsernames) {
            TestDTO testDTO = new TestDTO();
            testDTO.username = username;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);

            // Custom assertion message with detailed failure information
            assertThat(violations)
                    .withFailMessage("Username '%s' should be valid. Violations: %s", username,
                            violations.stream()
                                    .map(v -> String.format("Field: '%s', Message: '%s'",
                                            v.getPropertyPath(), v.getMessage()))
                                    .collect(Collectors.toList()))
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidUsernameThenViolations() {
        String[] invalidUsernames = {
                "",               // too short
                "       ",        // Only spaces
                null,             // null value
                "123456",         // only numbers
                "Some username",  // Has a space in it
                "username_with_special_chars_!@#", // special characters
                "a".repeat(33),
                "user name",      // contains space
                "user@name",       // contains special character
        };

        // Iterate through all invalid usernames and test to see that there are violations
        for (String username : invalidUsernames) {
            TestDTO testDTO = new TestDTO();
            testDTO.username = username;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);

            // Assert violations are there; we also have added logic to logout when an invalid username is seen as valid
            assertThat(violations)
                    .withFailMessage("Expected violations for username: '%s'.", username)
                    .isNotEmpty();
        }
    }
}
