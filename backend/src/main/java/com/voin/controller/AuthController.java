package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import com.voin.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;

    @Value("${kakao.client-id}")
    private String clientId;

    /**
     * 카카오 로그인 테스트 페이지
     */
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
    @GetMapping("/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code, Model model) {
        try {
            // 인가 코드로 토큰 받기
            String accessToken = kakaoAuthService.getAccessToken(code);
            log.info("카카오 액세스 토큰 획득 성공");
            
            // 사용자 정보 받기
            var userInfo = kakaoAuthService.getUserInfo(accessToken);
            log.info("카카오 사용자 정보 획득 성공: {}", userInfo);
            
            model.addAttribute("success", true);
            model.addAttribute("userInfo", userInfo);
            
        } catch (Exception e) {
            log.error("카카오 로그인 콜백 처리 실패", e);
            model.addAttribute("success", false);
            model.addAttribute("error", e.getMessage());
        }
        
        return "kakao-login-result";
    }

    /**
     * API 테스트용 - 카카오 로그인 URL 반환
     */
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
} 