package com.knguyendev.api.domain.dto.TaskList.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Covers not null and not just whitespace
@Constraint(validatedBy={})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message="Name of the task list is required!")
@Pattern(
        // I asked, 'what characters would a YouTube video allow?' and here was our guess.
        regexp = "^[a-zA-Z0-9 .,!?'()\\[\\]{}@&-_/]{1,100}$",
        message = "The task list name must be between 1 and 100 characters and can contain letters, numbers, spaces, and the following special characters: .,!?'()[]{}@&-_/~+"
)
public @interface NameConstraint {
    String message() default "Invalid name (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
