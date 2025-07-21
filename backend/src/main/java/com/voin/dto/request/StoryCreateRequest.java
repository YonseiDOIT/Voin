package com.voin.dto.request;

import com.voin.constant.StoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "스토리 생성 요청")
public class StoryCreateRequest {

    @Schema(description = "스토리 타입", example = "DAILY_DIARY")
    @NotNull(message = "스토리 타입은 필수입니다")
    private StoryType type;

    @Schema(description = "스토리 내용 (일기 또는 첫 번째 답변)", example = "오늘은 정말 좋은 하루였다...")
    @NotBlank(message = "스토리 내용은 필수입니다")
    private String content;

    @Schema(description = "상황 맥락 ID (사례 돌아보기용)", example = "1")
    private Integer situationContextId;
} 