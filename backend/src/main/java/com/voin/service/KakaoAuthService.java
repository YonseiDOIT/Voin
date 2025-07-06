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
        return getKakaoAuthUrl(false, false);
    }

    /**
     * 카카오 로그인 인증 URL 생성 (동의 페이지 강제 표시 옵션)
     */
    public String getKakaoAuthUrl(boolean forceConsent) {
        return getKakaoAuthUrl(forceConsent, false);
    }

    /**
     * 카카오 로그인 인증 URL 생성 (플로우 테스트용)
     */
    public String getKakaoAuthUrl(boolean forceConsent, boolean fromFlow) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authUrl + "/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", "http://localhost:8080/auth/kakao/callback")
                .queryParam("response_type", "code")
                // 필수 동의: 닉네임, 프로필 이미지
                .queryParam("scope", "profile_nickname,profile_image");
        
        // 플로우 테스트에서 온 요청인지 state 파라미터로 구분
        if (fromFlow) {
            builder.queryParam("state", "flow_test");
        }
        
        if (forceConsent) {
            builder.queryParam("prompt", "consent");  // 매번 동의 페이지 표시
        }
        
        return builder.build().toUriString();
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
        
        // 클라이언트 시크릿이 있는 경우에만 추가
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            params.add("client_secret", clientSecret);
            log.debug("클라이언트 시크릿 포함하여 토큰 요청");
        } else {
            log.debug("클라이언트 시크릿 없이 토큰 요청");
        }
        
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        log.info("카카오 토큰 요청 URL: {}", tokenUrl);
        log.debug("요청 파라미터: grant_type={}, client_id={}, redirect_uri={}, code={}...", 
                params.get("grant_type"), params.get("client_id"), params.get("redirect_uri"), 
                code.substring(0, Math.min(10, code.length())));

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("카카오 토큰 응답 상태: {}", response.getStatusCode());
            log.debug("카카오 토큰 응답 본문: {}", response.getBody());

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.has("error")) {
                String error = jsonNode.get("error").asText();
                String errorDescription = jsonNode.has("error_description") ? 
                    jsonNode.get("error_description").asText() : "설명 없음";
                throw new RuntimeException("카카오 토큰 요청 에러: " + error + " - " + errorDescription);
            }
            
            String accessToken = jsonNode.get("access_token").asText();
            log.info("액세스 토큰 획득 성공 (길이: {})", accessToken.length());
            return accessToken;

        } catch (Exception e) {
            log.error("카카오 토큰 요청 실패", e);
            throw new RuntimeException("카카오 토큰 요청에 실패했습니다: " + e.getMessage(), e);
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

        log.info("카카오 사용자 정보 요청 URL: {}", userInfoUrl);
        log.debug("액세스 토큰 길이: {}", accessToken.length());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            log.info("카카오 사용자 정보 응답 상태: {}", response.getStatusCode());
            log.debug("카카오 사용자 정보 응답 본문: {}", response.getBody());

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            if (jsonNode.has("code") && jsonNode.get("code").asInt() < 0) {
                String errorMsg = jsonNode.has("msg") ? jsonNode.get("msg").asText() : "알 수 없는 에러";
                throw new RuntimeException("카카오 사용자 정보 요청 에러: " + errorMsg);
            }
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", jsonNode.get("id").asLong());
            
            // 연결 시간
            if (jsonNode.has("connected_at")) {
                userInfo.put("connected_at", jsonNode.get("connected_at").asText());
            }
            
            // 프로필 정보 (닉네임, 프로필 이미지) - 필수 동의
            JsonNode properties = jsonNode.get("properties");
            if (properties != null) {
                if (properties.has("nickname")) {
                    userInfo.put("nickname", properties.get("nickname").asText());
                }
                if (properties.has("profile_image")) {
                    userInfo.put("profile_image", properties.get("profile_image").asText());
                }
                if (properties.has("thumbnail_image")) {
                    userInfo.put("thumbnail_image", properties.get("thumbnail_image").asText());
                }
            }

            // 카카오계정 정보
            JsonNode kakaoAccount = jsonNode.get("kakao_account");
            if (kakaoAccount != null) {
                userInfo.put("kakao_account", new HashMap<String, Object>());
                Map<String, Object> accountInfo = (Map<String, Object>) userInfo.get("kakao_account");
                
                // 프로필 정보
                if (kakaoAccount.has("profile")) {
                    JsonNode profile = kakaoAccount.get("profile");
                    Map<String, Object> profileInfo = new HashMap<>();
                    
                    if (profile.has("nickname")) {
                        profileInfo.put("nickname", profile.get("nickname").asText());
                    }
                    if (profile.has("profile_image_url")) {
                        profileInfo.put("profile_image_url", profile.get("profile_image_url").asText());
                    }
                    if (profile.has("thumbnail_image_url")) {
                        profileInfo.put("thumbnail_image_url", profile.get("thumbnail_image_url").asText());
                    }
                    
                    accountInfo.put("profile", profileInfo);
                }
                
                // 이메일 (선택 동의)
                if (kakaoAccount.has("email") && kakaoAccount.has("email_needs_agreement") 
                    && !kakaoAccount.get("email_needs_agreement").asBoolean()) {
                    accountInfo.put("email", kakaoAccount.get("email").asText());
                }
            }
            
            log.info("카카오 사용자 정보 파싱 완료: ID={}, 닉네임={}", 
                    userInfo.get("id"), userInfo.get("nickname"));
            return userInfo;

        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패", e);
            throw new RuntimeException("카카오 사용자 정보 요청에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카카오 앱 연결 끊기 (동의 페이지를 다시 보기 위해)
     */
    public Map<String, Object> unlinkKakaoApp(String accessToken) {
        String unlinkUrl = apiBaseUrl + "/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    unlinkUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            log.info("카카오 앱 연결 끊기 응답 상태: {}", response.getStatusCode());
            log.debug("카카오 앱 연결 끊기 응답 본문: {}", response.getBody());

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            Map<String, Object> result = new HashMap<>();
            if (jsonNode.has("id")) {
                result.put("unlinked_user_id", jsonNode.get("id").asLong());
                result.put("success", true);
                result.put("message", "카카오 앱 연결이 성공적으로 해제되었습니다. 이제 다시 로그인하면 동의 페이지가 나타납니다.");
                log.info("카카오 앱 연결 끊기 완료: 사용자 ID {}", jsonNode.get("id").asLong());
            } else {
                result.put("success", false);
                result.put("message", "연결 끊기 응답이 예상과 다릅니다.");
            }

            return result;

        } catch (Exception e) {
            log.error("카카오 앱 연결 끊기 실패", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("error", "연결 끊기에 실패했습니다: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 카카오톡 친구 목록 조회 (friends 권한 필요)
     * 이용 중 동의가 필요한 API
     */
    public Map<String, Object> getFriends(String accessToken) {
        String friendsUrl = apiBaseUrl + "/v1/api/talk/friends";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    friendsUrl,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            
            Map<String, Object> friendsInfo = new HashMap<>();
            if (jsonNode.has("total_count")) {
                friendsInfo.put("total_count", jsonNode.get("total_count").asInt());
            }
            
            if (jsonNode.has("elements")) {
                JsonNode elements = jsonNode.get("elements");
                friendsInfo.put("friends_count", elements.size());
                // 실제 친구 목록은 개인정보이므로 개수만 반환
                // 필요시 친구 정보를 파싱할 수 있음
            }

            log.info("카카오톡 친구 정보 조회 완료: {} 명", friendsInfo.get("friends_count"));
            return friendsInfo;

        } catch (Exception e) {
            log.warn("카카오톡 친구 정보 조회 실패 (권한이 없거나 이용 중 동의 필요): {}", e.getMessage());
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("error", "친구 정보 조회 권한이 없습니다. 이용 중 동의가 필요합니다.");
            return emptyResult;
        }
    }
} 