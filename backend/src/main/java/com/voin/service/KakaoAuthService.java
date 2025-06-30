package com.voin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.api.auth-url}")
    private String authUrl;

    @Value("${kakao.api.base-url}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 카카오 로그인 인증 URL 생성
     */
    public String getKakaoAuthUrl() {
        return UriComponentsBuilder.fromHttpUrl(authUrl + "/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8080/auth/kakao/callback")
                .queryParam("response_type", "code")
                .queryParam("scope", "profile_nickname,profile_image")
                .build()
                .toUriString();
    }

    /**
     * 인가 코드로 액세스 토큰 받기
     */
    public String getAccessToken(String code) {
        String tokenUrl = authUrl + "/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();

        } catch (Exception e) {
            log.error("카카오 토큰 요청 실패", e);
            throw new RuntimeException("카카오 토큰 요청에 실패했습니다.", e);
        }
    }

    /**
     * 액세스 토큰으로 사용자 정보 받기
     */
    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = apiBaseUrl + "/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", jsonNode.get("id").asLong());
            
            JsonNode properties = jsonNode.get("properties");
            if (properties != null) {
                userInfo.put("nickname", properties.get("nickname").asText());
                userInfo.put("profile_image", properties.get("profile_image").asText());
            }

            return userInfo;

        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패", e);
            throw new RuntimeException("카카오 사용자 정보 요청에 실패했습니다.", e);
        }
    }
} 