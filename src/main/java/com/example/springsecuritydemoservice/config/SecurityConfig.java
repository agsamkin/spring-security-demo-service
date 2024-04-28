package com.example.springsecuritydemoservice.config;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)  throws Exception {
         http
                 .csrf(CsrfConfigurer::disable)
                 .authorizeHttpRequests(request ->
                     request
                             .requestMatchers(WHITE_LIST_URL).permitAll()
                             .anyRequest().authenticated()
                 )
                 .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                 .authenticationProvider(authenticationProvider)
                 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                 .logout(logout ->
                         logout.logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                 );

         return http.build();
    }

}
