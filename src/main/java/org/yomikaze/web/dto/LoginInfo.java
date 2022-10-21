package org.yomikaze.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.yomikaze.validation.UsernameExistsConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginInfo {
    @NotBlank(message = "login.username.blank")
    @Length(min = 3, max = 32, message = "login.username.invalid.length")
    @Pattern(regexp = "^\\w+$", message = "login.username.invalid.characters")
    @UsernameExistsConstraint(message = "login.username.not-exists")
    private String username;

    @Email(message = "login.email.invalid")
    @NotBlank(message = "login.email.blank")
    @UsernameExistsConstraint(message = "login.email.not-exists")
    private String email;

    @NotBlank(message = "login.password.blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "login.password.invalid")
    private String password;

}
