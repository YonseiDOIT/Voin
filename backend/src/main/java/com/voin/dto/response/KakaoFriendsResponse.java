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
@Schema(description = "카카오톡 친구 정보 응답")
public class KakaoFriendsResponse {

    @Schema(description = "총 친구 수", example = "150")
    private Integer totalCount;

    @Schema(description = "조회된 친구 수", example = "50")
    private Integer friendsCount;

    @Schema(description = "조회 성공 여부", example = "true")
    private Boolean success;

    @Schema(description = "에러 메시지", example = "친구 정보 조회 권한이 없습니다. 이용 중 동의가 필요합니다.")
    private String error;

    @Schema(description = "추가 메시지", example = "카카오 앱 심사 완료 후 이용 가능합니다.")
    private String message;
} 