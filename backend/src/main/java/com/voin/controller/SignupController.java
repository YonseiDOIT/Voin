package com.voin.controller;

import com.voin.dto.request.NicknameSettingRequest;
import com.voin.dto.request.ProfileImageSettingRequest;
import com.voin.dto.response.ApiResponse;
import com.voin.dto.response.SignupResponse;
import com.voin.entity.Member;
import com.voin.repository.MemberRepository;
import com.voin.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/signup")
@Tag(name = "회원가입 API", description = "카카오 로그인을 통한 회원가입 관련 API")
public class SignupController {

    private final SignupService signupService;
    private final MemberRepository memberRepository;

    /**
     * 카카오 로그인 후 회원가입 프로세스 시작
     */
    @Operation(summary = "회원가입 프로세스 시작", 
               description = "카카오 액세스 토큰으로 회원가입 프로세스를 시작합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "회원가입 프로세스 시작 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "이미 가입된 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500", 
                    description = "카카오 API 오류")
    })
    @SecurityRequirements // 인증 불필요
    @PostMapping("/start")
    @ResponseBody
    public ApiResponse<SignupResponse> startSignup(
            @Parameter(description = "카카오 액세스 토큰", required = true)
            @RequestParam("access_token") String accessToken) {
        
        try {
            SignupResponse response = signupService.startSignupProcess(accessToken);
            return ApiResponse.success("회원가입 프로세스가 시작되었습니다.", response);
        } catch (IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("회원가입 시작 오류: {}", e.getMessage());
            return ApiResponse.error("회원가입 프로세스를 시작할 수 없습니다.");
        }
    }

    /**
     * 닉네임 설정 페이지
     */
    @Operation(summary = "닉네임 설정 페이지", 
               description = "닉네임을 설정할 수 있는 HTML 페이지를 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/nickname")
    public String nicknameSettingPage(
            @Parameter(description = "카카오 액세스 토큰")
            @RequestParam(value = "access_token", required = false) String accessToken,
            Model model) {
        
        model.addAttribute("accessToken", accessToken);
        return "/signup-nickname";
    }

    /**
     * 닉네임 설정 처리
     */
    @Operation(summary = "닉네임 설정", 
               description = "사용자가 선택한 닉네임을 설정합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "닉네임 설정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 요청 또는 세션 만료")
    })
    @SecurityRequirements // 인증 불필요
    @PostMapping("/nickname")
    @ResponseBody
    public ApiResponse<SignupResponse> setNickname(@Valid @RequestBody NicknameSettingRequest request) {
        try {
            SignupResponse response = signupService.setNickname(request);
            return ApiResponse.success("닉네임이 설정되었습니다.", response);
        } catch (IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("닉네임 설정 오류: {}", e.getMessage());
            return ApiResponse.error("닉네임을 설정할 수 없습니다.");
        }
    }

    /**
     * 프로필 이미지 설정 페이지
     */
    @Operation(summary = "프로필 이미지 설정 페이지", 
               description = "프로필 이미지를 설정할 수 있는 HTML 페이지를 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/profile-image")
    public String profileImageSettingPage(
            @Parameter(description = "카카오 액세스 토큰")
            @RequestParam(value = "access_token", required = false) String accessToken,
            Model model) {
        
        model.addAttribute("accessToken", accessToken);
        return "/signup-profile-image";
    }

    /**
     * 프로필 이미지 설정 및 회원가입 완료
     */
    @Operation(summary = "프로필 이미지 설정 및 회원가입 완료", 
               description = "프로필 이미지를 설정하고 회원가입을 완료합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", 
                    description = "회원가입 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", 
                    description = "잘못된 요청 또는 세션 만료")
    })
    @SecurityRequirements // 인증 불필요
    @PostMapping("/profile-image")
    @ResponseBody
    public ApiResponse<SignupResponse> setProfileImageAndComplete(@Valid @RequestBody ProfileImageSettingRequest request) {
        try {
            SignupResponse response = signupService.setProfileImageAndComplete(request);
            return ApiResponse.success("회원가입이 완료되었습니다!", response);
        } catch (IllegalStateException e) {
            return ApiResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("프로필 이미지 설정 오류: {}", e.getMessage());
            return ApiResponse.error("프로필 이미지를 설정할 수 없습니다.");
        }
    }

    /**
     * 회원가입 완료 페이지
     */
    @Operation(summary = "회원가입 완료 페이지", 
               description = "회원가입 완료를 알리는 HTML 페이지를 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/complete")
    public String signupCompletePage(
            @Parameter(description = "JWT 토큰")
            @RequestParam(value = "token", required = false) String token,
            Model model) {
        
        model.addAttribute("jwtToken", token);
        
        // 임시 토큰에서 회원 ID 추출하여 회원 정보 조회
        if (token != null && token.startsWith("VOIN_TOKEN_")) {
            try {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    String memberIdStr = parts[2];
                    // UUID 파싱 시도
                    java.util.UUID memberId = java.util.UUID.fromString(memberIdStr);
                    
                    // 회원 정보 조회하여 모델에 추가
                    Optional<Member> memberOpt = memberRepository.findById(memberId);
                    if (memberOpt.isPresent()) {
                        Member member = memberOpt.get();
                        model.addAttribute("member", member);
                        log.info("회원가입 완료 페이지 - 회원 정보 조회 성공: {}", member.getNickname());
                    }
                }
            } catch (Exception e) {
                log.warn("토큰에서 회원 ID 추출 실패: {}", e.getMessage());
            }
        }
        
        return "/signup-complete";
    }

    /**
     * DB에 저장된 모든 회원 조회 (개발/테스트용)
     */
    @Operation(summary = "전체 회원 조회", 
               description = "DB에 저장된 모든 회원 정보를 조회합니다 (개발/테스트용)")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/members")
    @ResponseBody
    public ApiResponse<java.util.List<Member>> getAllMembers() {
        try {
            java.util.List<Member> members = memberRepository.findAll();
            log.info("전체 회원 조회: {} 명", members.size());
            return ApiResponse.success("전체 회원 조회 성공", members);
        } catch (Exception e) {
            log.error("전체 회원 조회 실패", e);
            return ApiResponse.error("회원 조회에 실패했습니다: " + e.getMessage());
        }
    }
} 