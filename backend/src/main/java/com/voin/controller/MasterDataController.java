package com.voin.controller;

import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.repository.CoinRepository;
import com.voin.repository.KeywordRepository;
import com.voin.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 💎 마스터 데이터 컨트롤러
 * 
 * 이 클래스는 시스템의 기준 데이터를 제공합니다.
 * 
 * 주요 기능들:
 * - 🪙 6개 고정 코인 목록 제공
 * - 🏷️ 55개 키워드 목록 제공
 * - 📊 코인별 키워드 매핑 정보 제공
 * - 🎯 상황 맥락 정보 제공
 * 
 * 이 데이터들은 애플리케이션 시작 시 DataInitializer를 통해 자동 초기화됩니다.
 */
@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@Tag(name = "💎 Master Data", description = "코인, 키워드 등 마스터 데이터")
public class MasterDataController {

    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;

    @Operation(summary = "전체 코인 목록 조회", description = "시스템에 등록된 6개의 고정 코인 목록을 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/coins")
    public ResponseEntity<ApiResponse<List<Coin>>> getAllCoins() {
        try {
            List<Coin> coins = coinRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("코인 목록을 조회했습니다.", coins));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("코인 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "전체 키워드 목록 조회", description = "시스템에 등록된 55개의 키워드 목록을 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<Keyword>>> getAllKeywords() {
        try {
            List<Keyword> keywords = keywordRepository.findAll();
            return ResponseEntity.ok(ApiResponse.success("키워드 목록을 조회했습니다.", keywords));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("키워드 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "특정 코인의 키워드 조회", description = "지정된 코인 ID에 속한 키워드들을 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/coins/{coinId}/keywords")
    public ResponseEntity<ApiResponse<List<Keyword>>> getKeywordsByCoin(
            @PathVariable Long coinId) {
        try {
            List<Keyword> keywords = keywordRepository.findByCoinId(coinId);
            return ResponseEntity.ok(ApiResponse.success("코인별 키워드 목록을 조회했습니다.", keywords));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("코인별 키워드 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "코인과 키워드 전체 매핑 조회", description = "모든 코인과 해당 키워드들의 매핑 정보를 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/coins-with-keywords")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCoinsWithKeywords() {
        try {
            List<Coin> coins = coinRepository.findAll();
            
            Map<String, Object> result = new HashMap<>();
            
            for (Coin coin : coins) {
                List<Keyword> keywords = keywordRepository.findByCoinId(coin.getId());
                
                Map<String, Object> coinInfo = Map.of(
                    "id", coin.getId(),
                    "name", coin.getName(),
                    "description", coin.getDescription(),
                    "color", coin.getColor(),
                    "keywords", keywords.stream()
                        .map(keyword -> Map.of(
                            "id", keyword.getId(),
                            "name", keyword.getName(),
                            "description", keyword.getDescription()
                        ))
                        .collect(Collectors.toList())
                );
                
                result.put(coin.getName(), coinInfo);
            }
            
            return ResponseEntity.ok(ApiResponse.success("코인과 키워드 매핑 정보를 조회했습니다.", Map.of("coins", result)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("코인과 키워드 매핑 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "시스템 통계 정보", description = "등록된 코인, 키워드, 회원, 카드 수 등의 통계 정보를 반환합니다.")
    @SecurityRequirements // 인증 불필요  
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemStats() {
        try {
            long coinCount = coinRepository.count();
            long keywordCount = keywordRepository.count();
            
            Map<String, Object> stats = Map.of(
                "coinCount", coinCount,
                "keywordCount", keywordCount,
                "expectedCoins", 6,
                "expectedKeywords", 55,
                "isDataInitialized", coinCount == 6 && keywordCount == 55
            );
            
            return ResponseEntity.ok(ApiResponse.success("시스템 통계 정보를 조회했습니다.", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("시스템 통계 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 