package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "카드 공개 설정 변경 요청")
public class CardVisibilityUpdateRequest {

    @Schema(description = "공개 여부 (true: 공개, false: 비공개)", example = "true")
    @NotNull(message = "공개 여부는 필수입니다")
    private Boolean isPublic;
} 