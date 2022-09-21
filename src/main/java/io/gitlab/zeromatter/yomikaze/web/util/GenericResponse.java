package io.gitlab.zeromatter.yomikaze.web.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Data
@RequiredArgsConstructor
public class GenericResponse {
    private final String message;
    private final String error;

    public GenericResponse(String message, List<ObjectError> allErrors) {
        this.message = message;
        StringBuilder errors = new StringBuilder();
        for (ObjectError error : allErrors) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                errors.append("{\"field\":");
                errors.append(fieldError.getField());
            } else {
                errors.append("{\"object\":");
                errors.append(error.getObjectName());
            }
            errors.append(",\"message\":");
            errors.append(error.getDefaultMessage());
            errors.append("},");
        }
        errors.deleteCharAt(errors.length() - 1);
        this.error = "[" + errors + "]";
    }
}
