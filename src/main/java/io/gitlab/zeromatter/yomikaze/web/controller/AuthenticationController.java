package io.gitlab.zeromatter.yomikaze.web.controller;

import io.gitlab.zeromatter.yomikaze.persistence.entity.Account;
import io.gitlab.zeromatter.yomikaze.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Log
@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @RequestMapping({"/login", "/sign-in"})
    public String login() {
        return "sign-in";
    }

    @GetMapping({"/register", "/sign-up"})
    public String register() {
        return "sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    @ResponseBody
    public Map<String, Object> register(String username, String password, String email) {
        Account account;
        Map<String, Object> map = new HashMap<>();
        try {
            account = authenticationService.createAccount(username, password, email);
        } catch (Exception e) {
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
        map.put("success", true);
        map.put("message", "Account created");
        map.put("account", account);
        return map;
    }

}
