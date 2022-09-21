package io.gitlab.zeromatter.yomikaze.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@Configuration
@EnableCaching
@EnableJpaRepositories(basePackages = "io.gitlab.zeromatter.yomikaze.persistence.repository")
public class JpaPersistenceConfig {
    @Bean
    @Primary
    public DataSource primaryDataSource() throws URISyntaxException {
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        if (jdbcUrl != null) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl(jdbcUrl);
            return dataSource;
        }
        String envUrl = Optional.ofNullable(System.getenv("DATABASE_URL"))
                .orElseThrow(() -> new IllegalArgumentException("DATABASE_URL is not set"));
        URI uri = new URI(envUrl);
        String userInfo = uri.getUserInfo();
        int userInfoSplitter = userInfo.indexOf(':');
        String username = userInfo.substring(0, userInfoSplitter);
        String password = userInfo.substring(userInfoSplitter + 1);
        String host = uri.getHost();
        int port = uri.getPort();
        String database = uri.getPath().substring(1);
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:postgresql://" + host + ":" + port + "/" + database)
                .username(username)
                .password(password)
                .build();
    }
}
