package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;

@RequiredArgsConstructor
@Service
@Log
public class YomikazeUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsernameOrEmail(username, username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Account account = accountRepository.findByUsername(user.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        account.setPassword(newPassword);
        account = accountRepository.save(account);
        return account;
    }
}
