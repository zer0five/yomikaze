package io.gitlab.zeromatter.yomikaze.web.error;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messages;

//    @ExceptionHandler({ Exception.class })
//    public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
//        logger.error("500 Status Code", ex);
//        final GenericResponse bodyOfResponse = new GenericResponse(messages.getMessage("message.error", null, request.getLocale()), "InternalError");
//        return new ResponseEntity<>(bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
