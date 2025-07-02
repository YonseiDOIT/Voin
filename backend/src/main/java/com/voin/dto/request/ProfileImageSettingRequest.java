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

    @Schema(description = "Base64로 인코딩된 이미지 데이터")
    private String base64ImageData;

    @Schema(description = "이미지 파일 이름")
    private String fileName;

    @Schema(description = "카카오 프로필 이미지 사용 여부", example = "true")
    @Builder.Default
    private Boolean useKakaoProfileImage = true;

    @Schema(description = "파일 업로드 사용 여부", example = "false")
    @Builder.Default
    private Boolean useFileUpload = false;
} 