package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.service.AccountService;
import org.yomikaze.validation.UsernameExistsConstraint;
import org.yomikaze.web.dto.form.VerifyForm;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Email;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @GetMapping("/verify")
    public String verify(@ModelAttribute VerifyForm verifyForm) {
        log.info("Displaying verify form");
        return "views/account/verify";
    }

    @GetMapping(value = "/verify", params = "token")
    public String verify(@Validated @ModelAttribute VerifyForm verifyForm, BindingResult bindingResult) {
        String token = verifyForm.getToken().trim();
        log.info("Verifying account with token {}", token);
        boolean verified = false;
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
        }
        try {
            accountService.verifyAccount(token);
            verified = true;
            log.info("Account verified");
        } catch (AccountExpiredException e) {
            bindingResult.rejectValue("token", "token.expired", "Token has expired");
            log.info("Token has expired");
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("token", "token.invalid", "Token is invalid");
            log.info("Invalid token {}", verifyForm.getToken());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("token", "token.id.not-found", "Token with given id not found");
            log.info("Token with id {} not found", verifyForm.getToken());
        }
        return verified ? "redirect:/login?verified" : "views/account/verify";
    }

    @GetMapping("/verify/resend")
    public String resendVerification() {
        return "views/account/resend";
    }

    @PostMapping("/verify/resend")
    public String resendVerification(@Email @UsernameExistsConstraint @Validated String email, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
            return "views/account/resend";
        }
        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        accountService.sendVerificationEmail(account);
        return "redirect:/login?resent";
    }

    public String manageAccount() {
        return "views/account/account-list";
    }

}
