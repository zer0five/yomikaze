package org.yomikaze.web.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class GenreForm {
    @NotBlank(message = "genre.name.blank")
    @Length(min = 3, max = 32, message = "genre.name.invalid.length")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "genre.name.invalid")
    private String name;

    @Length(max = 255, message = "genre.description.invalid")
    private String description;
}
