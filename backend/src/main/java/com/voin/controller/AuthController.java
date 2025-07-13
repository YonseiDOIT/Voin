package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import com.voin.entity.Member;
import com.voin.repository.MemberRepository;
import com.voin.security.JwtTokenProvider;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * 🔐 로그인 및 인증 컨트롤러
 * 
 * 이 클래스는 사용자의 로그인과 인증을 처리하는 기능을 담당합니다.
 * 
 * 주요 기능들:
 * - 💛 카카오 로그인 처리하기
 * - 🔄 로그인 콜백 처리하기 (카카오에서 돌아온 정보 받기)
 * - 🧪 로그인 테스트 페이지 보여주기
 * - 📋 로그인 URL 제공하기
 * 
 * 쉽게 말해서, "출입 관리 사무소" 같은 역할을 해요!
 * 누가 들어올 수 있는지, 어떻게 들어오는지를 관리합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "🔐 Auth", description = "카카오 로그인 및 인증 관리")
public class AuthController {

    private final KakaoAuthService kakaoAuthService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String FRONTEND_URL = "https://localhost:5173";

    @Value("${kakao.client-id}")
    private String clientId;



    /**
     * 카카오 로그인 콜백 처리
     */
    @Operation(summary = "카카오 로그인 콜백 처리", 
               description = "카카오 인증 서버로부터 받은 인가 코드를 처리하여 사용자 정보를 조회합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/kakao/callback")
    public ResponseEntity<Void> kakaoCallback(
            @Parameter(description = "카카오로부터 받은 인가 코드", required = true)
            @RequestParam("code") String code,
            @Parameter(description = "플로우 테스트에서 왔는지 확인하는 state 파라미터", required = false)
            @RequestParam(value = "state", required = false) String state,
            HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        log.info("카카오 콜백 처리 시작 - code: {}, state: {}, referer: {}", code, state, referer);
        
        // state가 없더라도 referer를 통해 플로우 테스트인지 확인
        boolean isFromFlowTest = "flow_test".equals(state) || 
                               (referer != null && referer.contains("flow-test.html"));
        
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
                Member member = existingMember.get();
                log.info("기존 회원 로그인 - 회원 ID: {}, 닉네임: {}", member.getId(), member.getNickname());
                
                // 세션에 사용자 정보 저장
                request.getSession().setAttribute("memberId", member.getId());
                request.getSession().setAttribute("nickname", member.getNickname());
                request.getSession().setMaxInactiveInterval(24 * 60 * 60); // 24시간
                
                // JWT 토큰 생성
                String jwtToken = jwtTokenProvider.createToken(member.getId().toString());
                log.info("로그인 성공 - JWT 토큰 생성: {}", jwtToken.substring(0, 20) + "...");
                
                // 프론트엔드로 리디렉션
                try {
                    String targetUrl = FRONTEND_URL + "/?login_success=true&access_token=" + jwtToken + 
                           "&is_new_member=false&nickname=" + 
                           URLEncoder.encode(member.getNickname(), StandardCharsets.UTF_8);
                    URI redirectUri = new URI(targetUrl);
                    log.info("기존 회원 프론트엔드 리디렉트: {}", redirectUri);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(redirectUri);
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);
                } catch (Exception e) {
                    log.error("기존 회원 프론트엔드 리디렉트 URI 생성 실패", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                // 신규 회원 - 회원가입 플로우로 리다이렉트
                log.info("신규 회원 감지 - 회원가입 플로우로 이동");
                
                // 프론트엔드의 회원가입 페이지로 리디렉션
                try {
                    String targetUrl = FRONTEND_URL + "/signup?is_new_member=true&kakao_access_token=" + accessToken;
                    URI redirectUri = new URI(targetUrl);
                    log.info("신규 회원 프론트엔드 리디렉트: {}", redirectUri);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(redirectUri);
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);
                } catch (Exception e) {
                    log.error("신규 회원 프론트엔드 리디렉트 URI 생성 실패", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }
            
        } catch (Exception e) {
            log.error("카카오 로그인 콜백 처리 실패", e);
            // 오류 발생 시 프론트엔드 에러 페이지로 리다이렉트
            try {
                String errorMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                URI errorRedirectUri = new URI(FRONTEND_URL + "/?error=true&message=" + errorMessage);
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(errorRedirectUri);
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            } catch (Exception ex) {
                log.error("오류 리다이렉트 URI 생성 실패", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
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
    public ApiResponse<String> getKakaoAuthUrlApi(
            @Parameter(description = "플로우 테스트에서 호출하는지 여부", required = false)
            @RequestParam(value = "from_flow", required = false, defaultValue = "false") boolean fromFlow) {
        try {
            String authUrl = kakaoAuthService.getKakaoAuthUrl(false, fromFlow);
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
    
    @Operation(summary = "세션 확인", 
               description = "현재 세션의 로그인 상태를 확인합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/session/check")
    @ResponseBody
    public ApiResponse<Object> checkSession(HttpServletRequest request) {
        Map<String, Object> sessionInfo = new HashMap<>();
        
        Object memberId = request.getSession().getAttribute("memberId");
        Object nickname = request.getSession().getAttribute("nickname");
        
        if (memberId != null) {
            sessionInfo.put("isLoggedIn", true);
            sessionInfo.put("memberId", memberId);
            sessionInfo.put("nickname", nickname);
            sessionInfo.put("sessionId", request.getSession().getId());
            sessionInfo.put("maxInactiveInterval", request.getSession().getMaxInactiveInterval());
            return ApiResponse.success("세션이 유효합니다.", sessionInfo);
        } else {
            sessionInfo.put("isLoggedIn", false);
            return ApiResponse.success("세션이 없습니다.", sessionInfo);
        }
    }
    
    @Operation(summary = "로그아웃", 
               description = "현재 세션을 무효화합니다.")
    @SecurityRequirements // 인증 불필요
    @PostMapping("/logout")
    @ResponseBody
    public ApiResponse<Object> logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return ApiResponse.success("로그아웃되었습니다.");
    }
} 