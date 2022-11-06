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
import org.yomikaze.web.dto.form.account.EmailForm;
import org.yomikaze.web.dto.form.account.ResetPasswordForm;
import org.yomikaze.web.dto.form.account.VerifyForm;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.Email;
import java.util.Optional;

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
        boolean valid = false;
        log.info("Verifying account with token {}", token);
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
        }
        try {
            accountService.verifyAccount(token);
            log.info("Account verified");
            valid = true;
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
        if (valid) {
            bindingResult.rejectValue("success", "success", "Account verified");
        }
        return "views/account/verify";
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

    @GetMapping("/password/forgot")
    public String forgotPassword(@ModelAttribute EmailForm emailForm) {
        return "views/account/forgot-password";
    }

    @PostMapping("/password/forgot")
    public String forgotPassword(@Validated @ModelAttribute EmailForm emailForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
            return "views/account/forgot-password";
        }
        Account account = accountRepository.findByEmail(emailForm.getEmail())
            .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        accountService.sendPasswordResetEmail(account);
        bindingResult.reject("email.sent", "Password reset email sent");
        return "views/account/forgot-password";
    }

    @GetMapping("/password/reset")
    public String resetPassword(@ModelAttribute ResetPasswordForm resetPasswordForm, Optional<String> token) {
        token.ifPresent(resetPasswordForm::setToken);
        return "views/account/reset-password";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@Validated @ModelAttribute ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        String token = resetPasswordForm.getToken().trim();
        String password = resetPasswordForm.getPassword().trim();
        boolean valid = false;
        log.info("Resetting password with token {}", token);
        if (bindingResult.hasErrors()) {
            log.info("Validation errors: {}", bindingResult);
        }
        try {
            accountService.resetPassword(token, password);
            log.info("Password reset");
            valid = true;
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
        if (valid) {
            bindingResult.reject("password.reset", "Password reset");
        }
        return "views/account/reset-password";
    }

    public String manageAccount() {
        return "views/account/account-list";
    }

}
