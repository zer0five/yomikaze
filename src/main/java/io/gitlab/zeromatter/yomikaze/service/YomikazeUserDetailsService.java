package io.gitlab.zeromatter.yomikaze.service;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Permission;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Role;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

@RequiredArgsConstructor
@Service
@Log
public class YomikazeUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseGet(() -> accountRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
        Collection<Role> roles = account.getRoles();
        Collection<Permission> permissions = new LinkedHashSet<>();
        for (Role role : roles) {
            permissions.addAll(role.getPermissions());
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .authorities(permissions)
                .build();
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Account account = accountRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        account.setPassword(newPassword);
        account = accountRepository.save(account);
        return new User(account.getUsername(), account.getPassword(), user.getAuthorities());
    }
}
