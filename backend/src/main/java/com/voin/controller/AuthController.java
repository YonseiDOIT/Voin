package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import com.voin.entity.Member;
import com.voin.repository.MemberRepository;
import com.voin.service.KakaoAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "인증 API", description = "카카오 로그인 관련 API")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final MemberRepository memberRepository;

    @Value("${kakao.client-id}")
    private String clientId;

    /**
     * 카카오 로그인 테스트 페이지
     */
    @Operation(summary = "카카오 로그인 테스트 페이지", 
               description = "카카오 로그인을 테스트할 수 있는 HTML 페이지를 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/test")
    public String loginTestPage(Model model) {
        try {
            String kakaoAuthUrl = kakaoAuthService.getKakaoAuthUrl();
            
            model.addAttribute("kakaoAuthUrl", kakaoAuthUrl);
            model.addAttribute("clientId", clientId);
            
            log.info("카카오 인증 URL 생성: {}", kakaoAuthUrl);
        } catch (Exception e) {
            log.error("카카오 인증 URL 생성 실패", e);
            model.addAttribute("error", "카카오 설정이 올바르지 않습니다: " + e.getMessage());
        }
        return "kakao-login-test";
    }

    /**
     * 카카오 로그인 콜백 처리
     */
    @Operation(summary = "카카오 로그인 콜백 처리", 
               description = "카카오 인증 서버로부터 받은 인가 코드를 처리하여 사용자 정보를 조회합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/kakao/callback")
    public String kakaoCallback(
            @Parameter(description = "카카오로부터 받은 인가 코드", required = true)
            @RequestParam("code") String code, 
            Model model) {
        log.info("카카오 콜백 처리 시작 - code: {}", code);
        
        try {
            // 1단계: 인가 코드로 토큰 받기
            log.info("1단계: 액세스 토큰 요청 시작");
            String accessToken = kakaoAuthService.getAccessToken(code);
            log.info("1단계 완료: 카카오 액세스 토큰 획득 성공");
            
            // 2단계: 사용자 정보 받기
            log.info("2단계: 사용자 정보 요청 시작");
            var userInfo = kakaoAuthService.getUserInfo(accessToken);
            String kakaoId = userInfo.get("id").toString();
            log.info("2단계 완료: 카카오 사용자 정보 획득 성공 - ID: {}, 닉네임: {}", 
                    kakaoId, userInfo.get("nickname"));
            
            // 3단계: 기존 회원인지 확인
            Optional<Member> existingMember = memberRepository.findByKakaoId(kakaoId);
            
            if (existingMember.isPresent()) {
                // 기존 회원 - 로그인 처리
                log.info("기존 회원 로그인 - 회원 ID: {}", existingMember.get().getId());
                
                model.addAttribute("success", true);
                model.addAttribute("isExistingMember", true);
                model.addAttribute("member", existingMember.get());
                model.addAttribute("userInfo", userInfo);
                
                // TODO: JWT 토큰 생성 및 추가
                // model.addAttribute("jwtToken", jwtToken);
                
                return "kakao-login-result";
            } else {
                // 신규 회원 - 회원가입 플로우로 리다이렉트
                log.info("신규 회원 감지 - 회원가입 플로우로 이동");
                
                // 액세스 토큰과 함께 닉네임 설정 페이지로 리다이렉트
                return "redirect:/signup/nickname?access_token=" + accessToken;
            }
            
        } catch (Exception e) {
            log.error("카카오 로그인 콜백 처리 실패", e);
            model.addAttribute("success", false);
            model.addAttribute("error", "로그인 처리 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("errorDetail", e.getClass().getSimpleName());
            return "kakao-login-result";
        }
    }

    /**
     * API 테스트용 - 카카오 로그인 URL 반환
     */
    @Operation(summary = "카카오 로그인 URL 조회", 
               description = "카카오 로그인을 위한 인증 URL을 반환합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "카카오 로그인 URL 반환 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", 
                    description = "카카오 설정 오류")
    })
    @SecurityRequirements // 인증 불필요
    @GetMapping("/kakao/url")
    @ResponseBody
    public ApiResponse<String> getKakaoAuthUrlApi() {
        try {
            String authUrl = kakaoAuthService.getKakaoAuthUrl();
            return ApiResponse.success(authUrl);
        } catch (Exception e) {
            return ApiResponse.error("카카오 설정 오류: " + e.getMessage());
        }
    }

    /**
     * 카카오 앱 연결 끊기 (동의 페이지를 다시 보기 위해)
     */
    @Operation(summary = "카카오 앱 연결 끊기", 
               description = "카카오 앱과의 연결을 해제합니다. 연결 해제 후 다시 로그인하면 동의 페이지가 나타납니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "연결 끊기 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 액세스 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", 
                    description = "연결 끊기 처리 오류")
    })
    @PostMapping("/kakao/unlink")
    @ResponseBody
    public ApiResponse<Object> unlinkKakaoApp(
            @Parameter(description = "카카오 액세스 토큰", required = true)
            @RequestParam("access_token") String accessToken) {
        try {
            var result = kakaoAuthService.unlinkKakaoApp(accessToken);
            if ((Boolean) result.getOrDefault("success", false)) {
                return ApiResponse.success(result);
            } else {
                return ApiResponse.error(result.get("error").toString());
            }
        } catch (Exception e) {
            log.error("카카오 앱 연결 끊기 API 오류", e);
            return ApiResponse.error("연결 끊기 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 디버깅용 - 콜백 테스트
     */
    @Operation(summary = "콜백 엔드포인트 테스트", 
               description = "카카오 콜백 엔드포인트가 정상 작동하는지 테스트합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/kakao/test-callback")
    @ResponseBody
    public ApiResponse<Object> testCallback() {
        Map<String, Object> testInfo = new HashMap<>();
        testInfo.put("clientId", clientId);
        testInfo.put("message", "콜백 엔드포인트가 정상 작동 중입니다");
        testInfo.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(testInfo);
    }
} 