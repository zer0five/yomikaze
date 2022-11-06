package org.yomikaze.web.dto.form.account;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ForgotPasswordForm {
    @Email
    @NotBlank
    private String email;
}
