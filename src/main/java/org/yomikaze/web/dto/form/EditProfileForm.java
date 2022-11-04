package org.yomikaze.web.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@Data
public class EditProfileForm {
    @NotBlank(message = "profile.display-name.blank")
    @Length(min = 3, max = 32, message = "profile.display-name.invalid.length")
    private String displayName;
    @Length( max = 512, message = "profile.bio.invalid.length")
    private String bio;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date birthday;
    private boolean showBirthday = true;
    private boolean showEmail = true;
    @AssertTrue(message = "registration.password.confirmation.mismatch")
    @SuppressWarnings("unused") // used by @AssertTrue
    public boolean isBirthdayValid() {
        if(birthday == null) return true;
        Instant instant = birthday.toInstant();
        Instant now = Instant.now();
        Duration duration = Duration.between(instant,now);
        return duration.toDays() < 365*200;
    }



}
