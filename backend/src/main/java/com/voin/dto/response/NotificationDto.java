package com.voin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String type;       // 알림 종류 (예: FRIEND_REQUEST, COMMENT, SYSTEM 등)
    private String message;    // 알림 내용
    private long timestamp;    // 생성 시각 (System.currentTimeMillis())
}
