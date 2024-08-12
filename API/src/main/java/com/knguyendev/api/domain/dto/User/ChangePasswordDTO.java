package com.knguyendev.api.domain.dto.User;

import com.knguyendev.api.domain.dto.User.constraints.PasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for changing a user's password.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordDTO {

    @NotBlank(message = "Current password is required.")
    private String password;

    @PasswordConstraint
    private String newPassword;

    @NotBlank(message = "Please retype your new password to confirm it.")
    private String confirmNewPassword;

    /**
     * Checks if the new password and confirmation match.
     *
     * @return true if the new password and confirmation match, false otherwise
     */
    public boolean isNewPasswordMatch() {
        return newPassword.equals(confirmNewPassword);
    }
}
