package org.yomikaze.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Slf4j
@Configuration
@EnableCaching
@Profile("heroku")
@EnableRedisHttpSession
public class RedisConfig {

    @Bean
    @Profile("heroku")
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    @Profile("heroku")
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return clientConfigurationBuilder -> {
            if (clientConfigurationBuilder.build().isUseSsl()) {
                clientConfigurationBuilder.useSsl().disablePeerVerification();
            }
        };
    }

}
