package org.yomikaze.web.dto.form.account;

import lombok.Data;
import org.yomikaze.validation.EmailExistsConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailForm {
    @Email(message = "email.invalid")
    @NotBlank(message = "email.blank")
    @EmailExistsConstraint(message = "email.not-exists")
    private String email;
}
