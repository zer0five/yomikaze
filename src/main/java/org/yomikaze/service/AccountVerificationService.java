package org.yomikaze.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;
import org.yomikaze.snowflake.Snowflake;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountVerificationService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AccountRepository accountRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;
    @Value("${yomikaze.base-url}")
    private String baseUrl;

    @Value("#{T(java.time.Duration).parse('PT${yomikaze.account.verification.expire:24h}')}")
    private Duration tokenDuration = Duration.ofHours(24);

    private String generateToken(Account account) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .expiresAt(Instant.now().plus(tokenDuration))
            .id(account.getId().toString())
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void sendVerificationEmail(Account account) {
        log.info("Verification for {}", account.getEmail());
        String token = generateToken(account);
        log.info("Generated token {}", token);
        final Context ctx = new Context(Locale.US);
        ctx.setVariable("token", token);
        ctx.setVariable("account", account);
        ctx.setVariable("url", baseUrl + "/account/verify/" + token);
        emailTemplateEngine.getTemplateResolvers().forEach(resolver -> log.info("Resolver: {}", resolver));
        final String htmlContent = emailTemplateEngine.process("verification", ctx);
        if (htmlContent.equals("verification")) {
            log.error("Template not found");
            return;
        }
        log.info("Content: {}", htmlContent);
        final MimeMailMessage message = new MimeMailMessage(mailSender.createMimeMessage());
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message.getMimeMessage(), true);
            helper.setTo(account.getEmail());
            helper.setSubject("Account Verification");
            helper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to create email message", e);
        }
        log.info("Sending email to {}", account.getEmail());
        try {
            mailSender.send(message.getMimeMessage());
        } catch (MailAuthenticationException e) {
            log.error("Authentication failed for email account: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
        }
    }

    public boolean verifyAccount(String token) {
        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(token);
        } catch (JwtException e) {
            return false;
        }
        Snowflake id = Snowflake.of(jwt.getId());
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) return false;
        if (account.isVerified()) return false;
        account.setVerified(true);
        accountRepository.save(account);
        return true;
    }
}
