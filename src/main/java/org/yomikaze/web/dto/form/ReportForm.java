package org.yomikaze.web.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class ReportForm {
    @NotBlank(message = "report.message.blank")
    @Length(max = 255, message = "report.message.length")
    private String message;
}
