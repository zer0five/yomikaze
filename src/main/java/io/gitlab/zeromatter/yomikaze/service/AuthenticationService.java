package io.gitlab.zeromatter.yomikaze.service;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.web.dto.RegistrationData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final AuthenticationManager authenticationManager;

    public void register(RegistrationData registrationData) {
        if (accountRepository.existsByUsernameOrEmail(registrationData.getUsername(), registrationData.getEmail())) {
            throw new EntityExistsException("Username or email already exists");
        }
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
