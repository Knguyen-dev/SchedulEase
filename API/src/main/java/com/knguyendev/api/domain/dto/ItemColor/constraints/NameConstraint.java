package com.knguyendev.api.domain.dto.ItemColor.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy={})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message="The color name is required!")
@Pattern(
        regexp="^(?=.*\\S).{1,32}$",
        message="The color name must be 1 to 32 characters long and contain at least one non-whitespace character."
)
public @interface NameConstraint {
    String message() default "Invalid color item name (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
