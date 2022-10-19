package org.yomikaze.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.yomikaze.persistence.entity.Account;
import org.yomikaze.persistence.repository.AccountRepository;

import javax.servlet.http.HttpSession;

@Log
@EnableCaching
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AccountRepository accountRepository;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(authorize -> authorize
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
            .formLogin(form -> form
                .loginPage("/login")
                .failureUrl("/login?failure")
                .defaultSuccessUrl("/")
                .successHandler((request, response, authentication) -> {
                    Account account = accountRepository.findByUsername(authentication.getName())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    HttpSession session = request.getSession();
                    session.setAttribute("account", account);
                    System.out.println("authentication: " + authentication);
                    System.out.println("authentication.principal: " + authentication.getPrincipal());
                    System.out.println("authentication.details: " + authentication.getDetails());
                    System.out.println("authentication.authorities: " + authentication.getAuthorities());
                    System.out.println("authentication.credentials: " + authentication.getCredentials());
                    System.out.println("authentication.name: " + authentication.getName());
                    String referer = request.getHeader(HttpHeaders.REFERER);
                    if (referer != null) {
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect("/");
                    }
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
                .logoutSuccessUrl("/login")
            )
            .exceptionHandling(exceptions -> exceptions
                    .accessDeniedHandler((request, response, exception) -> {
                        System.out.println("Access denied: " + exception.getMessage());
                        response.sendRedirect("/access-denied.html");
                    })
//                        .accessDeniedPage("/access-denied.html")
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
