package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.service.AccountVerificationService;
import org.yomikaze.service.AuthenticationService;
import org.yomikaze.service.RedirectService;
import org.yomikaze.web.dto.LoginInfo;
import org.yomikaze.web.dto.Registration;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Optional;

@Log
@Controller
@RequiredArgsConstructor
@PreAuthorize("authentication == null || anonymous")
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RedirectService redirectService;
    private final AccountVerificationService verificationService;

    @RequestMapping({"/login", "/sign-in"})
    public String login(@ModelAttribute LoginInfo loginInfo,
                        @RequestHeader(HttpHeaders.REFERER) Optional<URI> referer,
                        HttpSession session) {
        redirectService.storeRedirect(session, referer);
        return "views/auth/sign-in";
    }

    @GetMapping({"/register", "/sign-up"})
    public String register(@ModelAttribute Registration registration,
                           @RequestHeader(HttpHeaders.REFERER) Optional<URI> referer,
                           HttpSession session) {
        redirectService.storeRedirect(session, referer);
        return "views/auth/sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    public String register(@Validated @ModelAttribute Registration registration,
                           BindingResult bindingResult,
                           HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "views/auth/sign-up";
        }
        Account account = authenticationService.register(registration);
        verificationService.sendVerificationEmail(account);
        return redirectService.getRedirectSpring(session);
    }


}
