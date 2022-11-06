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
import org.yomikaze.web.dto.form.account.SignInForm;
import org.yomikaze.web.dto.form.account.SignUpForm;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;

    public Account register(@Validated SignUpForm signUpForm) {
        final String username = signUpForm.getUsername();
        final String email = signUpForm.getEmail();
        final String rawPassword = signUpForm.getPassword();
        final String password = passwordEncoder.encode(rawPassword);
        Account account = new Account();
        account.setEmail(email);
        account.setUsername(username);
        account.setPassword(password);
        account = accountRepository.save(account);
        return account;
    }

    public void login(SignInForm signInForm) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
