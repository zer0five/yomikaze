package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.web.dto.Registration;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;

    public Account register(@Validated Registration registration) {
        return register(registration, true);
    }

    public Account register(@Validated Registration registration, boolean autoLogin) {
        final String username = registration.getUsername();
        final String email = registration.getEmail();
        final String rawPassword = registration.getPassword();
        final String password = passwordEncoder.encode(rawPassword);
        Account account = new Account();
        account.setEmail(email);
        account.setUsername(username);
        account.setPassword(password);
        account = accountRepository.save(account);
        if (autoLogin) login(username, rawPassword);
        return account;
    }

    public void login(String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
