package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.service.AccountVerificationService;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountVerificationService accountVerificationService;

    @GetMapping("/verify")
    public String verify(Optional<String> token) {
        if (token.isPresent()) {
            accountVerificationService.verifyAccount(token.get());
            return "redirect:/login?verified";
        }
        return "views/account/verify";
    }

    @GetMapping("/resend-verification")
    public String resendVerification() {
        return "views/auth/resend-verification";
    }

}
