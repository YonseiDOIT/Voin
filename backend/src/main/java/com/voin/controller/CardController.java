package com.voin.controller;

import com.voin.entity.Card;
import com.voin.service.CardService;
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

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "🪙 Card", description = "장점 카드 CRUD 및 관리")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);
    private final CardService cardService;

    @Operation(summary = "카드 조회", description = "카드 ID로 카드 정보를 조회합니다.")
    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCard(
            @Parameter(description = "카드 ID", required = true) @PathVariable Long cardId) {
        Card card = cardService.findById(cardId);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Card>> getCardsByMember(@PathVariable UUID memberId) {
        List<Card> cards = cardService.findByOwnerId(memberId);
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

    @Operation(summary = "내 카드 목록 조회", description = "현재 로그인된 사용자가 생성한 모든 카드를 Story 정보와 함께 조회합니다. 경험 돌아보기 카드의 경우 answer1, answer2 정보도 포함됩니다.")
    @GetMapping("/my-cards")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyCards() {
        try {
            List<Map<String, Object>> myCards = cardService.getMyCardsWithStoryData();
            return ResponseEntity.ok(ApiResponse.success("내 카드를 성공적으로 조회했습니다.", myCards));
        } catch (Exception e) {
            logger.error("Error fetching my cards", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("내 카드 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 