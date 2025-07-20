package com.voin.dto.response;

import com.voin.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카카오 로그인/회원가입 결과 응답")
public class KakaoLoginResponse {

    @Schema(description = "처리 유형 (Login 또는 Signup)", example = "Login")
    private String type;

    @Schema(description = "회원 정보")
    private Member member;

    @Schema(description = "발급된 JWT 토큰")
    private String jwtToken;
} 