package io.gitlab.zeromatter.yomikaze.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class RegistrationData {

    @Pattern(regexp = "^\\w{3,16}$", message = "registration.username.invalid")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String password;

    @Email(message = "registration.email.invalid")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String passwordConfirmation;
}
