package com.voin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카카오 로그인 전체 응답")
public class KakaoLoginResponse {

    @Schema(description = "카카오 액세스 토큰", example = "gDrCMhSzm4I7xJ8KW7YYJ3bLYYKLJHXKdkj7...")
    private String accessToken;

    @Schema(description = "카카오 사용자 정보")
    private KakaoUserResponse userInfo;

    @Schema(description = "카카오톡 친구 정보")
    private KakaoFriendsResponse friendsInfo;

    @Schema(description = "에러 메시지", example = "토큰 교환 실패")
    private String error;

    @Schema(description = "에러 상세", example = "Invalid authorization code")
    private String errorDetail;

    @Schema(description = "로그인 성공 여부", example = "true")
    private Boolean success;
} 