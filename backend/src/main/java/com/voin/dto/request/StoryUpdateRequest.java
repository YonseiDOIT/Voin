package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "스토리 업데이트 요청")
public class StoryUpdateRequest {

    @Schema(description = "추가할 내용 (두 번째 답변)", example = "내 행동에 대해 생각해보니...")
    @NotBlank(message = "업데이트할 내용은 필수입니다")
    private String additionalContent;
} 