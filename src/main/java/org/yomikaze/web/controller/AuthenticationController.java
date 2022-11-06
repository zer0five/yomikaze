package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.service.AccountService;
import org.yomikaze.service.AuthenticationService;
import org.yomikaze.service.RedirectService;
import org.yomikaze.web.dto.form.account.SignInForm;
import org.yomikaze.web.dto.form.account.SignUpForm;

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
    private final AccountService verificationService;

    @GetMapping({"/login", "/sign-in"})
    public String login(@ModelAttribute SignInForm signInForm,
                        @RequestHeader(HttpHeaders.REFERER) Optional<URI> referer,
                        HttpSession session) {
        redirectService.storeRedirect(session, referer);
        return "views/auth/sign-in";
    }

    @PostMapping({"/login", "/sign-in"})
    public String login(@ModelAttribute @Validated SignInForm signInForm,
                        BindingResult bindingResult,
                        HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "views/auth/sign-in";
        }
        try {
            authenticationService.login(signInForm);
        } catch (DisabledException e) {
            bindingResult.rejectValue(null, "account.unverified");
            return "views/auth/sign-in";
        } catch (LockedException e) {
            bindingResult.rejectValue(null, "account.banned");
            return "views/auth/sign-in";
        } catch (BadCredentialsException e) {
            bindingResult.rejectValue("password", "login.password.mismatch");
            return "views/auth/sign-in";
        }
        return redirectService.getRedirectSpring(session);
    }

    @GetMapping({"/register", "/sign-up"})
    public String register(@ModelAttribute SignUpForm signUpForm,
                           @RequestHeader(HttpHeaders.REFERER) Optional<URI> referer,
                           HttpSession session) {
        redirectService.storeRedirect(session, referer);
        return "views/auth/sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    public String register(@Validated @ModelAttribute SignUpForm signUpForm,
                           BindingResult bindingResult,
                           HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "views/auth/sign-up";
        }
        Account account = authenticationService.register(signUpForm);
        verificationService.sendVerificationEmail(account);
        return redirectService.getRedirectSpring(session);
    }


}
