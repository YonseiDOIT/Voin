package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 이미지 설정 요청")
public class ProfileImageSettingRequest {

    @Schema(description = "카카오 액세스 토큰", required = true)
    @NotBlank(message = "액세스 토큰은 필수입니다")
    private String accessToken;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;

    @Schema(description = "카카오 프로필 이미지 사용 여부", example = "true")
    @Builder.Default
    private Boolean useKakaoProfileImage = true;
} 