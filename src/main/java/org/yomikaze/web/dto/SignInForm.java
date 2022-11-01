package org.yomikaze.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.yomikaze.validation.UsernameExistsConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignInForm {

    @NotBlank(message = "login.username.blank")
    @Length(min = 3, max = 32, message = "login.username.invalid.length")
    @Pattern(regexp = "^\\w+$", message = "login.username.invalid.characters")
    @UsernameExistsConstraint(message = "login.username.not-exists")
    private String username;

    @NotBlank(message = "login.password.blank")
    private String password;

}
