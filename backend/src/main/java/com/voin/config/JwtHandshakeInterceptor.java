package com.voin.config;

import com.voin.security.JwtTokenProvider;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider; // 용용이 프로젝트에 있는 구현 그대로 사용

    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 예: 쿼리스트링 또는 헤더에서 토큰 추출
        // 클라에서 연결 시 `Authorization: Bearer xxx` 헤더를 보내도록 권장
        if (request instanceof ServletServerHttpRequest servlet) {
            var httpReq = servlet.getServletRequest();
            String auth = httpReq.getHeader("Authorization");
            if (auth != null && auth.startsWith("Bearer ")) {
                String token = auth.substring(7);
                if (jwtTokenProvider.validateToken(token)) {
                    String memberId = jwtTokenProvider.getSubject(token); // 용용 프로젝트에서 subject=memberId
                    attributes.put("memberId", memberId);
                    return true;
                }
            }
        }
        return false; // 인증 실패 시 연결 거절
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception ex) { }
}