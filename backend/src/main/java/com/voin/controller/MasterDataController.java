package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.service.MasterDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@Tag(name = "📚 Master Data", description = "기준 정보 조회 (코인, 키워드, 상황맥락)")
public class MasterDataController {

    private final MasterDataService masterDataService;

    /**
     * 모든 코인 목록 조회
     */
    @Operation(summary = "코인 목록 조회", description = "모든 코인 정보를 조회합니다.")
    @GetMapping("/coins")
    public ResponseEntity<ApiResponse<List<Coin>>> getAllCoins() {
        log.info("Getting all coins");
        List<Coin> coins = masterDataService.getAllCoins();
        return ResponseEntity.ok(ApiResponse.success("코인 목록을 조회했습니다.", coins));
    }

    /**
     * 특정 코인의 키워드 목록 조회
     */
    @Operation(summary = "코인별 키워드 조회", description = "특정 코인에 속한 키워드들을 조회합니다.")
    @GetMapping("/coins/{coinId}/keywords")
    public ResponseEntity<ApiResponse<List<Keyword>>> getKeywordsByCoin(
            @Parameter(description = "코인 ID") @PathVariable Long coinId) {
        log.info("Getting keywords for coin: {}", coinId);
        List<Keyword> keywords = masterDataService.getKeywordsByCoin(coinId);
        return ResponseEntity.ok(ApiResponse.success("키워드 목록을 조회했습니다.", keywords));
    }

    /**
     * 모든 키워드 목록 조회
     */
    @Operation(summary = "전체 키워드 조회", description = "모든 키워드를 코인별로 그룹화하여 조회합니다.")
    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<Map<String, List<Keyword>>>> getAllKeywords() {
        log.info("Getting all keywords grouped by coin");
        Map<String, List<Keyword>> keywordsByCoin = masterDataService.getKeywordsGroupedByCoin();
        return ResponseEntity.ok(ApiResponse.success("키워드 목록을 조회했습니다.", keywordsByCoin));
    }

    /**
     * 상황 맥락 옵션 조회 (사례 돌아보기용)
     */
    @Operation(summary = "상황 맥락 조회", description = "사례 돌아보기에서 사용할 상황 맥락 옵션들을 조회합니다.")
    @GetMapping("/situation-contexts")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSituationContexts() {
        log.info("Getting situation contexts");
        List<Map<String, Object>> contexts = masterDataService.getSituationContexts();
        return ResponseEntity.ok(ApiResponse.success("상황 맥락 목록을 조회했습니다.", contexts));
    }

    /**
     * 스토리 타입 옵션 조회
     */
    @Operation(summary = "스토리 타입 조회", description = "스토리 생성 시 선택할 수 있는 타입들을 조회합니다.")
    @GetMapping("/story-types")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStoryTypes() {
        log.info("Getting story types");
        List<Map<String, Object>> storyTypes = masterDataService.getStoryTypes();
        return ResponseEntity.ok(ApiResponse.success("스토리 타입 목록을 조회했습니다.", storyTypes));
    }

    /**
     * 전체 마스터 데이터 조회 (한 번에 모든 기준 정보 조회)
     */
    @Operation(summary = "전체 마스터 데이터", description = "코인, 키워드, 상황맥락 등 모든 기준 정보를 한 번에 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllMasterData() {
        log.info("Getting all master data");
        Map<String, Object> masterData = masterDataService.getAllMasterData();
        return ResponseEntity.ok(ApiResponse.success("마스터 데이터를 조회했습니다.", masterData));
    }
} 