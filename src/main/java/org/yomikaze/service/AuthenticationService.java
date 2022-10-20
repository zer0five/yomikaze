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
        Account account = new Account();
        account.setUsername(registration.getUsername());
        String rawPassword = registration.getPassword();
        String password = passwordEncoder.encode(rawPassword);
        account.setPassword(password);
        account.setEmail(registration.getEmail());
        return accountRepository.save(account);
    }

    public void authenticate(Account account, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account.getUsername(), password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
