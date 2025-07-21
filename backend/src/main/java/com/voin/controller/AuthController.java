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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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

    /**
     * 카카오 로그인/회원가입 처리
     */
    @Operation(summary = "카카오 토큰 검증", description = "프론트엔드에서 받은 카카오 액세스 토큰을 검증하고 JWT를 발급합니다.")
    @PostMapping("/kakao/verify")
    public ResponseEntity<ApiResponse<KakaoLoginResponse>> verifyKakaoToken(
            @Valid @RequestBody KakaoTokenRequest request) {
        log.info("Verifying Kakao token");
        
        try {
            // 1. 카카오 토큰으로 사용자 정보 조회
            Map<String, Object> userInfo = kakaoAuthService.getUserInfo(request.getAccessToken());
            String kakaoId = userInfo.get("id").toString();
            
            // 2. DB에서 회원 확인
            Optional<Member> existingMemberOpt = memberRepository.findByKakaoId(kakaoId);
            
            if (existingMemberOpt.isPresent()) {
                // 기존 회원 - 로그인
                Member member = existingMemberOpt.get();
                String jwtToken = jwtTokenProvider.createToken(member.getId().toString());
                
                KakaoLoginResponse response = KakaoLoginResponse.builder()
                        .type("Login")
                        .member(member)
                        .jwtToken(jwtToken)
                        .build();
                        
                return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", response));
            } else {
                // 신규 회원 - 회원가입
                Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
                String nickname = properties != null ? (String) properties.get("nickname") : "카카오사용자";
                String profileImage = properties != null ? (String) properties.get("profile_image_url") : null;

                // 친구 코드 생성
                String friendCode;
                do {
                    friendCode = friendCodeGenerator.generate();
                } while (memberRepository.existsByFriendCode(friendCode));

                Member newMember = Member.builder()
                        .kakaoId(kakaoId)
                        .nickname(nickname)
                        .profileImage(profileImage)
                        .friendCode(friendCode)
                        .build();
                
                memberRepository.save(newMember);
                String jwtToken = jwtTokenProvider.createToken(newMember.getId().toString());

                KakaoLoginResponse response = KakaoLoginResponse.builder()
                        .type("Signup")
                        .member(newMember)
                        .jwtToken(jwtToken)
                        .build();
                        
                return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다.", response));
            }
            
        } catch (Exception e) {
            log.error("카카오 토큰 검증 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("카카오 토큰 검증에 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 카카오 로그인 URL 조회
     */
    @Operation(summary = "카카오 로그인 URL", description = "카카오 로그인을 위한 인증 URL을 반환합니다.")
    @GetMapping("/kakao/url")
    public ResponseEntity<ApiResponse<String>> getKakaoAuthUrl() {
        log.info("Getting Kakao auth URL");
        try {
            String authUrl = kakaoAuthService.getKakaoAuthUrl();
            return ResponseEntity.ok(ApiResponse.success("카카오 로그인 URL을 조회했습니다.", authUrl));
        } catch (Exception e) {
            log.error("카카오 로그인 URL 조회 중 오류 발생", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("카카오 설정 오류: " + e.getMessage()));
        }
    }

    /**
     * 토큰 유효성 검증
     */
    @Operation(summary = "토큰 검증", description = "JWT 토큰의 유효성을 검증합니다.")
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String token) {
        log.info("Validating JWT token");
        try {
            // Bearer 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            boolean isValid = jwtTokenProvider.validateToken(token);
            return ResponseEntity.ok(ApiResponse.success("토큰 검증이 완료되었습니다.", isValid));
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            return ResponseEntity.ok(ApiResponse.success("토큰이 유효하지 않습니다.", false));
        }
    }
} 