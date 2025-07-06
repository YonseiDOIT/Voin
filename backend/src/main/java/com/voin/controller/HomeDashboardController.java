package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/home")
@Tag(name = "홈 화면 API", description = "홈 화면 슬라이드 및 네비게이션 API")
public class HomeDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(HomeDashboardController.class);

    @Operation(summary = "홈 대시보드 전체 정보", description = "홈 화면의 모든 슬라이드 정보를 한번에 반환합니다.")
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard() {
        logger.info("Getting home dashboard data");
        
        try {
            Map<String, Object> dashboard = Map.of(
                "slides", List.of(
                    Map.of(
                        "type", "coin_discover", 
                        "title", "코인 찾기", 
                        "subtitle", "오늘 있었던 뿌듯한 순간을 떠올려보세요",
                        "description", "새로운 코인을 발견해보세요"
                    ),
                    Map.of(
                        "type", "most_owned", 
                        "title", "가장 많이 보유한 코인",
                        "description", "당신이 가장 많이 가진 강점"
                    ),
                    Map.of(
                        "type", "recent_coin", 
                        "title", "가장 최근에 찾은 코인",
                        "description", "최근에 발견한 새로운 강점"
                    ),
                    Map.of(
                        "type", "most_shared_friend", 
                        "title", "코인을 가장 많이 나눈 친구",
                        "description", "함께 성장하는 소중한 친구"
                    )
                ),
                "navigation", Map.of(
                    "home", "/api/home/dashboard",
                    "feed", "/api/feed",
                    "archive", "/api/archive"
                )
            );
            
            return ResponseEntity.ok(ApiResponse.success("홈 대시보드 정보를 성공적으로 조회했습니다.", dashboard));
            
        } catch (Exception e) {
            logger.error("Error getting dashboard data", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("대시보드 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "가장 많이 보유한 코인", description = "사용자가 가장 많이 보유한 코인 정보를 반환합니다.")
    @GetMapping("/most-owned-coin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMostOwnedCoin() {
        logger.info("Getting most owned coin data");
        
        try {
            // TODO: 실제 데이터베이스에서 조회하는 로직으로 대체
            Map<String, Object> mostOwnedCoin = Map.of(
                "coinId", 1,
                "coinName", "관리와 성장",
                "count", 15,
                "percentage", 32.5,
                "description", "체계적으로 관리하고 성장하는 능력",
                "recentKeywords", List.of("시간관리", "목표설정", "계획수립"),
                "color", "#4A90E2"
            );
            
            return ResponseEntity.ok(ApiResponse.success("가장 많이 보유한 코인 정보를 조회했습니다.", mostOwnedCoin));
            
        } catch (Exception e) {
            logger.error("Error getting most owned coin", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("코인 정보 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "가장 최근에 찾은 코인", description = "가장 최근에 발견한 코인 정보를 반환합니다.")
    @GetMapping("/recent-coin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecentCoin() {
        logger.info("Getting recent coin data");
        
        try {
            // TODO: 실제 데이터베이스에서 조회하는 로직으로 대체
            Map<String, Object> recentCoin = Map.of(
                "coinId", 3,
                "coinName", "창의와 몰입",
                "discoveredAt", "2024-07-01T10:30:00",
                "keyword", "집중력",
                "description", "창의적 사고와 깊은 몰입을 통한 성과 창출",
                "experience", "프로젝트에 완전히 몰입해서 새로운 아이디어를 구현했다",
                "color", "#F5A623"
            );
            
            return ResponseEntity.ok(ApiResponse.success("최근 발견한 코인 정보를 조회했습니다.", recentCoin));
            
        } catch (Exception e) {
            logger.error("Error getting recent coin", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("최근 코인 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "코인을 가장 많이 나눈 친구", description = "코인을 가장 많이 공유한 친구 정보를 반환합니다.")
    @GetMapping("/most-shared-friend")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMostSharedFriend() {
        logger.info("Getting most shared friend data");
        
        try {
            // TODO: 실제 데이터베이스에서 조회하는 로직으로 대체
            Map<String, Object> mostSharedFriend = Map.of(
                "friendId", 1,
                "nickname", "김철수",
                "profileImage", "/images/profile/default.png",
                "sharedCount", 8,
                "lastSharedAt", "2024-06-30T15:20:00",
                "relationshipDays", 45,
                "recentSharedCoins", List.of("관리와 성장", "감정과 태도"),
                "friendCode", "ABC123"
            );
            
            return ResponseEntity.ok(ApiResponse.success("가장 많이 공유한 친구 정보를 조회했습니다.", mostSharedFriend));
            
        } catch (Exception e) {
            logger.error("Error getting most shared friend", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("친구 정보 조회 중 오류가 발생했습니다."));
        }
    }
} 