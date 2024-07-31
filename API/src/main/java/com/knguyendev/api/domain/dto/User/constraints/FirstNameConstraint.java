package com.knguyendev.api.domain.dto.User.constraints;

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
@NotNull
@Pattern(
        regexp="^[A-Za-z]{1,32}$",
        message="Your first name should be 1 to 32 characters, and only contain letters!"
)
public @interface FirstNameConstraint {
    String message() default "Invalid first name (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
