package com.voin.controller;

import com.voin.entity.Card;
import com.voin.service.CardService;
import com.voin.constant.FormType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.voin.dto.request.CardCreateRequest;
import com.voin.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card", description = "카드 관리 및 코인 찾기 API")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);
    private final CardService cardService;

    // ===== 기존 카드 CRUD API =====
    
    @Operation(summary = "카드 조회", description = "카드 ID로 카드 정보를 조회합니다.")
    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCard(
            @Parameter(description = "카드 ID", required = true) @PathVariable Long cardId) {
        Card card = cardService.findById(cardId);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Card>> getCardsByMember(@PathVariable UUID memberId) {
        List<Card> cards = cardService.findByMemberId(memberId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/public")
    public ResponseEntity<Page<Card>> getPublicCards(Pageable pageable) {
        Page<Card> cards = cardService.findPublicCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "카드 검색", description = "키워드로 공개된 카드를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<Card>> searchCards(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(description = "페이징 정보") Pageable pageable) {
        Page<Card> cards = cardService.searchByContent(keyword, pageable);
        return ResponseEntity.ok(cards);
    }

    @Operation(summary = "새로운 카드 생성", description = "플로우를 통해 수집된 정보로 새 카드를 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Card>> createCard(@RequestBody CardCreateRequest request) {
        try {
            Card createdCard = cardService.createCard(request);
            return ResponseEntity.ok(ApiResponse.success("카드가 성공적으로 생성되었습니다.", createdCard));
        } catch (Exception e) {
            logger.error("Error creating card", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("카드 생성에 실패했습니다: " + e.getMessage()));
        }
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(
            @PathVariable Long cardId,
            @RequestBody Card card) {
        Card updatedCard = cardService.updateCard(cardId, card);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 카드 목록 조회", description = "현재 로그인된 사용자가 생성한 모든 카드를 조회합니다.")
    @GetMapping("/my-cards")
    public ResponseEntity<ApiResponse<List<Card>>> getMyCards() {
        try {
            List<Card> myCards = cardService.findMyCards();
            return ResponseEntity.ok(ApiResponse.success("내 카드를 성공적으로 조회했습니다.", myCards));
        } catch (Exception e) {
            logger.error("Error fetching my cards", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("내 카드 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // ===== 코인 찾기 플로우 API =====

    @Operation(summary = "찾기 유형 선택 옵션", description = "코인 찾기의 3가지 유형 옵션을 반환합니다.")
    @GetMapping("/coin-finder/types")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> getFinderTypes() {
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
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("찾기 유형 옵션을 조회했습니다.", types));
            
        } catch (Exception e) {
            logger.error("Error getting finder types", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("찾기 유형 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "순간의 상황 옵션", description = "사례 돌아보기와 추억 떠올리기에서 사용할 상황 옵션들을 반환합니다.")
    @GetMapping("/coin-finder/situations")
    public ResponseEntity<com.voin.dto.response.ApiResponse<List<Map<String, Object>>>> getSituations() {
        logger.info("Getting situation options");
        
        try {
            List<Map<String, Object>> situations = List.of(
                Map.of("id", 1, "title", "평소 내 모습", "description", "일상적인 모습에서 찾는 장점"),
                Map.of("id", 2, "title", "누군가와 상호작용", "description", "다른 사람과의 관계에서 나타나는 장점"),
                Map.of("id", 3, "title", "업무/과제/팀플", "description", "업무나 과제 수행 중 발견되는 장점"),
                Map.of("id", 4, "title", "도전하는 과정", "description", "새로운 도전에서 나타나는 장점"),
                Map.of("id", 5, "title", "배려하고 챙기는..", "description", "남을 배려하고 챙기는 모습에서의 장점"),
                Map.of("id", 6, "title", "기타", "description", "위에 해당하지 않는 특별한 상황")
            );
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("순간의 상황 옵션을 조회했습니다.", situations));
            
        } catch (Exception e) {
            logger.error("Error getting situations", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("상황 옵션 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "오늘의 일기 저장", description = "오늘의 일기 내용을 Form에 저장합니다.")
    @PostMapping("/coin-finder/daily-diary")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> saveDailyDiary(
            @RequestBody Map<String, Object> request) {
        
        logger.info("Saving daily diary: {}", request);
        
        try {
            String diaryContent = (String) request.get("content");
            
            if (diaryContent == null || diaryContent.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(com.voin.dto.response.ApiResponse.error("일기 내용을 입력해주세요."));
            }
            
            Long formId = cardService.saveDiaryForm(diaryContent);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "type", FormType.TODAY_DIARY.name(),
                "content", diaryContent,
                "createdAt", LocalDateTime.now().toString(),
                "nextStep", "direct_selection"
            );
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("일기가 저장되었습니다. 직접 찾기 단계로 진행하세요.", result));
            
        } catch (Exception e) {
            logger.error("Error saving daily diary", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("일기 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "사례 돌아보기 - 첫 번째 단계", description = "순간의 상황과 첫 번째 질문 답변을 Form에 저장합니다.")
    @PostMapping("/coin-finder/experience-review/step1")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> saveExperienceReviewStep1(
            @RequestBody Map<String, Object> request) {
        
        logger.info("Saving experience review step 1: {}", request);
        
        try {
            Integer situationId = (Integer) request.get("situationId");
            String actionDescription = (String) request.get("actionDescription");
            
            if (situationId == null || actionDescription == null || actionDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(com.voin.dto.response.ApiResponse.error("상황 선택과 행동 설명을 모두 입력해주세요."));
            }
            
            Long formId = cardService.saveExperienceStep1(situationId, actionDescription);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "type", FormType.EXPERIENCE_REFLECTION.name(),
                "situationId", situationId,
                "step1Answer", actionDescription,
                "question2", "내 행동에 대해 어떻게 생각하나요?",
                "nextStep", "step2"
            );
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("첫 번째 단계가 저장되었습니다.", result));
            
        } catch (Exception e) {
            logger.error("Error saving experience review step 1", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("첫 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "사례 돌아보기 - 두 번째 단계", description = "두 번째 질문 답변을 Form에 저장합니다.")
    @PostMapping("/coin-finder/experience-review/step2")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> saveExperienceReviewStep2(
            @RequestBody Map<String, Object> request) {
        
        logger.info("Saving experience review step 2: {}", request);
        
        try {
            Long formId = ((Number) request.get("formId")).longValue();
            String thoughtDescription = (String) request.get("thoughtDescription");
            
            if (formId == null || thoughtDescription == null || thoughtDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(com.voin.dto.response.ApiResponse.error("Form ID와 생각 설명을 모두 입력해주세요."));
            }
            
            cardService.saveExperienceStep2(formId, thoughtDescription);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "step2Answer", thoughtDescription,
                "nextStep", "direct_selection",
                "targetType", "self"
            );
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("두 번째 단계가 저장되었습니다. 직접 찾기 단계로 진행하세요.", result));
            
        } catch (Exception e) {
            logger.error("Error saving experience review step 2", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("두 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "함께한 추억 떠올리기 - 첫 번째 단계", description = "친구 관련 순간의 상황과 첫 번째 질문 답변을 Form에 저장합니다.")
    @PostMapping("/coin-finder/memory-recall/step1")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> saveMemoryRecallStep1(
            @RequestBody Map<String, Object> request) {
        
        logger.info("Saving memory recall step 1: {}", request);
        
        try {
            Integer situationId = (Integer) request.get("situationId");
            String friendActionDescription = (String) request.get("friendActionDescription");
            
            if (situationId == null || friendActionDescription == null || friendActionDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(com.voin.dto.response.ApiResponse.error("상황 선택과 친구 행동 설명을 모두 입력해주세요."));
            }
            
            Long formId = cardService.saveFriendStep1(situationId, friendActionDescription);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "type", FormType.FRIEND_STRENGTH.name(),
                "situationId", situationId,
                "step1Answer", friendActionDescription,
                "question2", "그 행동에 대해 어떻게 생각하나요?",
                "nextStep", "step2"
            );
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("첫 번째 단계가 저장되었습니다.", result));
            
        } catch (Exception e) {
            logger.error("Error saving memory recall step 1", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("첫 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "함께한 추억 떠올리기 - 두 번째 단계", description = "친구에 대한 두 번째 질문 답변을 Form에 저장합니다.")
    @PostMapping("/coin-finder/memory-recall/step2")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> saveMemoryRecallStep2(
            @RequestBody Map<String, Object> request) {
        
        logger.info("Saving memory recall step 2: {}", request);
        
        try {
            Long formId = ((Number) request.get("formId")).longValue();
            String friendThoughtDescription = (String) request.get("friendThoughtDescription");
            
            if (formId == null || friendThoughtDescription == null || friendThoughtDescription.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(com.voin.dto.response.ApiResponse.error("Form ID와 친구에 대한 생각을 모두 입력해주세요."));
            }
            
            cardService.saveFriendStep2(formId, friendThoughtDescription);
            
            Map<String, Object> result = Map.of(
                "formId", formId,
                "step2Answer", friendThoughtDescription,
                "nextStep", "direct_selection",
                "targetType", "friend"
            );
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("두 번째 단계가 저장되었습니다. 직접 찾기 단계로 진행하세요.", result));
            
        } catch (Exception e) {
            logger.error("Error saving memory recall step 2", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("두 번째 단계 저장 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "코인과 키워드 선택 옵션", description = "직접 찾기에서 선택할 수 있는 코인과 키워드 옵션들을 반환합니다.")
    @GetMapping("/coin-finder/selection-options")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Map<String, Object>>> getSelectionOptions() {
        logger.info("Getting coin and keyword selection options");
        
        try {
            Map<String, Object> options = cardService.getCoinAndKeywordOptions();
            
            return ResponseEntity.ok(com.voin.dto.response.ApiResponse.success("선택 옵션을 조회했습니다.", options));
            
        } catch (Exception e) {
            logger.error("Error getting selection options", e);
            return ResponseEntity.badRequest()
                .body(com.voin.dto.response.ApiResponse.error("선택 옵션 조회 중 오류가 발생했습니다."));
        }
    }

    @Operation(summary = "최종 카드 생성", description = "선택된 코인과 키워드로 최종 카드를 생성합니다.")
    @PostMapping("/coin-finder/create-card")
    public ResponseEntity<com.voin.dto.response.ApiResponse<Card>> createCardFromFinder(
            @RequestBody Map<String, Object> request) {
        // 이 엔드포인트는 더 이상 사용되지 않으므로, 주요 로직은 /api/cards POST로 통합되었습니다.
        // 호환성을 위해 남겨두거나, 삭제할 수 있습니다.
        // 현재는 비워두겠습니다.
        logger.warn("Deprecated endpoint /api/cards/coin-finder/create-card was called.");
        return ResponseEntity.status(404).body(com.voin.dto.response.ApiResponse.error("This endpoint is deprecated. Please use POST /api/cards instead."));
    }
} 