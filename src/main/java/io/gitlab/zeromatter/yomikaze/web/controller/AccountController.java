package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Log
public class AccountController {
    private final AccountRepository accountRepository;

    @RequestMapping("/info/@{id}")
    @ResponseBody
    public Map<String, Object> getUser(@PathVariable(value = "id") String id, HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        Optional<Account> account;
        if (id.equalsIgnoreCase("me")) {
            account = accountRepository.findByUsername(username);
        } else {
            long userId;
            if (id.startsWith("-")) {
                userId = Long.parseLong(id);
            } else {
                userId = Long.parseUnsignedLong(id);
            }
            account = accountRepository.findById(userId);
        }
        return account.<Map<String, Object>>map(value -> Collections.singletonMap("user", value)).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
