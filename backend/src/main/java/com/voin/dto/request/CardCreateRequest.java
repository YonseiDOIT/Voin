package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "카드 생성 요청")
public class CardCreateRequest {

    @Schema(description = "스토리 ID", example = "1")
    @NotNull(message = "스토리 ID는 필수입니다")
    private Long storyId;

    @Schema(description = "코인 ID", example = "1")
    @NotNull(message = "코인 ID는 필수입니다")
    private Long coinId;

    @Schema(description = "키워드 ID", example = "1")
    @NotNull(message = "키워드 ID는 필수입니다 ")
    private Long keywordId;

    @Schema(description = "공개 여부", example = "true")
    private Boolean isPublic = true;
} 