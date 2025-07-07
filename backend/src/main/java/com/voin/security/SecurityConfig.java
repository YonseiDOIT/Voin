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
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable())
            .sessionManagement(session -> 
                session.maximumSessions(1) // 한 사용자당 최대 1개 세션
                       .maxSessionsPreventsLogin(false)) // 새 로그인 시 기존 세션 무효화
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/", "/index.html", "/signup/**", 
                               "/h2-console/**", "/swagger-ui/**", "/api-docs/**", 
                               "/api/**").permitAll() // 모든 API 허용, 애플리케이션에서 로그인 체크
                .anyRequest().permitAll()); // 전체 허용, JavaScript에서 로그인 체크

        return http.build();
    }
} 