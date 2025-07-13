package com.voin.security;

import com.voin.security.JwtAuthenticationEntryPoint;
import com.voin.security.JwtAccessDeniedHandler;
import com.voin.security.JwtAuthenticationFilter;
import com.voin.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

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
//                 .csrf(csrf -> csrf.disable())
//                 .headers(headers -> headers.frameOptions().disable())
//                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/auth/**", "/error", "/swagger-ui.html", "/v3/api-docs/**", "/h2-console/**").permitAll()
//                         .anyRequest().authenticated()
//                 )
//                 .exceptionHandling(ex -> ex
//                         .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                         .accessDeniedHandler(jwtAccessDeniedHandler)
//                 )
//                 .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
