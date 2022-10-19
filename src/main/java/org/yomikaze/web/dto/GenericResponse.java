package org.yomikaze.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GenericResponse {

    private Boolean status;
    private String message;
    private Collection<String> messages;
    private Object error;
    private Collection<Object> errors;
    private Object data;

}
