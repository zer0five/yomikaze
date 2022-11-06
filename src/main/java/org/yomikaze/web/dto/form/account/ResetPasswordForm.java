package org.yomikaze.web.dto.form.account;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Data
public class ResetPasswordForm {
    @NotBlank(message = "token.blank")
    private String token;

    @NotBlank(message = "registration.password.blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String password;

    @NotBlank(message = "password.confirmation.blank")
    private String passwordConfirmation;

    @AssertTrue(message = "password.confirmation.mismatch")
    @SuppressWarnings("unused") // used by @AssertTrue
    public boolean isPasswordConfirmationValid() {
        return Objects.equals(password, passwordConfirmation);
    }
}
