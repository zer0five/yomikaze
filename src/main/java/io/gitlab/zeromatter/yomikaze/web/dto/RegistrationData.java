package io.gitlab.zeromatter.yomikaze.web.dto;

import io.gitlab.zeromatter.yomikaze.validation.UsernameExistsConstraint;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class RegistrationData {

    @Pattern(regexp = "^\\w{3,16}$", message = "registration.username.invalid")
    @UsernameExistsConstraint(message = "registration.username.exists")
    private String username;

    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String password;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String passwordConfirmation;

}
