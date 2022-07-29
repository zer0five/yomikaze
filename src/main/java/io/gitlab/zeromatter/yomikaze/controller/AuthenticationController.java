package io.gitlab.zeromatter.yomikaze.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import io.gitlab.zeromatter.yomikaze.dto.AuthenticationResult;
import io.gitlab.zeromatter.yomikaze.entity.account.Account;
import io.gitlab.zeromatter.yomikaze.entity.account.AccountRepository;
import io.gitlab.zeromatter.yomikaze.entity.session.Session;
import io.gitlab.zeromatter.yomikaze.entity.session.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AccountRepository accountRepository;
    private final Argon2PasswordEncoder passwordEncoder;
    private final Algorithm jwtSigningAlgorithm;

    private final SessionRepository sessionRepository;

    private final boolean debug;

    @PostMapping(value = {"/login", "/sign-in"}, consumes = {"application/json", "application/x-www-form-urlencoded", "multipart/form-data"})
    @ResponseBody
    public AuthenticationResult login(@RequestParam(value = "username-or-email", required = false, defaultValue = "") String usernameOrEmail,
                                      @RequestParam(value = "password", required = false, defaultValue = "") String password) {
        if (usernameOrEmail.isEmpty()) {
            return new AuthenticationResult(false, "Username or email is required");
        }
        if (password.isEmpty()) {
            return new AuthenticationResult(false, "Password is required");
        }
        Optional<Account> accountOptional = accountRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (!accountOptional.isPresent()) {
            return new AuthenticationResult(false, "Account not found");
        }
        Account account = accountOptional.get();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            return new AuthenticationResult(false, "Invalid password");
        }
        String token = generateToken(account);
        return new AuthenticationResult(true, "Successfully", token);
    }

    private String generateToken(Account account) {
        String token = JWT.create()
                .withIssuedAt(Instant.now())
                .withClaim("id", account.getId())
                .withClaim("username", account.getUsername())
                .withClaim("email", account.getEmail())
                .withClaim("twoFactorEnabled", account.isTwoFactorEnabled())
                .sign(jwtSigningAlgorithm);
        Session session = new Session();
        session.setToken(token);
        session.setAccount(account);
        sessionRepository.save(session);
        return token;
    }

    @PostMapping(value = {"/register", "/sign-up"}, consumes = {"application/json", "application/x-www-form-urlencoded", "multipart/form-data"})
    @ResponseBody
    public AuthenticationResult register(@RequestParam(value = "username", required = false, defaultValue = "") String username,
                                         @RequestParam(value = "email", required = false, defaultValue = "") String email,
                                         @RequestParam(value = "password", required = false, defaultValue = "") String password,
                                         @RequestParam(value = "password_confirmation", required = false, defaultValue = "") String passwordConfirm) {
        if (password.isEmpty() || passwordConfirm.isEmpty()) {
            return new AuthenticationResult(false, "Password is required");
        }

        if (!password.equals(passwordConfirm)) {
            return new AuthenticationResult(false, "Passwords do not match");
        }

        if (username.isEmpty()) {
            return new AuthenticationResult(false, "Username is required");
        }

        if (email.isEmpty()) {
            return new AuthenticationResult(false, "Email is required");
        }

        if (accountRepository.existsByUsername(username)) {
            return new AuthenticationResult(false, "Username already exists");
        }

        if (accountRepository.existsByEmail(email)) {
            return new AuthenticationResult(false, "Email already exists");
        }

        Account account = new Account();
        account.setUsername(username);
        account.setEmail(email);
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);

        return new AuthenticationResult(true, "Successfully");
    }

    @RequestMapping(value = {"/logout", "/sign-out"})
    @ResponseBody
    public AuthenticationResult logout(@RequestHeader(value = "Authorization", required = false, defaultValue = "") String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            return new AuthenticationResult(false, "Unauthenticated");
        }
        String token = authorization.substring("Bearer ".length());
        sessionRepository
                .findById(token)
                .ifPresent(sessionRepository::delete);
        return new AuthenticationResult(true, "Successfully");
    }

    private boolean verify(String token) {
        try {
            JWT.require(jwtSigningAlgorithm).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean check(String token) {
        return sessionRepository.findById(token).isPresent();
    }

    @RequestMapping(value = {"/verify-token", "/verify-session"})
    @ResponseBody
    public AuthenticationResult verifyToken(@RequestHeader(value = "Authorization", required = false, defaultValue = "") String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            return new AuthenticationResult(false, "Authorization header is required");
        }
        String token = authorization.substring("Bearer ".length());

        if (verify(token)) {
            return new AuthenticationResult(true, "OK");
        } else {
            return new AuthenticationResult(false, "Invalid token");
        }
    }

    @RequestMapping(value = {"/check-token", "/check-session"})
    @ResponseBody
    public AuthenticationResult checkToken(@RequestHeader(value = "Authorization", required = false, defaultValue = "") String authorization) {
        if (!authorization.startsWith("Bearer ")) {
            return new AuthenticationResult(false, "Authorization header is required");
        }
        String token = authorization.substring("Bearer ".length());

        if (check(token)) {
            Optional<Session> session = sessionRepository.findById(authorization);
            if (session.isPresent()) {
                return new AuthenticationResult(true, "Successfully");
            }
        }
        return new AuthenticationResult(false, "Invalid token");
    }

}
