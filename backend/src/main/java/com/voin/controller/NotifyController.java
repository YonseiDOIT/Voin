package com.voin.controller;

import com.voin.dto.response.NotificationDto;
import com.voin.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class NotifyController {

    private final NotificationService notificationService;

    public NotifyController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 클라가 /pub/notify 로 보낸 메시지를, sender에게 에코로 보내보기
    @MessageMapping("/notify")
    public void echoToSelf(Principal principal, String message) {
        notificationService.sendToUser(principal.getName(),
                new NotificationDto("ECHO", message, System.currentTimeMillis()));
    }
}
