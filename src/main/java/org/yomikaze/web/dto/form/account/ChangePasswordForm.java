package org.yomikaze.web.dto.form.account;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class ChangePasswordForm {
    @NotBlank
    private String oldPassword;

    @NotBlank(message = "password.blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String newPassword;

    @NotBlank(message = "password.confirmation.blank")
    private String newPasswordConfirmation;
}
