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
@NotNull(message="Hex code for the color is required!")
@Pattern(
        regexp="^#[a-fA-F0-9]{6}$",
        message="Please enter a valid 6 digit hexadecimal code in the form '#xxxxxx'!"
)
public @interface HexCodeConstraint {
    String message() default "Invalid hex code (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
