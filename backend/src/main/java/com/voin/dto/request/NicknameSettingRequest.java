package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "닉네임 설정 요청")
public class NicknameSettingRequest {

    @Schema(description = "카카오 액세스 토큰", required = true)
    @NotBlank(message = "액세스 토큰은 필수입니다")
    private String accessToken;

    @Schema(description = "설정할 닉네임", example = "새로운닉네임", required = true)
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2-10자 사이여야 합니다")
    private String nickname;

    @Schema(description = "카카오 닉네임 사용 여부", example = "false")
    @Builder.Default
    private Boolean useKakaoNickname = false;
} 