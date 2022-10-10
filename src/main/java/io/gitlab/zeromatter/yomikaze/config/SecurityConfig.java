package io.gitlab.zeromatter.yomikaze.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Log
@EnableCaching
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(userDetailsService)
                .anonymous(anonymous -> anonymous
                        .principal("anonymous")
                        .authorities("anonymous")
                )
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers("/login", "/sign-in").hasAuthority("anonymous")
                        .antMatchers("/register", "/sign-up").hasAuthority("anonymous")
                        .antMatchers("/api/v1/**").authenticated()
                        .antMatchers("/image/upload", "/image/upload/**").authenticated()
                        .anyRequest().permitAll()
                )
                .httpBasic(basic -> basic.realmName("yomikaze"))
                .sessionManagement(session -> session
                        .sessionAuthenticationFailureHandler((request, response, exception) -> {
                            log.info("sessionAuthenticationFailureHandler");
                            response.sendRedirect("/login");
                        })
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .maximumSessions(1)
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
                                .accessDeniedHandler((request, response, exception) -> {
                                    System.out.println("Access denied: " + exception.getMessage());
                                    response.sendRedirect("/access-denied.html");
                                })
//                        .accessDeniedPage("/access-denied.html")
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
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
