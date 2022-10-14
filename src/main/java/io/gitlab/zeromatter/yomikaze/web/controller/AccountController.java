package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import io.gitlab.zeromatter.yomikaze.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@Controller
@PreAuthorize("authentication != null && isAuthenticated()")
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository accountRepository;

    @RequestMapping({"/info/@{id}", "/info/{id}", "/info"})
    @ResponseBody
    public ResponseEntity<Account> getUser(@PathVariable(value = "id") Optional<String> oid, Authentication authentication) {
        Optional<Account> account;
        String id = oid.orElse("me");
        if (id.equalsIgnoreCase("me")) {
            String username = authentication.getName();
            account = accountRepository.findByUsernameOrEmail(username, username);
        } else {
            Snowflake uid = Snowflake.of(id);
            account = accountRepository.findById(uid);
        }
        return account.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
