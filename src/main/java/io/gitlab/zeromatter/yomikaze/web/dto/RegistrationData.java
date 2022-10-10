package io.gitlab.zeromatter.yomikaze.web.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class RegistrationData {

    @Pattern(regexp = "^\\w{3,16}$", message = "registration.username.invalid")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String password;

    @Email
    private String email;
}
