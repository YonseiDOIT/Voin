package com.voin.dto.response;

import com.voin.entity.Friend;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "친구 요청 응답 DTO")
public class FriendRequestResponse {
    
    @Schema(description = "친구 요청 ID")
    private Long requestId;
    
    @Schema(description = "요청을 보낸 사용자 ID")
    private String fromMemberId;
    
    @Schema(description = "요청을 보낸 사용자 닉네임")
    private String fromMemberNickname;
    
    @Schema(description = "요청을 받은 사용자 ID")
    private String toMemberId;
    
    @Schema(description = "요청을 받은 사용자 닉네임")
    private String toMemberNickname;
    
    @Schema(description = "요청 상태 (PENDING, ACCEPTED, REJECTED)")
    private String status;
    
    @Schema(description = "요청 생성 시간")
    private LocalDateTime createdAt;
} 