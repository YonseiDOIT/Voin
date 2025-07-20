package com.voin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "친구 요청 DTO")
public class FriendRequestDto {
    
    @Schema(description = "친구 코드", example = "ABC12345")
    private String friendCode;
} 