package com.knguyendev.api.domain.dto.Task.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy={})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message="Name of the task list is required!")
@Size(min = 1, max = 100, message = "The task title must be between 1 and 100 characters.")
public @interface TitleConstraint {
    String message() default "Invalid title (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
