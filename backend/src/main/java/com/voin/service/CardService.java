package com.voin.service;

import com.voin.entity.Card;
import com.voin.exception.ResourceNotFoundException;
import com.voin.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;

    public Card findById(String cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
    }

    public List<Card> findByMemberId(UUID memberId) {
        return cardRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public Page<Card> findByMemberId(UUID memberId, Pageable pageable) {
        return cardRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
    }

    public Page<Card> findPublicCards(Pageable pageable) {
        return cardRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }

    public Page<Card> searchByContent(String keyword, Pageable pageable) {
        return cardRepository.findByContentContainingAndIsPublicTrue(keyword, pageable);
    }

    @Transactional
    public Card createCard(Card card) {
        Card savedCard = cardRepository.save(card);
        log.info("Created new card: {} for member: {}", savedCard.getId(), savedCard.getMember().getId());
        return savedCard;
    }

    @Transactional
    public Card updateCard(String cardId, Card updatedCard) {
        Card existingCard = findById(cardId);
        
        Card updatedEntity = Card.builder()
                .id(existingCard.getId())
                .member(existingCard.getMember())
                .targetMember(existingCard.getTargetMember())
                .form(existingCard.getForm())
                .keyword(existingCard.getKeyword())
                .content(updatedCard.getContent() != null ? updatedCard.getContent() : existingCard.getContent())
                .formResponse(existingCard.getFormResponse())
                .isPublic(updatedCard.getIsPublic() != null ? updatedCard.getIsPublic() : existingCard.getIsPublic())
                .build();
        
        return cardRepository.save(updatedEntity);
    }

    @Transactional
    public void deleteCard(String cardId) {
        Card card = findById(cardId);
        cardRepository.delete(card);
        log.info("Deleted card: {}", cardId);
    }
} 