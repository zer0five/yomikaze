package io.gitlab.zeromatter.yomikaze;

import com.auth0.jwt.algorithms.Algorithm;
import io.gitlab.zeromatter.yomikaze.task.WakeMyDyno;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Log
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class YomikazeConfiguration {
    public static final String DEFAULT_JWT_SECRET = "yomikaze-secret-with-a-long-enough-secret-to-be-used-as-a-jwt-secret";

    @Bean
    public boolean debug() {
        boolean debug = Optional.ofNullable(System.getenv("DEBUG"))
                .map(Boolean::parseBoolean)
                .orElse(false);
        if (debug) log.info("Debug mode is enabled");
        return debug;
    }

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        if (jdbcUrl != null) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl(jdbcUrl);
            return dataSource;
        }
        String envUrl = Optional.ofNullable(System.getenv("DATABASE_URL"))
                .orElseThrow(() -> new IllegalArgumentException("DATABASE_URL is not set"));
        URI uri = new URI(envUrl);
        String[] userInfo = uri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];
        String host = uri.getHost();
        int port = uri.getPort();
        String database = uri.getPath().substring(1);
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.type(BasicDataSource.class);
        builder.driverClassName("org.postgresql.Driver");
        builder.url("jdbc:postgresql://" + host + ":" + port + "/" + database);
        builder.username(username);
        builder.password(password);
        return builder.build();
    }

    @Bean
    public Argon2PasswordEncoder passwordEncoder() {
        int saltLength = Optional.ofNullable(System.getenv("ARGON2_SALT_LENGTH")).map(Integer::parseInt).orElse(1 << 4);
        int hashLength = Optional.ofNullable(System.getenv("ARGON2_HASH_LENGTH")).map(Integer::parseInt).orElse(1 << 5);
        int parallelism = Optional.ofNullable(System.getenv("ARGON2_PARALLELISM")).map(Integer::parseInt).orElse(1);
        int memory = Optional.ofNullable(System.getenv("ARGON2_MEMORY")).map(Integer::parseInt).orElse(1 << 16);
        int iterations = Optional.ofNullable(System.getenv("ARGON2_ITERATIONS")).map(Integer::parseInt).orElse(1 << 1 | 1);
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    @Bean
    public Algorithm jwtSigningAlgorithm() {
        return Algorithm.HMAC256(Optional.ofNullable(System.getenv("JWT_SECRET")).orElse(DEFAULT_JWT_SECRET));
    }

    @Bean
    public WakeMyDyno wakeMyDyno() {
        String url = System.getenv("DYNO_URL");
        if (url == null) {
            String host = "http://127.0.0.1";
            String port = System.getProperty("server.port");
            if (port == null) port = System.getenv("PORT");
            if (port == null) port = "8080";
            url = host + ":" + port;
        }
        url += "/wake";
        return new WakeMyDyno(url);
    }

}
