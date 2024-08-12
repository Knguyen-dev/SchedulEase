package com.knguyendev.api.domain.dto.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Quick refresher on NotBlank: https://www.baeldung.com/java-bean-validation-not-null-empty-blank
// These are good for ensuring our fields aren't null, and they aren't empty after trimming them of whitespace.

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDTO {

    @NotBlank(message="Username is required!")
    private String username;

    @NotBlank(message="Password is required!")
    private String password;

    public void normalizeData() {

        /*
         * Usernames will be lowercased when the user is created in the database. By lowercasing the login username,
         * we just make it easier for users to login because they can now enter their username case-insensitive.
         * We'll also trim it because there's really no need, and in general this is true. The only input we'll never
         * touch is password inputs.
         *
         * 1. In some systems, passwords can include spaces, and trimming it could mess up with user. In our case, we don't allow spaces.
         * 2. Modifying the password in a user's attempt to login, or even sign up, will mess up their login later and can lead to security concerns.
         * 
         */
        username = username.toLowerCase().trim();
    }
}
