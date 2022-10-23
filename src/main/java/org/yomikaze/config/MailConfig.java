package org.yomikaze.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
@Data
@Slf4j
public class MailConfig {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttls;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private String starttlsRequired;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.smtp.starttls.required", starttlsRequired);
        return mailSender;
    }

    @Bean
    public MessageSource emailMessageSource() {
        final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/languages/mail/messages", "./languages/mail/messages");
        return messageSource;
    }

    @Bean
    public TemplateEngine emailTemplateEngine(ApplicationContext applicationContext) {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver(applicationContext));
        templateEngine.setTemplateEngineMessageSource(emailMessageSource());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    private ITemplateResolver emailTemplateResolver(ApplicationContext applicationContext) {
        SpringResourceTemplateResolver emailTemplateResolver = new SpringResourceTemplateResolver();
        emailTemplateResolver.setOrder(1);
        emailTemplateResolver.setPrefix("classpath:/templates/mail/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode(TemplateMode.HTML);
        emailTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        emailTemplateResolver.setCacheable(false);
        emailTemplateResolver.setApplicationContext(applicationContext);
        return emailTemplateResolver;
    }


}
