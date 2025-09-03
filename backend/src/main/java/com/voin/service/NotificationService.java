package com.voin.service;

import com.voin.dto.response.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendToUser(String memberId, NotificationDto payload) {
        // 클라이언트가 구독하는 경로: /user/queue/notifications
        messagingTemplate.convertAndSendToUser(memberId, "/queue/notifications", payload);
    }
}