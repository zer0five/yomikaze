package org.yomikaze.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.yomikaze.validation.UsernameNotExistsConstraint;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

@Data
@NoArgsConstructor
public class SignUpForm {

    @NotBlank(message = "registration.username.blank")
    @Length(min = 3, max = 32, message = "registration.username.invalid.length")
    @Pattern(regexp = "^\\w+$", message = "registration.username.invalid.characters")
    @UsernameNotExistsConstraint(message = "registration.username.exists")
    private String username;

    @Email(message = "registration.email.invalid")
    @NotBlank(message = "registration.email.blank")
    @UsernameNotExistsConstraint(message = "registration.email.exists")
    private String email;

    @NotBlank(message = "registration.password.blank")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$", message = "registration.password.invalid")
    private String password;

    @NotBlank(message = "registration.password.confirmation.blank")
    private String passwordConfirmation;

    @AssertTrue(message = "registration.password.confirmation.mismatch")
    @SuppressWarnings("unused") // used by @AssertTrue
    public boolean isPasswordConfirmationValid() {
        return Objects.equals(password, passwordConfirmation);
    }

}
