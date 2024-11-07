package ug.edu.pl.server.infrastructure.security.auth;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ug.edu.pl.server.domain.user.UserFacade;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(TokenProperties.class)
class WebSecurityConfiguration {

    private static final String ADMIN = "ADMIN";
    private static final String[] API_PUBLIC_ENDPOINTS = {
            "/api/auth/**"
    };
    private static final String[] API_ADMIN_ENDPOINTS = {};

    @Bean
    CurrentUserContext currentUserContext() {
        return new CurrentUserContext();
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint() {
        return new AuthenticationEntryPointImpl();
    }

    @Bean
    AuthFacade authFacade(TokenProperties tokenProperties, AuthenticationManager authenticationManager, UserFacade userFacade, PasswordEncoder passwordEncoder) {
        return new AuthFacade(tokenProperties, authenticationManager, userFacade, passwordEncoder);
    }

    @Bean
    AuthTokenFilter authenticationTokenFilter(AuthFacade authFacade, UserDetailsService userDetailsService) {
        return new AuthTokenFilter(authFacade, userDetailsService);
    }

    @Bean
    UserDetailsService userDetailsService(UserFacade userFacade) {
        return new UserDetailsServiceImpl(userFacade);
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthFacade authFacade, UserDetailsService userDetailsService, AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authenticationTokenFilter(authFacade, userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(API_ADMIN_ENDPOINTS).hasRole(ADMIN)
                        .requestMatchers(API_PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .build();
    }
}
