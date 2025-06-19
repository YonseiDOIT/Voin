package com.voin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 완전 비활성화 (개발용)
            .headers(headers -> headers
                .frameOptions().disable() // H2 콘솔의 iframe 허용
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll() // Swagger UI 허용
                .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 허용
                .anyRequest().permitAll() // 모든 요청 허용 (개발용)
            );

        return http.build();
    }
} 