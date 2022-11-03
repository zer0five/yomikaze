package org.yomikaze.web.dto.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerifyForm {
    @NotBlank
    private String token;
}
