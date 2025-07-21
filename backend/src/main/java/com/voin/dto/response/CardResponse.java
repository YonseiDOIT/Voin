package com.voin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Schema(description = "카드 응답")
public class CardResponse {

    @Schema(description = "카드 ID")
    private Long id;

    @Schema(description = "카드 소유자 ID")
    private UUID ownerId;

    @Schema(description = "카드 소유자 닉네임")
    private String ownerNickname;

    @Schema(description = "스토리 ID")
    private Long storyId;

    @Schema(description = "코인 이름")
    private String coinName;

    @Schema(description = "키워드 목록")
    private List<String> keywords;

    @Schema(description = "카드 내용 (스토리 내용)")
    private String content;

    @Schema(description = "공개 여부")
    private Boolean isPublic;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;
} 