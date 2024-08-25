package com.knguyendev.api.domain.dto.Task.constraints;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Size(max = 300, message = "The description must not exceed 300 characters.")
public @interface DescriptionConstraint {
    String message() default "Invalid description (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}