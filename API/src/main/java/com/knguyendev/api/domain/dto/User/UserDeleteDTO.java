package com.knguyendev.api.domain.dto.User;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeleteDTO {



    // This represents the user's current password that they're attempting to input as plain-text.
    @NotBlank(message="Password is required. Please enter your current password!")
    private String password;

    @NotBlank(message="Please retype your password to confirm it!")
    private String confirmPassword;

    /**
     * Method used to see if the 'password' and 'confirmPassword' fields are the same. If they are, it means the user
     * is successfully confirming their password, else they aren't.
     */
    public boolean isPasswordMatch() {
        return password.equals(confirmPassword);
    }
}
