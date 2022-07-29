package io.gitlab.zeromatter.yomikaze.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorInfo {

    private final Integer status;
    private final String message;
    private final Throwable throwable;

}
