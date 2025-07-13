package com.voin.controller;

import com.voin.entity.Card;
import com.voin.service.CardService;
import com.voin.constant.StoryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voin.dto.request.CardCreateRequest;
import com.voin.dto.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@RestController
@RequestMapping("/api/coin-finder")
@RequiredArgsConstructor
@Tag(name = "🎯 Coin Finder", description = "장점 발견 플로우 (일기, 사례 돌아보기 등)")
public class CoinFinderController {

    private static final Logger logger = LoggerFactory.getLogger(CoinFinderController.class);
    private final CardService cardService;

    @Operation(summary = "찾기 유형 선택 옵션", description = "코인 찾기의 3가지 유형 옵션을 반환합니다.")
    @GetMapping("/types")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFinderTypes() {
        logger.info("Getting coin finder types");
        
        try {
            Map<String, Object> myStrengthOptions = new HashMap<>();
            myStrengthOptions.put("type", StoryType.DAILY_DIARY.name());
            myStrengthOptions.put("title", "오늘의 일기");
            myStrengthOptions.put("description", "오늘의 일상을 기록하면서 내 장점을 찾아봐요");
            myStrengthOptions.put("category", "나의 장점 코인");
            
            Map<String, Object> experienceOptions = new HashMap<>();
            experienceOptions.put("type", StoryType.EXPERIENCE_REFLECTION.name());
            experienceOptions.put("title", "사례 돌아보기");
            experienceOptions.put("description", "이전 경험을 돌아보면서 내 장점을 찾아봐요");
            experienceOptions.put("category", "나의 장점 코인");
            
            Map<String, Object> friendOptions = new HashMap<>();
            friendOptions.put("type", "FRIEND_STRENGTH");
            friendOptions.put("title", "함께한 추억 떠올리기");
            friendOptions.put("description", "친구의 장점을 찾아주세요");
            friendOptions.put("category", "친구의 장점 코인");
            
            Map<String, Object> types = new HashMap<>();
            types.put("myStrengthCoins", List.of(myStrengthOptions, experienceOptions));
            types.put("friendStrengthCoins", List.of(friendOptions));
            
            return ResponseEntity.ok(ApiResponse.success("찾기 유형 옵션을 조회했습니다.", types));
            
        } catch (Exception e) {
            logger.error("Error getting finder types", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("찾기 유형 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "상황 맥락 목록 조회", description = "사례 돌아보기에서 선택할 수 있는 6가지 상황 맥락을 반환합니다.")
    @GetMapping("/situation-contexts")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSituationContexts() {
        logger.info("Getting situation contexts for experience reflection");
        
        try {
            Map<String, Object> contexts = cardService.getSituationContexts();
            return ResponseEntity.ok(ApiResponse.success("상황 맥락 목록을 조회했습니다.", contexts));
            
        } catch (Exception e) {
            logger.error("Error getting situation contexts", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("상황 맥락 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "오늘의 일기 저장", description = "오늘의 일기 내용을 Story에 저장합니다.")
    @PostMapping("/daily-diary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveDailyDiary(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        logger.info("Saving daily diary: {}", request);
        
        try {
            // 세션에서 로그인 상태 확인
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
                logger.warn("Unauthorized attempt to save diary - no session");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
            }
            
            String diaryContent = (String) request.get("content");
            
            if (diaryContent == null || diaryContent.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("일기 내용을 입력해주세요."));
            }
            
            Long storyId = cardService.saveDiaryStory(diaryContent);
            
            Map<String, Object> result = new HashMap<>();
            result.put("formId", storyId); // 호환성을 위해 formId로 반환
            result.put("storyId", storyId);
            result.put("type", StoryType.DAILY_DIARY.name());
            result.put("content", diaryContent);
            result.put("createdAt", LocalDateTime.now().toString());
            result.put("nextStep", "direct_selection");
            
            logger.info("Daily diary saved successfully for session: {}", session.getId());
            return ResponseEntity.ok(ApiResponse.success("일기가 저장되었습니다. 직접 찾기 단계로 진행하세요.", result));
            
        } catch (Exception e) {
            logger.error("Error saving daily diary", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일기 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "사례 돌아보기 - 첫 번째 단계", description = "순간의 상황과 첫 번째 질문 답변을 Story에 저장합니다.")
    @PostMapping("/experience-review/step1")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveExperienceReviewStep1(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        logger.info("Saving experience review step 1: {}", request);
        
        try {
            // 세션에서 로그인 상태 확인
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
                logger.warn("Unauthorized attempt to save experience step1 - no session");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
            }
            
            Integer situationContextId = (Integer) request.get("situationContextId");
            String actionDescription = (String) request.get("actionDescription");
            
            if (situationContextId == null || actionDescription == null || actionDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("상황 맥락 선택과 행동 설명을 모두 입력해주세요."));
            }
            
            Long storyId = cardService.saveExperienceStep1(situationContextId, actionDescription);
            
            Map<String, Object> result = new HashMap<>();
            result.put("formId", storyId); // 호환성을 위해 formId로 반환
            result.put("storyId", storyId);
            result.put("type", StoryType.EXPERIENCE_REFLECTION.name());
            result.put("situationContextId", situationContextId);
            result.put("step1Answer", actionDescription);
            result.put("question2", "내 행동에 대해 어떻게 생각하나요?");
            result.put("nextStep", "step2");
            
            return ResponseEntity.ok(ApiResponse.success("첫 번째 단계가 저장되었습니다.", result));
            
        } catch (Exception e) {
            logger.error("Error saving experience review step 1", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("첫 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "사례 돌아보기 - 두 번째 단계", description = "두 번째 질문 답변을 Story에 저장합니다.")
    @PostMapping("/experience-review/step2")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveExperienceReviewStep2(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        logger.info("Saving experience review step 2: {}", request);
        
        try {
            // 세션에서 로그인 상태 확인
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
                logger.warn("Unauthorized attempt to save experience step2 - no session");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
            }
            
            Long storyId = ((Number) request.get("formId")).longValue(); // formId로 받아서 storyId로 사용 (호환성)
            String thoughtDescription = (String) request.get("thoughtDescription");
            
            if (storyId == null || thoughtDescription == null || thoughtDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Story ID와 생각 설명을 모두 입력해주세요."));
            }
            
            cardService.saveExperienceStep2(storyId, thoughtDescription);
            
            Map<String, Object> result = new HashMap<>();
            result.put("formId", storyId); // 호환성을 위해 formId로 반환
            result.put("storyId", storyId);
            result.put("type", StoryType.EXPERIENCE_REFLECTION.name());
            result.put("step2Answer", thoughtDescription);
            result.put("message", "두 번째 단계가 완료되었습니다.");
            result.put("nextStep", "direct_selection");
            
            return ResponseEntity.ok(ApiResponse.success("두 번째 단계가 저장되었습니다. 직접 찾기 단계로 진행하세요.", result));
            
        } catch (Exception e) {
            logger.error("Error saving experience review step 2", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("두 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "코인과 키워드 선택 옵션", description = "직접 찾기에서 선택할 수 있는 코인과 키워드 옵션들을 반환합니다.")
    @GetMapping("/selection-options")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSelectionOptions() {
        logger.info("Getting coin and keyword selection options");
        
        try {
            Map<String, Object> options = cardService.getCoinAndKeywordOptions();
            return ResponseEntity.ok(ApiResponse.success("선택 옵션을 조회했습니다.", options));
            
        } catch (Exception e) {
            logger.error("Error getting selection options", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("선택 옵션 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "최종 카드 생성", description = "선택된 코인과 키워드로 최종 카드를 생성합니다.")
    @PostMapping("/create-card")
    public ResponseEntity<ApiResponse<Card>> createCardFromFinder(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        logger.info("Creating card from coin finder: {}", request);
        
        try {
            // 세션에서 로그인 상태 확인
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
                logger.warn("Unauthorized attempt to create card - no session");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
            }
            
            // Map을 CardCreateRequest로 변환
            CardCreateRequest cardRequest = new CardCreateRequest();
            cardRequest.setFormId(((Number) request.get("formId")).longValue()); // formId를 storyId로 사용
            cardRequest.setCoinId(((Number) request.get("coinId")).longValue());
            
            @SuppressWarnings("unchecked")
            List<Number> keywordNumbers = (List<Number>) request.get("keywordIds");
            List<Long> keywordIds = keywordNumbers.stream()
                    .map(Number::longValue)
                    .toList();
            cardRequest.setKeywordIds(keywordIds);
            
            Card createdCard = cardService.createCard(cardRequest);
            
            logger.info("Card created successfully: {}", createdCard.getId());
            return ResponseEntity.ok(ApiResponse.success("카드가 성공적으로 생성되었습니다.", createdCard));
            
        } catch (Exception e) {
            logger.error("Error creating card from coin finder", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("카드 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "Story 데이터 조회", description = "Story ID로 저장된 Story 데이터를 조회합니다.")
    @GetMapping("/forms/{formId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStoryData(
            @PathVariable Long formId,
            HttpServletRequest httpRequest) {
        
        logger.info("Getting story data for ID: {}", formId);
        
        try {
            // 세션에서 로그인 상태 확인
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
                logger.warn("Unauthorized attempt to get story data - no session");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
            }
            
            Map<String, Object> storyData = cardService.getStoryData(formId);
            
            // 호환성을 위해 formId 필드도 추가
            storyData.put("formId", formId);
            
            return ResponseEntity.ok(ApiResponse.success("Story 데이터를 조회했습니다.", storyData));
            
        } catch (Exception e) {
            logger.error("Error getting story data for ID: {}", formId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Story 데이터 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 