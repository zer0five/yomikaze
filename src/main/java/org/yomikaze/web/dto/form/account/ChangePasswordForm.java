package org.yomikaze.web.dto.form.account;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Data
public class ChangePasswordForm {
    @NotBlank
    private String oldPassword;

    @NotBlank(message = "password.blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "password.invalid")
    private String newPassword;

    @NotBlank(message = "password.confirmation.blank")
    private String newPasswordConfirmation;

    @AssertTrue(message = "password.confirmation.mismatch")
    @SuppressWarnings("unused") // used by @AssertTrue
    public boolean isNewPasswordConfirmationValid() {
        return Objects.equals(newPassword, newPasswordConfirmation);
    }
}
