package com.voin.controller;

import com.voin.dto.request.KakaoTokenRequest;
import com.voin.dto.response.ApiResponse;
import com.voin.dto.response.KakaoLoginResponse;
import com.voin.entity.Member;
import com.voin.repository.MemberRepository;
import com.voin.security.JwtTokenProvider;
import com.voin.service.KakaoAuthService;
import com.voin.util.FriendCodeGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;                 // ✅ 추가
import org.springframework.http.HttpStatus;                        // ✅ 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;            // ✅ 추가

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "🔐 Authentication", description = "인증 및 토큰 관리")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final FriendCodeGenerator friendCodeGenerator;

    // ✅ 프론트 콜백 URL을 환경설정에서 주입 (기본값: https://localhost:5174/api/auth/kakao/callback)
    @Value("${app.frontend-callback-uri:https://localhost:5174/api/auth/kakao/callback}")
    private String frontendCallbackUri;

    // ===== 1) 카카오 인가 URL: 리다이렉트 방식 =====
    @Operation(summary = "카카오 로그인 시작(리다이렉트)", description = "카카오 인가 페이지로 브라우저를 리다이렉트합니다.")
    @GetMapping("/kakao/url")
    public ResponseEntity<Void> redirectToKakaoAuthorize() {
        log.info("[AUTH] Redirecting to Kakao authorize");
        String authorizeUrl = kakaoAuthService.getKakaoAuthUrl(); // 내부에서 yml의 redirectUri 사용
        log.info("[AUTH] Kakao authorize URL = {}", authorizeUrl);
        // GET이므로 302/303 둘 다 무방하지만, 일관성 위해 303 사용 가능
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create(authorizeUrl))
                .build();
    }

    // ===== 2) 콜백: 인가 코드 처리 → 회원 조회/가입 → JWT 발급 → 프론트로 리다이렉트 =====
    @Operation(summary = "카카오 콜백", description = "인가 코드를 받아 토큰 교환 후 로그인/회원가입을 처리합니다.")
    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> kakaoCallback(@RequestParam("code") String code,
                                              @RequestParam(value = "state", required = false) String state) {
        log.info("[AUTH] Kakao callback received: state={}", state);

        try {
            // 1) 인가 코드 → 액세스 토큰
            String accessToken = kakaoAuthService.getAccessToken(code);

            // 2) 사용자 정보 조회
            Map<String, Object> userInfo = kakaoAuthService.getUserInfo(accessToken);
            String kakaoId = String.valueOf(userInfo.get("id"));

            // 3) 회원 조회/가입
            Optional<Member> existing = memberRepository.findByKakaoId(kakaoId);
            Member member = existing.orElseGet(() -> {
                String nickname = (String) userInfo.getOrDefault("nickname", "카카오사용자");
                String profileImage = (String) userInfo.getOrDefault("profile_image", null);

                String friendCode;
                do { friendCode = friendCodeGenerator.generate(); }
                while (memberRepository.existsByFriendCode(friendCode));

                return memberRepository.save(
                        Member.builder()
                                .kakaoId(kakaoId)
                                .nickname(nickname)
                                .profileImage(profileImage)
                                .friendCode(friendCode)
                                .build()
                );
            });

            // 4) JWT 발급
            String jwt = jwtTokenProvider.createToken(member.getId().toString());
            String type = existing.isPresent() ? "Login" : "Signup";

            // 5) 프론트로 303 리다이렉트 (토큰 & 타입 전달)
            URI redirect = UriComponentsBuilder
                    .fromUriString(frontendCallbackUri)         // e.g. https://localhost:5173/auth/callback
                    .queryParam("token", jwt)
                    .queryParam("type", type)
                    .build(true)
                    .toUri();

            log.info("[AUTH] Redirecting to frontend callback: {}", redirect);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)     // ✅ 303 See Other
                    .location(redirect)
                    .build();

        } catch (Exception e) {
            log.error("[AUTH] Kakao callback error", e);

            // 실패 시 프론트 에러 페이지로 리다이렉트
            URI redirect = UriComponentsBuilder
                    .fromUriString(frontendCallbackUri.replace("/auth/callback", "/login"))
                    .queryParam("error", e.getClass().getSimpleName())
                    .build(true)
                    .toUri();

            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .location(redirect)
                    .build();
        }
    }

    // ===== 3) (옵션) 프론트에서 카카오 SDK로 access_token 받은 뒤 검증하는 흐름 유지 =====
    @Operation(summary = "카카오 토큰 검증", description = "프론트에서 받은 카카오 액세스 토큰을 검증하고 JWT를 발급합니다.")
    @PostMapping("/kakao/verify")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> verifyKakaoToken(
            @Valid @RequestBody KakaoTokenRequest request) {
        log.info("[AUTH] Verifying Kakao token");
        try {
            Map<String, Object> userInfo = kakaoAuthService.getUserInfo(request.getAccessToken());
            String kakaoId = userInfo.get("id").toString();

            Optional<Member> existingMemberOpt = memberRepository.findByKakaoId(kakaoId);
            Member member = existingMemberOpt.orElseGet(() -> {
                String nickname = (String) userInfo.getOrDefault("nickname", "카카오사용자");
                String profileImage = (String) userInfo.getOrDefault("profile_image", null);

                String friendCode;
                do { friendCode = friendCodeGenerator.generate(); }
                while (memberRepository.existsByFriendCode(friendCode));

                return memberRepository.save(
                        Member.builder()
                                .kakaoId(kakaoId)
                                .nickname(nickname)
                                .profileImage(profileImage)
                                .friendCode(friendCode)
                                .build()
                );
            });

            String jwtToken = jwtTokenProvider.createToken(member.getId().toString());
            KakaoLoginResponse response = KakaoLoginResponse.builder()
                    .type(existingMemberOpt.isPresent() ? "Login" : "Signup")
                    .member(member)
                    .jwtToken(jwtToken)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(
                    existingMemberOpt.isPresent() ? "로그인이 완료되었습니다." : "회원가입이 완료되었습니다.", response));

        } catch (Exception e) {
            log.error("[AUTH] Kakao token verify error", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("카카오 토큰 검증에 실패했습니다: " + e.getMessage()));
        }
    }

    // ===== 4) JWT 유효성 검증 =====
    @Operation(summary = "토큰 검증", description = "JWT 토큰의 유효성을 검증합니다.")
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String token) {
        log.info("[AUTH] Validating JWT token");
        try {
            if (token.startsWith("Bearer ")) token = token.substring(7);
            boolean isValid = jwtTokenProvider.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success("토큰 검증이 완료되었습니다.", isValid));
        } catch (Exception e) {
            log.error("[AUTH] Token validation error", e);
            return ResponseEntity.ok(ApiResponse.success("토큰이 유효하지 않습니다.", false));
        }
    }
}
