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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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

    @GetMapping({"/register", "/sign-up"})
    public String register(@RequestHeader("referer") Optional<URI> referer, HttpSession session) {
        referer.ifPresent(uri -> session.setAttribute("redirect", uri));
        return "sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    public ResponseEntity<Object> register(@Valid RegistrationData registration, @SessionAttribute("redirect") Optional<URI> redirect, HttpSession session, HttpServletRequest request) throws EntityExistsException {
        authenticationService.register(registration);
        URI uri = redirect.orElseGet(() -> {
            if (request.getContextPath().isEmpty()) {
                return URI.create("/");
            } else {
                return URI.create(request.getContextPath());
            }
        });
        redirect.ifPresent(u -> session.removeAttribute("redirect"));
        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();
    }

    @ExceptionHandler({EntityExistsException.class})
    protected ResponseEntity<Object> handleEntityExistsException(EntityExistsException e, HttpServletRequest request) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(GenericResponse
                .builder()
                .status(false)
                .message("Username or email already exists")
                .build()
            );
    }

}
