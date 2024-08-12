package com.knguyendev.api.domain.dto.User;

import com.knguyendev.api.domain.dto.User.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * DTO or object that represents the information that the user will input when trying to register a user account.
 */
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
        username = username.toLowerCase();
        email = email.toLowerCase();
    }
}
