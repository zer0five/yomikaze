package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yomikaze.service.AuthenticationService;
import org.yomikaze.web.dto.RegistrationData;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Log
@Controller
@RequiredArgsConstructor
@PreAuthorize("authentication == null || isAnonymous()")
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private static final Set<String> NOT_TO_REDIRECT = new HashSet<>(Arrays.asList("/login", "/register", "/sign-in", "/sign-up"));

    @RequestMapping({"/login", "/sign-in"})
    public String login(@RequestHeader(HttpHeaders.REFERER) Optional<URI> referer, HttpSession session) {
        referer.filter(uri -> !NOT_TO_REDIRECT.contains(uri.getPath())).ifPresent(uri -> session.setAttribute("redirect", uri));
        return "sign-in";
    }

    @GetMapping({"/register", "/sign-up"})
    public String register(
        @ModelAttribute("registration") RegistrationData registration,
        @RequestHeader("referer") Optional<URI> referer, HttpSession session) {
        referer.filter(uri -> !NOT_TO_REDIRECT.contains(uri.getPath())).ifPresent(uri -> session.setAttribute("redirect", uri));
        return "sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    public String register(
        @Validated @ModelAttribute("registration") RegistrationData registration,
        BindingResult bindingResult,
        @SessionAttribute("redirect") Optional<URI> redirect,
        HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }
        authenticationService.register(registration);
        if (redirect.isPresent()) {
            session.removeAttribute("redirect");
            return "redirect:" + redirect.get();
        } else {
            return "redirect:/";
        }
    }


}
