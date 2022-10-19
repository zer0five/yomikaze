package org.yomikaze.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.yomikaze.validation.UsernameExistsConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
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