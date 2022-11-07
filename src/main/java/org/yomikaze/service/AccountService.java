package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.snowflake.Snowflake;
import org.yomikaze.web.dto.form.account.ChangePasswordForm;
import org.yomikaze.web.dto.form.account.ResetPasswordForm;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;

    private final UserDetailsPasswordService userDetailsPasswordService;
    @Value("${yomikaze.base-url}")
    private String baseUrl;

    @Value("#{T(java.time.Duration).parse('PT${yomikaze.account.verification.expire:24h}')}")
    private Duration tokenDuration = Duration.ofHours(24);

    private String generateVerifyToken(Account account) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .expiresAt(Instant.now().plus(tokenDuration))
            .subject("verify")
            .id(account.getId().toString())
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateResetToken(Account account) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .expiresAt(Instant.now().plus(tokenDuration))
            .subject("reset")
            .id(account.getId().toString())
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void sendVerificationEmail(Account account) {
        log.info("Verification for {}", account.getEmail());
        String token = generateVerifyToken(account);
        log.info("Generated token {}", token);

        final Context ctx = new Context(Locale.US);
        ctx.setVariable("token", token);
        ctx.setVariable("account", account);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        builder.path("/account/verify");
        builder.queryParam("token", token);
        final URI verifyUrl = builder.build().toUri();
        ctx.setVariable("url", verifyUrl);

        sendMail(ctx, "verification", "Yomikaze Account Verification", account);
    }

    public void sendPasswordResetEmail(Account account) {
        log.info("Reset for {}", account.getEmail());
        String token = generateResetToken(account);
        log.info("Generated token {}", token);

        final Context ctx = new Context(Locale.US);
        ctx.setVariable("token", token);
        ctx.setVariable("account", account);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(baseUrl);
        builder.path("/account/reset");
        builder.queryParam("token", token);
        final URI verifyUrl = builder.build().toUri();
        ctx.setVariable("url", verifyUrl);

        sendMail(ctx, "reset-password", "Yomikaze Reset Password", account);
    }

    private void sendMail(Context ctx, String template, String subject, Account account) {
        final String content = emailTemplateEngine.process(template, ctx);
        final MimeMailMessage message = new MimeMailMessage(mailSender.createMimeMessage());
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message.getMimeMessage(), true);
            helper.setTo(account.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to create email message", e);
        }
        log.info("Sending email to {}", account.getEmail());
        try {
            mailSender.send(message.getMimeMessage());
        } catch (MailAuthenticationException e) {
            log.error("Authentication failed for email account: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send email", e);
        }
    }

    public void verifyAccount(String token) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(token);
            if (!jwt.getSubject().equals("verify")) {
                throw new IllegalArgumentException("Invalid token");
            }
        } catch (JwtValidationException e) {
            throw new AccountExpiredException("Expired token", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
        Snowflake id = Snowflake.of(jwt.getId());
        log.info("Verifying account with id {}", id);
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        log.info("Verifying account {}", account);
        if (account.isVerified()) {
            throw new IllegalStateException("Account already verified");
        }
        account.setVerified(true);
        accountRepository.save(account);
        log.info("Account {} verified", account);
    }

    public void resetPassword(ResetPasswordForm form) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(form.getToken().trim());
            if (!jwt.getSubject().equals("reset")) {
                throw new IllegalArgumentException("Invalid token");
            }
        } catch (JwtValidationException e) {
            throw new AccountExpiredException("Expired token", e);
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
        Snowflake id = Snowflake.of(jwt.getId());
        log.info("Resetting password for account with id {}", id);
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        log.info("Resetting password for account {}", account);
        String password = passwordEncoder.encode(form.getPassword());
        account.setPassword(password);
        accountRepository.save(account);
        log.info("Password for account {} reset", account);
    }

    private final PasswordEncoder passwordEncoder;

    public void changePassword(Account account, ChangePasswordForm form) {
        if (!passwordEncoder.matches(form.getOldPassword(), account.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        userDetailsPasswordService.updatePassword(account, form.getNewPassword());
    }

    public void banAccount(Account account) {
        account.setLocked(true);
        accountRepository.save(account);
    }

    public void pardonAccount(Account account) {
        account.setLocked(false);
        accountRepository.save(account);
    }

    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }
}
