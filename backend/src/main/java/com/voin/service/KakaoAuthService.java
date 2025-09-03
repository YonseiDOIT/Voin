package com.voin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret:}")
    private String clientSecret;

    @Value("${kakao.api.auth-url}")   // 예: https://kauth.kakao.com
    private String authHost;

    @Value("${kakao.api.base-url}")   // 예: https://kapi.kakao.com
    private String apiBaseUrl;

    @Value("${kakao.redirect-uri}")   // 예: http://localhost:8080/auth/kakao/callback
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 카카오 로그인 인증 URL 생성 (기본)
     */
    public String getKakaoAuthUrl() {
        return getKakaoAuthUrl(false, false, null);
    }

    /**
     * 카카오 로그인 인증 URL 생성 (동의 페이지 강제 표시)
     */
    public String getKakaoAuthUrl(boolean forceConsent) {
        return getKakaoAuthUrl(forceConsent, false, null);
    }

    /**
     * 카카오 로그인 인증 URL 생성 (state 포함/플로우 테스트용)
     */
    public String getKakaoAuthUrl(boolean forceConsent, boolean fromFlow) {
        return getKakaoAuthUrl(forceConsent, fromFlow, null);
    }

    /**
     * 내부 공용: 인증 URL 빌드
     */
    public String getKakaoAuthUrl(boolean forceConsent, boolean fromFlow, String state) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(authHost)
                .path("/oauth/authorize")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "profile_nickname,profile_image"); // 필요 권한만

        if (fromFlow) builder.queryParam("state", "flow_test");
        if (state != null && !state.isBlank()) builder.queryParam("state", state);
        if (forceConsent) builder.queryParam("prompt", "consent"); // 매번 동의

        String url = builder.build(true).toUriString();
        log.info("카카오 인가 URL 생성 완료");
        return url;
    }

    /**
     * 인가 코드로 액세스 토큰 받기
     */
    public String getAccessToken(String code) {
        String tokenUrl = UriComponentsBuilder
                .fromHttpUrl(authHost)
                .path("/oauth/token")
                .build(true)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        if (clientSecret != null && !clientSecret.isBlank()) {
            params.add("client_secret", clientSecret);
            log.debug("클라이언트 시크릿 포함하여 토큰 요청");
        } else {
            log.debug("클라이언트 시크릿 없이 토큰 요청");
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        log.info("카카오 토큰 요청 URL: {}", tokenUrl);
        log.debug("요청 파라미터: grant_type={}, client_id={}, redirect_uri={}, code(앞10)={}",
                params.getFirst("grant_type"), params.getFirst("client_id"),
                params.getFirst("redirect_uri"),
                code.substring(0, Math.min(10, code.length())));

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, request, String.class);

            log.info("카카오 토큰 응답 상태: {}", response.getStatusCode());
            JsonNode json = objectMapper.readTree(response.getBody());

            if (json.has("error")) {
                throw new RuntimeException("카카오 토큰 요청 에러: "
                        + json.get("error").asText() + " - "
                        + (json.has("error_description") ? json.get("error_description").asText() : "설명 없음"));
            }

            String accessToken = json.get("access_token").asText();
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
        String userInfoUrl = UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .path("/v2/user/me")
                .build(true)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        log.info("카카오 사용자 정보 요청 URL: {}", userInfoUrl);
        log.debug("액세스 토큰 길이: {}", accessToken.length());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, request, String.class);

            log.info("카카오 사용자 정보 응답 상태: {}", response.getStatusCode());
            JsonNode json = objectMapper.readTree(response.getBody());

            // 에러 형태 방어
            if (json.has("code") && json.get("code").isInt() && json.get("code").asInt() < 0) {
                String msg = json.has("msg") ? json.get("msg").asText() : "알 수 없는 에러";
                throw new RuntimeException("카카오 사용자 정보 요청 에러: " + msg);
            }

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", json.get("id").asLong());
            if (json.has("connected_at")) userInfo.put("connected_at", json.get("connected_at").asText());

            // legacy properties (있으면 사용)
            if (json.has("properties") && json.get("properties").isObject()) {
                JsonNode props = json.get("properties");
                if (props.has("nickname")) userInfo.put("nickname", props.get("nickname").asText());
                if (props.has("profile_image")) userInfo.put("profile_image", props.get("profile_image").asText());
                if (props.has("thumbnail_image")) userInfo.put("thumbnail_image", props.get("thumbnail_image").asText());
            }

            // kakao_account.profile 선호
            if (json.has("kakao_account") && json.get("kakao_account").isObject()) {
                JsonNode account = json.get("kakao_account");
                Map<String, Object> accountInfo = new HashMap<>();
                userInfo.put("kakao_account", accountInfo);

                if (account.has("profile") && account.get("profile").isObject()) {
                    JsonNode profile = account.get("profile");
                    Map<String, Object> profileInfo = new HashMap<>();
                    if (profile.has("nickname")) profileInfo.put("nickname", profile.get("nickname").asText());
                    if (profile.has("profile_image_url")) profileInfo.put("profile_image_url", profile.get("profile_image_url").asText());
                    if (profile.has("thumbnail_image_url")) profileInfo.put("thumbnail_image_url", profile.get("thumbnail_image_url").asText());
                    accountInfo.put("profile", profileInfo);

                    // 닉네임이 비어있으면 보강
                    userInfo.putIfAbsent("nickname", profileInfo.get("nickname"));
                    userInfo.putIfAbsent("profile_image", profileInfo.get("profile_image_url"));
                }

                if (account.has("email") && account.has("email_needs_agreement")
                        && !account.get("email_needs_agreement").asBoolean()) {
                    accountInfo.put("email", account.get("email").asText());
                }
            }

            log.info("카카오 사용자 정보 파싱 완료: ID={}, 닉네임={}",
                    userInfo.get("id"), userInfo.getOrDefault("nickname", "N/A"));
            return userInfo;

        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패", e);
            throw new RuntimeException("카카오 사용자 정보 요청에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 카카오 앱 연결 끊기 (동의 재표시용)
     */
    public Map<String, Object> unlinkKakaoApp(String accessToken) {
        String unlinkUrl = UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .path("/v1/user/unlink")
                .build(true)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    unlinkUrl, HttpMethod.POST, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            Map<String, Object> result = new HashMap<>();

            if (json.has("id")) {
                result.put("unlinked_user_id", json.get("id").asLong());
                result.put("success", true);
                result.put("message", "카카오 앱 연결이 성공적으로 해제되었습니다.");
                log.info("카카오 앱 연결 끊기 완료: 사용자 ID {}", json.get("id").asLong());
            } else {
                result.put("success", false);
                result.put("message", "연결 끊기 응답이 예상과 다릅니다.");
            }
            return result;

        } catch (Exception e) {
            log.error("카카오 앱 연결 끊기 실패", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "연결 끊기에 실패했습니다: " + e.getMessage());
            return error;
        }
    }

    /**
     * 카카오톡 친구 목록 조회 (friends 권한 필요, 이용 중 동의)
     */
    public Map<String, Object> getFriends(String accessToken) {
        String friendsUrl = UriComponentsBuilder
                .fromHttpUrl(apiBaseUrl)
                .path("/v1/api/talk/friends")
                .build(true)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    friendsUrl, HttpMethod.GET, request, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());
            Map<String, Object> friendsInfo = new HashMap<>();

            if (json.has("total_count")) friendsInfo.put("total_count", json.get("total_count").asInt());
            if (json.has("elements") && json.get("elements").isArray()) {
                friendsInfo.put("friends_count", json.get("elements").size());
            }
            log.info("카카오톡 친구 정보 조회 완료: {} 명", friendsInfo.getOrDefault("friends_count", 0));
            return friendsInfo;

        } catch (Exception e) {
            log.warn("카카오톡 친구 정보 조회 실패 (권한 부족 또는 동의 필요): {}", e.getMessage());
            Map<String, Object> empty = new HashMap<>();
            empty.put("error", "친구 정보 조회 권한이 없습니다. 이용 중 동의가 필요합니다.");
            return empty;
        }
    }
}
