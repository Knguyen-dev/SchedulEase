package com.knguyendev.TravelApp.domain.dto.user.constraints;

import jakarta.validation.constraints.Size;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


/*
 * Biography is optional, could be null, but we'll place a constraint that the maximum allowed
 * length is 150 characters!
 * */
@Constraint(validatedBy={})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Size(max=150, message="Biography is must be at most 150 characters long!")
public @interface BiographyConstraint {
    String message() default "Invalid biography (default message)!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
