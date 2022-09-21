package io.gitlab.zeromatter.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log
@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    @RequestMapping({"/login", "/sign-in"})
    public String login() {
        return "sign-in";
    }

    @GetMapping({"/register", "/sign-up"})
    public String register() {
        return "sign-up";
    }

    @PostMapping({"/register", "/sign-up"})
    public String register(String username, String password, String email) {
        System.out.println(username);
        System.out.println(password);
        System.out.println(email);
        return "redirect:/";
    }

}
