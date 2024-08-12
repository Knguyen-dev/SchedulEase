package com.knguyendev.api.domain.dto.User;

import com.knguyendev.api.domain.dto.User.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO that represents info that the user is going to submit in order to update their profile. This
 * will be the public information that the user can display on their profile page.
 * So to update their profile they should be able to change the username, email, firstName, lastName, and the biography.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileUpdateDTO {
    @UsernameConstraint
    private String username;

    @EmailConstraint
    private String email;

    @FirstNameConstraint
    private String firstName;

    @LastNameConstraint
    private String lastName;

    @BiographyConstraint
    private String biography;

    public void normalizeData() {
        username = username.toLowerCase().trim();
        email = email.toLowerCase().trim();

        // first and last name don't need to be lowercased, and also won't be trimmed since they don't accept whitespace.
        // We're not going to trim a biography since whitespace can be used to make things cool for the user.
    }
}
