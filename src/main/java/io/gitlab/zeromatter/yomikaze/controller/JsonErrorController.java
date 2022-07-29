package io.gitlab.zeromatter.yomikaze.controller;


import io.gitlab.zeromatter.yomikaze.dto.ErrorInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@NoArgsConstructor
@AllArgsConstructor
@RestController
public class JsonErrorController implements ErrorController {

    private boolean debug = false;

    @RequestMapping("/error")
    @ResponseBody
    public ErrorInfo handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String message = (String) request.getAttribute("javax.servlet.error.message");
        Throwable throwable = debug ? null : (Throwable) request.getAttribute("javax.servlet.error.exception");
        return new ErrorInfo(statusCode, message, throwable);
    }
}
