package org.yomikaze.web.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class GenreForm {
    @NotBlank(message = "genre.name.blank")
    @Length(min = 3, max = 32, message = "genre.name.invalid.length")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "genre.name.invalid")
    private String name;

    @Nullable
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "genre.description.invalid")
    private String description;
}
