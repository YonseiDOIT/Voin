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
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Slf4j
@RestController
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
    public ApiResponse<SignupResponse> startSignup(
            @Parameter(description = "카카오 액세스 토큰", required = true)
            @RequestParam("access_token") String accessToken) {
        
        log.info("회원가입 프로세스 시작 - 액세스 토큰: {}", accessToken);
        
        try {
            SignupResponse response = signupService.startSignupProcess(accessToken);
            log.info("회원가입 프로세스 시작 성공");
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("회원가입 프로세스 시작 실패", e);
            return ApiResponse.error("회원가입 프로세스 시작 실패: " + e.getMessage());
        }
    }

    /**
     * 닉네임 설정
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
    public ApiResponse<SignupResponse> setNickname(@Valid @RequestBody NicknameSettingRequest request) {
        log.info("닉네임 설정 요청 - 닉네임: {}", request.getNickname());
        
        try {
            SignupResponse response = signupService.setNickname(request);
            log.info("닉네임 설정 성공");
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("닉네임 설정 실패", e);
            return ApiResponse.error("닉네임 설정 실패: " + e.getMessage());
        }
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
    public ApiResponse<SignupResponse> setProfileImageAndComplete(@Valid @RequestBody ProfileImageSettingRequest request) {
        log.info("프로필 이미지 설정 및 회원가입 완료 요청");
        
        try {
            SignupResponse response = signupService.setProfileImageAndComplete(request);
            log.info("회원가입 완료 - JWT 토큰 생성됨");
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("프로필 이미지 설정 및 회원가입 완료 실패", e);
            return ApiResponse.error("회원가입 완료 실패: " + e.getMessage());
        }
    }

    /**
     * 전체 회원 조회 (개발/테스트용)
     */
    @Operation(summary = "전체 회원 조회", 
               description = "DB에 저장된 모든 회원 정보를 조회합니다 (개발/테스트용)")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/members")
    public ApiResponse<java.util.List<Member>> getAllMembers() {
        log.info("전체 회원 조회 요청");
        
        try {
            java.util.List<Member> members = memberRepository.findAll();
            log.info("전체 회원 조회 성공 - 총 {} 명", members.size());
            return ApiResponse.success(members);
        } catch (Exception e) {
            log.error("전체 회원 조회 실패", e);
            return ApiResponse.error("전체 회원 조회 실패: " + e.getMessage());
        }
    }
} 