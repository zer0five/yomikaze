package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.web.dto.RegistrationData;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;

    public void register(RegistrationData registrationData) {
        Account account = new Account();
        account.setUsername(registrationData.getUsername());
        account.setPassword(passwordEncoder.encode(registrationData.getPassword()));
        account.setEmail(registrationData.getEmail());
        accountRepository.save(account);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(registrationData.getUsername(), registrationData.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
