package org.yomikaze.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
public class AccountController {
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @GetMapping("/verify")
    @PreAuthorize("authentication == null || anonymous")
    public String verify(@ModelAttribute VerifyForm verifyForm) {
        return "views/account/verify";
    }

    @GetMapping(value = "/verify", params = "token")
    @PreAuthorize("authentication == null || anonymous")
    public String verify(@Validated @ModelAttribute VerifyForm verifyForm, BindingResult bindingResult, Model model) {
        String token = verifyForm.getToken().trim();
        if (bindingResult.hasErrors()) {
            return "views/account/verify";
        }
        try {
            accountService.verifyAccount(token);
            model.addAttribute("success", true);
            bindingResult.reject("account.verified", "Account verified");
        } catch (AccountExpiredException e) {
            bindingResult.rejectValue("token", "token.expired", "Token has expired");
            log.info("Token has expired");
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("token", "token.invalid", "Token is invalid");
            log.info("Invalid token {}", verifyForm.getToken());
        } catch (EntityNotFoundException e) {
            bindingResult.rejectValue("token", "token.id.not-found", "Token with given id not found");
            log.info("Token with id {} not found", verifyForm.getToken());
        } catch (IllegalStateException e) {
            bindingResult.reject("account.already-verified", "Account already verified");
        }
        return "views/account/verify";
    }

    @GetMapping("/verify/resend")
    @PreAuthorize("authentication == null || anonymous")
    public String resendVerification(@ModelAttribute EmailForm emailForm) {
        return "views/account/resend-verification";
    }

    @PostMapping("/verify/resend")
    @PreAuthorize("authentication == null || anonymous")
    public String resendVerification(@Validated @ModelAttribute EmailForm emailForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "views/account/resend-verification";
        }
        Account account = accountRepository.findByEmail(emailForm.getEmail()).orElse(null);
        if (account == null) {
            bindingResult.rejectValue("email", "email.not-found", "Email not found");
            return "views/account/resend-verification";
        }
        if (account.isVerified()) {
            bindingResult.reject("account.already-verified", "Account already verified");
            return "views/account/resend-verification";
        }
        accountService.sendVerificationEmail(account);
        model.addAttribute("success", true);
        bindingResult.reject("verify.email.resent", "Verification email has been resent");
        return "views/account/resend-verification";
    }

    @GetMapping("/password/forgot")
    @PreAuthorize("authentication == null || anonymous")
    public String forgotPassword(@ModelAttribute EmailForm emailForm) {
        return "views/account/forgot-password";
    }

    @PostMapping("/password/forgot")
    @PreAuthorize("authentication == null || anonymous")
    public String forgotPassword(@Validated @ModelAttribute EmailForm emailForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "views/account/forgot-password";
        }
        Account account = accountRepository.findByEmail(emailForm.getEmail()).orElse(null);
        if (account == null) {
            bindingResult.rejectValue("email", "email.not-found", "Email not found");
            return "views/account/forgot-password";
        }
        accountService.sendPasswordResetEmail(account);
        bindingResult.reject("forgot-password.email.sent", "Password reset email sent");
        return "views/account/forgot-password";
    }

    @GetMapping("/password/reset")
    @PreAuthorize("authentication == null || anonymous")
    public String resetPassword(@ModelAttribute ResetPasswordForm resetPasswordForm, Optional<String> token) {
        token.ifPresent(resetPasswordForm::setToken);
        return "views/account/reset-password";
    }

    @PostMapping("/password/reset")
    @PreAuthorize("authentication == null || anonymous")
    public String resetPassword(@Validated @ModelAttribute ResetPasswordForm resetPasswordForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "views/account/reset-password";
        }
        try {
            accountService.resetPassword(resetPasswordForm);
            bindingResult.reject("password.reset.success", "Password reset, now you can login with your new password");
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
        return "views/account/reset-password";
    }

    @PostAuthorize("hasAuthority('account.manage')")
    @GetMapping("/manage")
    public String manageAccount(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "views/account/account-list";
    }

}
