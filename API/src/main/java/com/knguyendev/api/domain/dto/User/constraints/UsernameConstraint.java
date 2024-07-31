package com.knguyendev.api.domain.dto.User.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.lang.annotation.*;


/**
 * Custom annotation that defines the input validation rules for a user's
 * username. So we'll make a constraint 'bean'
 *
 * NOTE: This does not check if a username is unique, rather that is done via a check at the
 * UserService implementation.
 *
 *
 * + Explaining the annotations
 *
 * 'Constraint':
 * - validatedBy: Specifies any 'validator' classes that validate elements annotated with annotation. Validators classes will contain the
 * validation logic, as they'll implement some 'isValid' method. Of course you can create your own validator class and it's pretty simple, but
 * we're not doing it in this case so we left it empty. The '@Pattern' annotation will be providing the validator
 * logic, so that handles things for us.
 *
 * 'Target': This specifies where this annotation can be applied and used.
 * - 'ElementType.FIELD': Can be applied to fields of a class.
 * - 'ElementType.METHOD': Can be applied to class methods. So in the example below we're validating the return value. If
 * the string returned by getUsername doesn't meet the restraints then an error is thrown.
 * '@UsernameConstraint'
 *   public String getUsername() {
 *       // Business logic to retrieve the username
 *       return "ValidUsername";
 *   }
 *
 * - 'ElementType.PARAMETER': Can be applied to method parameters. You'd have to put the '@Validated' annotation on your
 * rest controller class to enable method-level validation. Here our 'UserConstraint' is being applied to the 'username'
 * parameter that we're getting in the controller function.
 *
 * 1. Enabling method validation for a single class or controller in this case:
 * '@RestController'
 * '@Validated'
 * public class UserController {
 *      // Route to see if username is vlaid
 *     '@GetMapping("/validateUsername")'
 *     public String validateUsername(
 *         '@RequestParam("username")' @UsernameConstraint String username) {
 *         return "Validated username: " + username;
 *     }
 * }
 * 2. You can also enable method validation globally
 * '@Configuration'
 * '@Validated'
 * public class MethodValidationConfig {
 *     // This class can be empty; the @Validated annotation is enough to enable method validation
 * }
 * In either case, when invalid input is provided to our 'validateUsername' route, Spring automatically handles the
 * validation error, and returns a '400 Bad Request' with the appropriate error message.
 *
 * - 'ElementType.ANNOTATION_TYPE': Can be applied to other annotations (So you'd call this annotation a 'meta-annotation')
 *
 * 'Retention(RetentionPolicy.RUNTIME)': Specifies how long we have to keep these annotations. So 'RUNTIME" just means
 * that the annotation should be available when the application runs, allowing frameworks like Spring to process it.
 *
 * 'Pattern': A standard Java 'bean' Validation annotation. Basically it's used to validate that hte annotated string matches
 * the regular expression specified.
 *
 * + Error response structure:
 * As briefly mentioned earlier, when using validation annotations in Spring Boot, any validation errors are automatically
 * handled and returned as part of the response. Spring boot sends a status '400 bad request', alongside some detailed error
 * structure.
 * For example:
 * {
 *     "timestamp": "2024-07-18T12:34:56.789+00:00", // the time the error happened
 *     "status": 400, // http status code, which will be '400' for bad requests
 *     "error": "Bad Request", // the type of error
 *     "errors": [ // An array of validation errors
 *         {
 *             "codes": [ // Any validation codes
 *                 "UsernameConstraint.username",
 *                 "UsernameConstraint"
 *             ],
 *             "arguments": [], // Any 'arguments' used in the validation.
 *             "defaultMessage": "Name should be 1 to 32 characters long and only contain letters!", // Error message
 *             "objectName": "username", // Name of the object that failed validation
 *             "field": "username", // Name of the specific field that failed
 *             "rejectedValue": "12345", // The value that was rejected
 *             "bindingFailure": false, // Indicates if failure was due to 'binding' issues; if it was more-so a server-side error.
 *             "code": "UsernameConstraint" // Specific validation code
 *         }
 *     ],
 *     "message": "Validation failed for object='username'. Error count: 1",
 *     "path": "/validateUsername"
 * }
 * If you want to customize your error responses, you can create a global exception handler using the 'ControllerAdvice'
 * annotation.
 */
@Constraint(validatedBy={})
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NotNull(message = "Username is required!")
@Pattern(
        regexp="^(?=.*[A-Za-z])[A-Za-z0-9]{6,32}$",
        message = "Username must be between 6 to 32 characters long, can only contain alphanumeric characters, and must have at least one letter."
)
public @interface UsernameConstraint {
    /**
     * A method declaration within our annotation interface. It specifies the default error message that'll be used if
     * the validation fails, and no specific error message is provided.
     *
     */
    String message() default "Invalid username (default message)!";

    /**
     * Allows you to specify 'validation groups'. These groups allow you to selectively validate different constraints
     * in different scenarios. For example, you may validate the username differently in a 'basic' scenario
     * compared to an 'advanced' scenario.
     *
     */
    Class<?>[] groups() default {};

    /**
     * payload: Provides a way to associate random data with the constraint. It's usually used by frameworks and not directly by
     * people using annotations. Essentially, it'll just be something you'll include so that the frameworks can gather metadata
     * for your error handling, other than that you don't need to interact with this.
     *
     */
    Class<? extends Payload>[] payload() default {};
}
