package com.voin.dto.response;

import com.voin.entity.Card;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "친구 카드 응답 DTO")
public class FriendCardResponse {
    
    @Schema(description = "카드 ID")
    private Long cardId;
    
    @Schema(description = "작성자 ID")
    private String memberId;
    
    @Schema(description = "작성자 닉네임")
    private String memberNickname;
    
    @Schema(description = "카드 내용")
    private String content;
    
    @Schema(description = "코인 정보")
    private String coinType;
    
    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;
} 