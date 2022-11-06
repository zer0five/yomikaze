package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.yomikaze.web.dto.form.account.EmailForm;
import org.yomikaze.web.dto.form.account.ResetPasswordForm;
import org.yomikaze.web.dto.form.account.VerifyForm;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
@PreAuthorize("authentication == null || anonymous")
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
            return "views/account/verify";
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
    public String resendVerification(@ModelAttribute EmailForm emailForm) {
        return "views/account/resend-verification";
    }

    @PostMapping("/verify/resend")
    public String resendVerification(@Validated @ModelAttribute EmailForm emailForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
            return "views/account/resend-verification";
        }
        Account account = accountRepository.findByEmail(emailForm.getEmail())
            .orElseThrow(EntityNotFoundException::new);
        if (account.isVerified()) {
            bindingResult.rejectValue("email", "email.already-verified", "Account is already verified");
            log.info("Account is already verified");
            return "views/account/resend-verification";
        }
        accountService.sendVerificationEmail(account);
        bindingResult.reject("email.sent", "Verification email sent");
        return "views/account/resend-verification";
    }

    @GetMapping("/password/forgot")
    public String resetPassword(@ModelAttribute EmailForm resetPasswordForm) {
        return "views/account/forgot-password";
    }

    @PostMapping("/password/forgot")
    public String resetPassword(@Validated @ModelAttribute EmailForm resetPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
            return "views/account/forgot-password";
        }
        Account account = accountRepository.findByEmail(resetPasswordForm.getEmail())
            .orElseThrow(EntityNotFoundException::new);
        accountService.sendResetPasswordEmail(account);
        return "redirect:/login?reset";
    }

    @GetMapping("/password/reset")
    public String resetPassword(@ModelAttribute ResetPasswordForm resetPasswordForm, Optional<String> token) {
        token.ifPresent(resetPasswordForm::setToken);
        return "views/account/reset-password";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@Validated @ModelAttribute ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        boolean reset = false;
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
            return "views/account/reset-password";
        }
        try {
            accountService.resetPassword(resetPasswordForm.getToken(), resetPasswordForm.getPassword());
            reset = true;
        } catch (AccountExpiredException e) {
            bindingResult.rejectValue("token", "token.expired", "Token has expired");
            log.info("Token has expired");
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("token", "token.invalid", "Token is invalid");
            log.info("Invalid token {}", resetPasswordForm.getToken());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("token", "token.id.not-found", "Token with given id not found");
            log.info("Token with id {} not found", resetPasswordForm.getToken());
        }
        return reset ? "redirect:/login?reset" : "views/account/reset-password";
    }
}
