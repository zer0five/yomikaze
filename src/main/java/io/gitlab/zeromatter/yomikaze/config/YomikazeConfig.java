package io.gitlab.zeromatter.yomikaze.config;

import io.gitlab.zeromatter.yomikaze.task.WakeMyDyno;
import lombok.extern.java.Log;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Log
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
    public PasswordEncoder passwordEncoder() {
        int saltLength = Optional.ofNullable(System.getenv("ARGON2_SALT_LENGTH")).map(Integer::parseInt).orElse(1 << 4);
        int hashLength = Optional.ofNullable(System.getenv("ARGON2_HASH_LENGTH")).map(Integer::parseInt).orElse(1 << 5);
        int parallelism = Optional.ofNullable(System.getenv("ARGON2_PARALLELISM")).map(Integer::parseInt).orElse(1);
        int memory = Optional.ofNullable(System.getenv("ARGON2_MEMORY")).map(Integer::parseInt).orElse(1 << 16);
        int iterations = Optional.ofNullable(System.getenv("ARGON2_ITERATIONS")).map(Integer::parseInt).orElse(1 << 1 | 1);
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    @Bean
    @DependsOn("debug")
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
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames("classpath:/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

}
