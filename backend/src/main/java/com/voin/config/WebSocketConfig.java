package com.voin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired JwtHandshakeInterceptor jwtHandshakeInterceptor;
    @Autowired UserPrincipalHandshakeHandler userPrincipalHandshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(jwtHandshakeInterceptor)
                .setHandshakeHandler(userPrincipalHandshakeHandler)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 서버로 보내는 prefix (클라 → 서버)
        registry.setApplicationDestinationPrefixes("/pub");

        // 서버가 클라로 브로드캐스트/개인큐로 보내는 prefix (서버 → 클라)
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user"); // 개인큐 프리픽스
    }
}
