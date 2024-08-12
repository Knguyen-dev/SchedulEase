package com.knguyendev.api.domain.dto.User.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy={})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = "Email is required!")
@Pattern(
        // Using an email regex that handles a good amount of edge cases
        regexp = "^(?i)" +                       // (?i) makes the regex case-insensitive
                "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +  // Local part (before @)
                "|" +                            // OR
                "\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +  // Quoted string (local part)
                "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" + // Quoted pairs (local part)
                "@" +                            // @ symbol
                "(?:" +                          // Start of domain part
                "(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+" + // Subdomain(s)
                "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" + // Top-level domain (TLD)
                "|" +                            // OR
                "\\[(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])\\.){3}" + // IP address
                "(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|" +
                "[a-z0-9-]*[a-z0-9]:" +
                "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
                "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$", // End of IP address or domain literal
        message = "Invalid email format!"
)
@Size(max=40, message="Email is too long! Emails can have at most 40 characters!")
public @interface EmailConstraint {
    String message() default "Invalid Email (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
