package io.gitlab.zeromatter.yomikaze;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableCaching
@Profile("heroku")
@EnableRedisRepositories
public class RedisConfig {

    @Profile("heroku")
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();

        cacheNamesConfigurationMap.put("TimeZone", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)));
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)
                .withInitialCacheConfigurations(cacheNamesConfigurationMap)
                .build();

    }

    @Profile("heroku")
    @Bean
    public RedisConnectionFactory redisConnectionFactory(Environment environment) {
        String redisUrl = environment.getProperty("REDIS_URL", "");
        if (redisUrl.isEmpty()) {
            log.info("REDIS_URL is not set, not using redis");
            return null;
        }
        log.info("REDIS_URL is set to " + redisUrl);

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisUrl));
    }

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return clientConfigurationBuilder -> {
            if (clientConfigurationBuilder.build().isUseSsl()) {
                clientConfigurationBuilder.useSsl().disablePeerVerification();
            }
        };
    }
}
