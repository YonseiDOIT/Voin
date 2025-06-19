package com.voin.controller;

import com.voin.entity.Card;
import com.voin.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card", description = "카드 관리 API")
public class CardController {

    private final CardService cardService;

    @Operation(summary = "카드 조회", description = "카드 ID로 카드 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "카드를 찾을 수 없음")
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCard(
            @Parameter(description = "카드 ID", required = true) @PathVariable String cardId) {
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
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<Card>> searchCards(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(description = "페이징 정보") Pageable pageable) {
        Page<Card> cards = cardService.searchByContent(keyword, pageable);
        return ResponseEntity.ok(cards);
    }

    @PostMapping
    public ResponseEntity<Card> createCard(@RequestBody Card card) {
        Card createdCard = cardService.createCard(card);
        return ResponseEntity.ok(createdCard);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(
            @PathVariable String cardId,
            @RequestBody Card card) {
        Card updatedCard = cardService.updateCard(cardId, card);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable String cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }
} 