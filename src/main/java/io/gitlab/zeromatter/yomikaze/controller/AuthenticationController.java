package io.gitlab.zeromatter.yomikaze.controller;

import io.gitlab.zeromatter.yomikaze.dto.AuthenticationResult;
import io.gitlab.zeromatter.yomikaze.entity.account.Account;
import io.gitlab.zeromatter.yomikaze.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authentication;

    @PostMapping(value = {"/login", "/sign-in"}, consumes = {"application/json", "application/x-www-form-urlencoded", "multipart/form-data"})
    @ResponseBody
    public AuthenticationResult login(@RequestBody @RequestParam(value = "username-or-email", required = false, defaultValue = "") String usernameOrEmail,
                                      @RequestParam(value = "password", required = false, defaultValue = "") String password,
                                      HttpServletRequest request) {
        try {
            Account account = authentication.login(usernameOrEmail, password);
            String token = authentication.createSession(account, request.getHeader("User-Agent"), request.getRemoteAddr());
            return new AuthenticationResult(true, "Successfully", token);
        } catch (IllegalArgumentException e) {
            return new AuthenticationResult(false, e.getMessage());
        }
    }

    @PostMapping(value = {"/register", "/sign-up"}, consumes = {"application/json", "application/x-www-form-urlencoded", "multipart/form-data"})
    @ResponseBody
    public AuthenticationResult register(@RequestParam(value = "username", required = false, defaultValue = "") String username,
                                         @RequestParam(value = "email", required = false, defaultValue = "") String email,
                                         @RequestParam(value = "password", required = false, defaultValue = "") String password,
                                         @RequestParam(value = "password-confirmation", required = false, defaultValue = "") String passwordConfirm) {
        try {
            authentication.register(username, email, password, passwordConfirm);
            return new AuthenticationResult(true, "Successfully");
        } catch (IllegalArgumentException e) {
            return new AuthenticationResult(false, e.getMessage());
        }
    }

    @RequestMapping(value = {"/logout", "/sign-out"})
    @ResponseBody
    public AuthenticationResult logout(@RequestHeader(value = "Authorization", required = false, defaultValue = "") String authorization) {
        //TODO: implement
        return new AuthenticationResult(true, "Successfully");
    }
}
