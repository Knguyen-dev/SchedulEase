package com.knguyendev.TravelApp.domain.dto.user;

import com.knguyendev.TravelApp.domain.dto.user.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* + When does validation trigger?
* Merely instantiating a UserRegistrationDTO doesn't trigger the validation. There are only
* three cases where the validation is going to trigger and throw an error:
*
* 1. Controller method parameters: When a controller method is called and teh DTO is a parameter
* annotated with '@Valid' or '@Validated'.
*
* @PostMapping("/register")
* public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
*     // The userRegistrationDTO will be validated here
* }
*
* 2. Service method parameters: When a method in a service class is called and the DTO
* is a parameter annotated with '@Validated' or '@Valid'. So again, the main ways things
* are validated is when you place your objects that have the custom validation annotations, as
* parameters to other functions (commonly controllers or service functions) with the '@Valid' or '@Validated' annotations.
*
*
* 3. Manual/Programatic validation: You can activate the validation mechanism using a 'Validator' bean.
*
* @Autowired
* private Validator validator;
*
* public void validateUser(UserReistrationDTO userRegistrationDTO) {
*   Set<ConstraintViolation<UserRegistrationDTO>> violations = validator.validate(userRegistrationDTO);
*   if (!violations.isEmpty()) {
*      // Handle validation errors here.
*   }
* }
*
*
*
*
* + Credits:
* https://medium.com/@himani.prasad016/validations-in-spring-boot-e9948aa6286b
*
* https://www.baeldung.com/spring-mvc-custom-validator
* */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationDTO {
    @UsernameConstraint
    private String username;

    @EmailConstraint
    private String email;

    @FirstNameConstraint
    private String firstName;

    @LastNameConstraint
    private String lastName;

    @PasswordConstraint
    private String password;

    /**
     * This is to transform the data. For example, for usernames and emails we will lowercase them.
     *
     * - username: We believe usernames are duplicate even if they have different casing. E.g. 'SuperMan123' and 'superman123'
     * are the same username. We want to lowercase our usernames before we insert them into the database.
     *
     * - email: This is the same in the case of the latter. Like usernames, a given email can only be associated with one user
     * account. So emails 'SuperMan@hotmail.com' is the same as 'superman@hotmail.com'.
     *
     * - Other values and trimming: Due to our regex patterns, for usernames and emails, they won't be able to have spaces.
     * As well as this, firstName, lastName, and password are also the same as they can't contain spaces either. So we don't
     * have to worry about trimming spaces because if there are spaces in input then the error will receive an error that reminds
     * them of the input constraints.
     */
    public void normalizeData() {
        username = username.trim().toLowerCase();
        email = email.toLowerCase();
    }
}
