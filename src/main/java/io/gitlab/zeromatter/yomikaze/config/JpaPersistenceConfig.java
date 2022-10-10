package io.gitlab.zeromatter.yomikaze.config;

import com.zaxxer.hikari.HikariDataSource;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Comic;
import io.gitlab.zeromatter.yomikaze.persistence.entity.Genre;
import io.gitlab.zeromatter.yomikaze.persistence.repository.ComicRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.GenreRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.PermissionRepository;
import io.gitlab.zeromatter.yomikaze.persistence.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
@EnableJpaRepositories(basePackages = "io.gitlab.zeromatter.yomikaze.persistence.repository")
@Slf4j
public class JpaPersistenceConfig {
    boolean isInitialized = false;

    @Bean
    @Primary
    @Profile("heroku")
    public DataSource primaryDataSource() throws URISyntaxException {
        log.info("Using heroku datasource");
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        if (jdbcUrl != null) {
            return DataSourceBuilder.create()
                    .type(HikariDataSource.class)
                    .url(jdbcUrl)
                    .build();
        }
        String databaseUrl = Optional.ofNullable(System.getenv("DATABASE_URL"))
                .orElseThrow(() -> new IllegalArgumentException("DATABASE_URL is not set"));
        URI uri = new URI(databaseUrl);
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

    @Bean
    public boolean initData(GenreRepository genreRepository, ComicRepository comicRepository, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        if (isInitialized) {
            return true;
        }
        String[] genresStr = new String[]{
                "Action", "Adventure", "Comedy", "Drama", "Fantasy", "Horror", "Mystery", "Psychological", "Romance", "Sci-Fi", "Slice of Life", "Supernatural", "Thriller"
        };
        Map<String, Genre> genres = new HashMap<>();
        for (String genre : genresStr) {
            Optional<Genre> og = genreRepository.findByName(genre);
            Genre g;
            if (og.isPresent()) {
                g = og.get();
            } else {
                g = new Genre();
                g.setName(genre);
                g = genreRepository.save(g);
            }
            genres.put(genre, g);
        }

        String[] titles = new String[]{
                "Comic 001", "Comic 2"
        };
        String[][] comicGenres = new String[][]{
                {"Action", "Adventure"},
                {"Action", "Adventure", "Drama"}
        };
        for (int i = 0; i < titles.length; i++) {
            Comic comic = new Comic();
            comic.setTitle(titles[i]);

            for (String genre : comicGenres[i]) {
                comic.getGenres().add(genres.get(genre));
            }
            comicRepository.save(comic);
        }
        log.info("Data initialized");
        Set<Genre> whitelist = new HashSet<>();
        whitelist.add(genres.get("Action"));
        whitelist.add(genres.get("Adventure"));
        Set<Genre> blacklist = new HashSet<>(whitelist);
        blacklist.add(genres.get("Drama"));
        Set<Comic> filtered = comicRepository.findByGenresContainsAllAndNone(whitelist, blacklist);
        log.info("Filtered: " + filtered.stream().map(Comic::getTitle).collect(Collectors.joining(", ")));
        isInitialized = true;
        return true;
    }
}
