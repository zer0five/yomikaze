package org.yomikaze.web.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class RequestForm {
    @NotBlank(message = "request.message.blank")
    @Length(min = 3, max = 255, message = "request.message.invalid.length")
    private String message;


}
