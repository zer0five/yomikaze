package io.gitlab.zeromatter.yomikaze.service;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Permission;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Role;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Log
public class YomikazeUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseGet(() -> accountRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
        Set<Role> roles = account.getRoles();
        Set<Permission> permissions = new LinkedHashSet<>();
        for (Role role : roles) {
            permissions.addAll(role.getPermissions());
        }
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();
        permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
        return new User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                authorities
        );
    }
}
