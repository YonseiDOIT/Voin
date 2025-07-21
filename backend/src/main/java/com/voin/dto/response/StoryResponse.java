package com.voin.dto.response;

import com.voin.constant.StoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "스토리 응답")
public class StoryResponse {

    @Schema(description = "스토리 ID")
    private Long id;

    @Schema(description = "스토리 타입")
    private StoryType type;

    @Schema(description = "첫 번째 내용 (일기 또는 첫 번째 답변)")
    private String answer1;

    @Schema(description = "두 번째 내용 (사례 돌아보기의 두 번째 답변)")
    private String answer2;

    @Schema(description = "상황 맥락 ID")
    private Integer situationContextId;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
} 