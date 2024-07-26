package com.knguyendev.api.domain.dto.user.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;


/*
+ Password regex, same as the one on the front-end:
1. ^: start of the string
2. (?=.*[a-z]): Checks for at least one lower case letter
3. (?=.*[A-Z]): Checks for at least one upper case letter
4. (?=.*\d): Checks for at least one digit
5. (?=.*[!@#$%^&*]): Checks for at least one of those 'special' characters listed between the brackets
6. (?!.*\s): No white spaces for entire string, which makes sense since it's a password.
7. .{8, 40}: String is at least 8 characters and at most 40.
8. $: End of the string
*/
@Constraint(validatedBy={})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Pattern(
        regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])(?!.*\\s).{8,40}$",
        message="Password needs to be 8 to 40 characters, and must have one uppercase letter, lowercase letter, symbol, and one number."
)
public @interface PasswordConstraint {
    String message() default "Invalid password (default message)!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
