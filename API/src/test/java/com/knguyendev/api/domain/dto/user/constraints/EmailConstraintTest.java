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

public class EmailConstraintTest {
    private Validator validator;
    private static class TestDTO {
        @EmailConstraint
        String email;
    }

    @BeforeEach()
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void whenValidEmailThenNoViolations() {
        String[] validEmails = {
                "example@example.com",
                "user.name@subdomain.example.com",
                "user1234@example.com",
                "user.name@domain-example.com",
                "user@example.co.uk",
                "user-name1234@example-domain.com",
                "user+name@example.com",
                "averylongusername1234@example.com",
                "user_name@example.com",
                "first.last@example.com"
        };
        for (String email: validEmails) {
            TestDTO testDTO = new TestDTO();
            testDTO.email = email;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Email '%s' should be valid. Violations: %s", email,
                            violations.stream()
                                    .map(v -> String.format("Field: '%s', Message: '%s'",
                                            v.getPropertyPath(), v.getMessage()))
                                    .collect(Collectors.toList()))
                    .isEmpty();
        }
    }

    @Test
    void whenInvalidEmailThenViolations() {
        String[] invalidEmails = {
                "",                   // Empty string
                "   ",                // Just white space
                null,                 // Null value
                "plainaddress",       // Missing '@' symbol
                "user@.com",          // Missing domain
                "user@com",           // Invalid domain format
                " first.last@example.com ", // spaces on the outside of the email
                "longemailaddress12345678901ab@example.com", // 41 characters long, invalid because it exceeds 40 char. limit
                "@example.com",       // Missing local part
                "user@ex^ample.com",  // Invalid characters in domain
                "user name@example.com", // Spaces in local part
                "user@@example.com",  // Multiple '@' symbols
                "user@example",       // Missing top-level domain
                "user@-example.com",  // Domain starts with a hyphen
                "user@example..com",  // Consecutive dots in domain
                "user@.example.com",  // Domain starts with a dot
                ".user@example.com",  // Local part starts with a dot
                "user@example..com",  // Consecutive dots in domain
                "user@.example..com", // Consecutive dots in domain with leading dot
                "user@-example-.com", // Domain starts and ends with hyphen
                "user@[123.123.123.123", // Missing closing bracket in IP address
                "user@sub_domain.com", // Underscore in domain
                "user@example!.com",   // Invalid character in domain
                "user@exam_ple.com",   // Underscore in domain
                "user@exa&mple.com",   // Ampersand in domain
                "user@exam*ple.com",   // Asterisk in domain
                "user@exam(ple.com",   // Parenthesis in domain
                "user@exam)ple.com",   // Parenthesis in domain
                "user@exam,ple.com",   // Comma in domain
                "user@exam/ple.com",   // Slash in domain
                "user@exam:ple.com",   // Colon in domain
                "user@exam;ple.com",   // Semicolon in domain
                "user@exam=ple.com",   // Equal sign in domain
                "user@exam?ple.com",   // Question mark in domain
                "user@exam'ple.com",   // Single quote in domain
                "user@exam\"ple.com",  // Double quote in domain
                "user@exam[ple.com",   // Opening bracket in domain
                "user@exam]ple.com",   // Closing bracket in domain
        };

        for (String email : invalidEmails) {
            TestDTO testDTO = new TestDTO();
            testDTO.email = email;
            Set<ConstraintViolation<TestDTO>> violations = validator.validate(testDTO);
            assertThat(violations)
                    .withFailMessage("Expected violations for email: '%s'.", email)
                    .isNotEmpty();
        }
    }

}
