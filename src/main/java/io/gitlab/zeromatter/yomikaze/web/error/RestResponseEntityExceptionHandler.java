package io.gitlab.zeromatter.yomikaze.web.error;

import io.gitlab.zeromatter.yomikaze.web.dto.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class RestResponseEntityExceptionHandler {

    private final MessageSource messages;

    @NotNull
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    protected ResponseEntity<Object> handleValidationException(BindException ex, HttpServletRequest request) {
        Map<String, String> fieldError = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = Optional.ofNullable(error.getDefaultMessage()).orElse("Invalid " + fieldName);
            fieldError.put(fieldName, messages.getMessage(errorMessage, null, errorMessage, request.getLocale()));
        });
        final GenericResponse bodyOfResponse = GenericResponse.builder()
                .status(false)
                .message("Validation failed")
                .data(fieldError)
                .build();
        return ResponseEntity.badRequest().body(bodyOfResponse);
    }
}
