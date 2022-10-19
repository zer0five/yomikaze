package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.service.AuthenticationService;
import io.gitlab.zeromatter.yomikaze.web.dto.GenericResponse;
import io.gitlab.zeromatter.yomikaze.web.dto.RegistrationData;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Log
@Controller
@RequiredArgsConstructor
@PreAuthorize("authentication == null || isAnonymous()")
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @RequestMapping({"/login", "/sign-in"})
    public String login() {
        return "sign-in";
    }

//    @GetMapping({"/register", "/sign-up"})
//    public String register(RegistrationData registration, @RequestHeader("referer") Optional<URI> referer, HttpSession session) {
//        referer.ifPresent(uri -> session.setAttribute("redirect", uri));
//        return "sign-up";
//    }
//
//    @PostMapping({"/register", "/sign-up"})
//    public String register(@Valid RegistrationData registration, BindingResult bindingResult, @SessionAttribute("redirect") Optional<URI> redirect, HttpSession session, HttpServletRequest request) throws EntityExistsException {
//        if (bindingResult.hasErrors()) {
//            return "sign-up";
//        }
//        authenticationService.register(registration);
//        URI uri = redirect.orElseGet(() -> {
//            if (request.getContextPath().isEmpty()) {
//                return URI.create("/");
//            } else {
//                return URI.create(request.getContextPath());
//            }
//        });
//        redirect.ifPresent(u -> session.removeAttribute("redirect"));
//        return "redirect:" + uri;
//    }

    @GetMapping({"/register", "/sign-up"})
public String register(Model model) {
        model.addAttribute("registration", new RegistrationData());
        return "sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    public String register(@Valid RegistrationData registration, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "sign-up";
        }
        authenticationService.register(registration);
        return "redirect:/";
    }

}
