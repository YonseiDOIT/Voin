package com.voin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Voin API")
                        .version("1.0.0")
                        .description("""
                                Voin(Value+Coin) - 자기이해 플랫폼 API 문서
                                
                                ## 주요 기능
                                - 카카오 로그인 연동
                                - 사용자 정보 관리
                                - 가치 탐색 카드 시스템
                                
                                ## 카카오 로그인 테스트
                                - 테스트 페이지: `/auth/test`
                                - 동의항목: 닉네임, 프로필 사진, 친구 목록(선택)
                                
                                ## 인증 방식
                                - JWT Bearer Token 사용
                                - 카카오 OAuth 2.0 연동
                                """)
                        .contact(new Contact()
                                .name("Voin Team")
                                .email("contact@voin.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("로컬 개발 서버"),
                        new Server().url("https://api.voin.com").description("운영 서버")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 접두사 없이)")));
    }
} 