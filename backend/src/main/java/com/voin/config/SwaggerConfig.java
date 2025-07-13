package com.voin.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🪙 VOIN API")
                        .version("2.0.0")
                        .description("""
                                # VOIN (Value + Coin) - 장점 발견 플랫폼 API
                                
                                당신의 숨겨진 장점을 발견하고 소중한 사람들과 나누는 특별한 공간의 API 문서입니다.
                                
                                ## 🚀 주요 기능
                                - **카카오 로그인 연동**: OAuth 2.0 기반 소셜 로그인
                                - **장점 코인 생성**: 3가지 방식으로 나만의 장점 발견
                                  - 📝 오늘의 일기
                                  - 💭 사례 돌아보기  
                                  - 👥 함께한 추억 떠올리기 (준비 중)
                                - **사용자 관리**: 프로필, 친구 코드 시스템
                                - **카드 시스템**: 장점을 카드로 저장하고 관리
                                
                                ## 🔐 인증 방식
                                - **세션 기반 인증**: 카카오 로그인 후 서버 세션 관리
                                - **JWT 토큰**: API 요청 시 Bearer 토큰 사용 (선택적)
                                
                                ## 🧪 테스트 페이지
                                - **홈페이지**: `/` - 메인 기능 접근
                                - **플로우 테스트**: `/flow-test.html` - 전체 플로우 테스트
                                - **카카오 로그인 테스트**: `/kakao-login-test.html`
                                - **H2 DB 콘솔**: `/h2-console`
                                
                                ## 📊 데이터 구조
                                - **6개 고정 COIN**: 관리와 성장, 감정과 태도, 창의와 몰입, 사고와 해결, 관계와 공감, 신념과 실행
                                - **55개 KEYWORD**: 각 코인별 세분화된 장점 키워드
                                - **상황 맥락**: 6가지 상황별 경험 분류
                                """)
                        .contact(new Contact()
                                .name("VOIN Development Team")
                                .email("dev@voin.kr")
                                .url("https://github.com/voin-project"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("🖥️ 로컬 개발 서버"),
                        new Server().url("https://api.voin.kr").description("🌐 운영 서버")
                ))
                .tags(List.of(
                        new Tag().name("🏠 Home").description("홈페이지 및 정적 페이지 라우팅"),
                        new Tag().name("🔐 Auth").description("카카오 로그인 및 인증 관리"),
                        new Tag().name("👤 Member").description("사용자 정보 관리"),
                        new Tag().name("🪙 Card").description("장점 카드 CRUD 및 관리"),
                        new Tag().name("🎯 Coin Finder").description("장점 발견 플로우 (일기, 사례 돌아보기 등)"),
                        new Tag().name("💎 Master Data").description("코인, 키워드 등 마스터 데이터")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 접두사 없이). 현재는 세션 기반 인증을 주로 사용합니다.")));
    }
} 