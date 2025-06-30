package com.voin.dto.response;

import com.voin.constant.SignupStep;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 진행 상황 응답")
public class SignupResponse {

    @Schema(description = "현재 회원가입 단계")
    private SignupStep currentStep;

    @Schema(description = "다음 회원가입 단계")
    private SignupStep nextStep;

    @Schema(description = "카카오 사용자 정보")
    private KakaoUserResponse kakaoUserInfo;

    @Schema(description = "현재 설정된 닉네임", example = "박규민")
    private String currentNickname;

    @Schema(description = "현재 설정된 프로필 이미지 URL")
    private String currentProfileImage;

    @Schema(description = "회원가입 완료 여부", example = "false")
    private Boolean isCompleted;

    @Schema(description = "다음 단계 URL", example = "/signup/profile-image")
    private String nextStepUrl;

    @Schema(description = "JWT 토큰 (회원가입 완료 시)")
    private String jwtToken;
} 