package io.gitlab.zeromatter.yomikaze.config;

import io.gitlab.zeromatter.yomikaze.config.properties.YomikazeProperties;
import io.gitlab.zeromatter.yomikaze.config.properties.YomikazeProperties.Security.Password.Encoder;
import io.gitlab.zeromatter.yomikaze.config.properties.YomikazeProperties.Security.Password.Encoder.PasswordEncoderType;
import io.gitlab.zeromatter.yomikaze.task.WakeMyDyno;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.gitlab.zeromatter.yomikaze.config.properties.YomikazeProperties.Security.Password.Encoder.Argon2;
import static io.gitlab.zeromatter.yomikaze.config.properties.YomikazeProperties.Security.Password.Encoder.BCrypt;

@Slf4j
@Configuration
@EnableCaching
@EnableScheduling
public class YomikazeConfig {
    @Bean
    public boolean debug() {
        boolean debug = System.getenv("DEBUG") != null;
        if (debug) log.info("Debug mode is enabled");
        return debug;
    }

    @Bean
    public PasswordEncoder passwordEncoder(YomikazeProperties properties) {
        Encoder encoder = properties.getSecurity().getPassword().getEncoder();
        PasswordEncoderType encoderType = encoder.getType();
        switch (encoderType) {
            case ARGON2:
                Argon2 argon2 = encoder.getArgon2();
                return argon2.get();
            case BCRYPT:
                BCrypt bcrypt = encoder.getBcrypt();
                return bcrypt.get();
            default:
                throw new IllegalArgumentException("Unknown password encoder type: " + encoderType);
        }
    }

    @Bean
    @DependsOn("debug")
    @Profile("heroku")
    public WakeMyDyno wakeMyDyno(boolean debug) {
        String url = System.getenv("DYNO_URL");
        if (url == null) {
            if (debug) {
                String host = "http://127.0.0.1";
                String port = System.getProperty("server.port");
                if (port == null) port = System.getenv("PORT");
                if (port == null) port = "8080";
                url = host + ":" + port;
            } else {
                log.info("DYNO_URL is not set, and debug mode is disabled, not scheduling wakeMyDyno");
                return null;
            }
        }
        url += "/wake";
        log.info("WakeMyDyno URL is set to " + url);
        return new WakeMyDyno(url);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/languages/messages", "./languages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
