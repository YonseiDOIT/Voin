package com.voin.controller;

import com.voin.entity.Card;
import com.voin.service.CardService;
import com.voin.constant.FormType;
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
            Map<String, Object> types = Map.of(
                "myStrengthCoins", List.of(
                    Map.of(
                        "type", FormType.TODAY_DIARY.name(),
                        "title", "오늘의 일기",
                        "description", "오늘의 일상을 기록하면서 내 장점을 찾아봐요",
                        "category", "나의 장점 코인"
                    ),
                    Map.of(
                        "type", FormType.EXPERIENCE_REFLECTION.name(),
                        "title", "사례 돌아보기", 
                        "description", "이전 경험을 돌아보면서 내 장점을 찾아봐요",
                        "category", "나의 장점 코인"
                    )
                ),
                "friendStrengthCoins", List.of(
                    Map.of(
                        "type", FormType.FRIEND_STRENGTH.name(),
                        "title", "함께한 추억 떠올리기",
                        "description", "친구의 장점을 찾아주세요",
                        "category", "친구의 장점 코인"
                    )
                )
            );
            
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

    @Operation(summary = "오늘의 일기 저장", description = "오늘의 일기 내용을 Form에 저장합니다.")
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
            
            Long formId = cardService.saveDiaryForm(diaryContent);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "type", FormType.TODAY_DIARY.name(),
                "content", diaryContent,
                "createdAt", LocalDateTime.now().toString(),
                "nextStep", "direct_selection"
            );
            
            logger.info("Daily diary saved successfully for session: {}", session.getId());
            return ResponseEntity.ok(ApiResponse.success("일기가 저장되었습니다. 직접 찾기 단계로 진행하세요.", result));
            
        } catch (Exception e) {
            logger.error("Error saving daily diary", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("일기 저장 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "사례 돌아보기 - 첫 번째 단계", description = "순간의 상황과 첫 번째 질문 답변을 Form에 저장합니다.")
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
            
            Long formId = cardService.saveExperienceStep1(situationContextId, actionDescription);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "type", FormType.EXPERIENCE_REFLECTION.name(),
                "situationContextId", situationContextId,
                "step1Answer", actionDescription,
                "question2", "내 행동에 대해 어떻게 생각하나요?",
                "nextStep", "step2"
            );
            
            return ResponseEntity.ok(ApiResponse.success("첫 번째 단계가 저장되었습니다.", result));
            
        } catch (Exception e) {
            logger.error("Error saving experience review step 1", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("첫 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "사례 돌아보기 - 두 번째 단계", description = "두 번째 질문 답변을 Form에 저장합니다.")
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
            
            Long formId = ((Number) request.get("formId")).longValue();
            String thoughtDescription = (String) request.get("thoughtDescription");
            
            if (formId == null || thoughtDescription == null || thoughtDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Form ID와 생각 설명을 모두 입력해주세요."));
            }
            
            cardService.saveExperienceStep2(formId, thoughtDescription);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "step2Answer", thoughtDescription,
                "nextStep", "direct_selection",
                "targetType", "self"
            );
            
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
            
            // CardCreateRequest로 변환
            CardCreateRequest cardRequest = new CardCreateRequest();
            cardRequest.setFormId(((Number) request.get("formId")).longValue());
            cardRequest.setCoinId(((Number) request.get("coinId")).longValue());
            cardRequest.setKeywordIds(List.of(((Number) request.get("keywordId")).longValue()));
            
            Card createdCard = cardService.createCard(cardRequest);
            
            return ResponseEntity.ok(ApiResponse.success("카드가 성공적으로 생성되었습니다.", createdCard));
            
        } catch (Exception e) {
            logger.error("Error creating card from finder", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("카드 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @Operation(summary = "Form 데이터 조회", description = "Form ID로 저장된 Form 데이터를 조회합니다.")
    @GetMapping("/forms/{formId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFormData(
            @PathVariable Long formId,
            HttpServletRequest httpRequest) {
        
        logger.info("Getting form data for ID: {}", formId);
        
        try {
            // 세션에서 로그인 상태 확인
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("memberId") == null) {
                logger.warn("Unauthorized attempt to get form data - no session");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("로그인이 필요합니다."));
            }
            
            Map<String, Object> formData = cardService.getFormData(formId);
            
            if (formData == null) {
                return ResponseEntity.status(404)
                    .body(ApiResponse.error("Form을 찾을 수 없습니다."));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Form 데이터를 조회했습니다.", formData));
            
        } catch (Exception e) {
            logger.error("Error getting form data for ID: {}", formId, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Form 데이터 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 