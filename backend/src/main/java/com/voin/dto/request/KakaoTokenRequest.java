package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "카카오 토큰 검증 요청")
public class KakaoTokenRequest {

    @Schema(description = "카카오 액세스 토큰", example = "abcd1234...")
    @NotBlank(message = "카카오 액세스 토큰은 필수입니다")
    private String accessToken;
} 