package io.gitlab.zeromatter.yomikaze.config;

import io.gitlab.zeromatter.yomikaze.service.YomikazeUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Log
@EnableCaching
@Configuration
@EnableJdbcHttpSession
@RequiredArgsConstructor
public class SecurityConfig {

    private final YomikazeUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(userDetailsService)
                .anonymous(anonymous -> anonymous
                        .principal("anonymous")
                        .authorities("ANONYMOUS")
                )
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/login", "/sign-in").hasAuthority("ANONYMOUS")
                        .antMatchers("/register", "/sign-up").hasAuthority("ANONYMOUS")
                        .antMatchers("/api/v1/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(basic -> basic.realmName("yomikaze"))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().changeSessionId()
                        .maximumSessions(1)
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .failureHandler((request, response, exception) -> {
                            request.setAttribute("error", "authentication.login.unknown");
                            request.getRequestDispatcher("/login?error").forward(request, response);
                        })
                )
                .rememberMe(rememberMe -> rememberMe
                        .tokenValiditySeconds(60 * 3600 * 24 * 7)
                        .userDetailsService(userDetailsService)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .deleteCookies("YOMIKAZE_SESSION", "token", "JSESSIONID")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/")
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied.html")
                )
                .build();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieName("YOMIKAZE_SESSION");
        defaultCookieSerializer.setUseHttpOnlyCookie(true);
        defaultCookieSerializer.setSameSite("Strict");
        return defaultCookieSerializer;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
