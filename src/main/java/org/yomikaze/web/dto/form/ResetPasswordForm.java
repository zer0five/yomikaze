package org.yomikaze.web.dto.form;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordForm {
    @NotBlank
    private String token;

    @NotBlank
    private String password;

    @NotBlank
    private String passwordConfirmation;

    @AssertTrue
    @SuppressWarnings("unused") // used by @AssertTrue
    public boolean isPasswordConfirmationValid() {
        return password.equals(passwordConfirmation);
    }
}
