package com.voin.service;

import com.voin.entity.Card;
import com.voin.entity.Story;
import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.entity.Member;
import com.voin.constant.StoryType;
import com.voin.exception.ResourceNotFoundException;
import com.voin.repository.CardRepository;
import com.voin.repository.StoryRepository;
import com.voin.repository.CoinRepository;
import com.voin.repository.KeywordRepository;
import com.voin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.voin.dto.request.CardCreateRequest;
import com.voin.dto.response.CardResponse;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import com.voin.constant.SituationContext;

/**
 * 🪙 코인(카드) 관리 서비스
 * 
 * 이 클래스는 사용자의 코인(장점 카드)을 관리하는 모든 기능을 담당합니다.
 * 
 * 주요 기능들:
 * - 📖 코인 조회하기 (내 코인 목록, 특정 코인 상세보기)
 * - ✨ 새로운 코인 만들기 (일기 쓰기, 경험 돌아보기 등)
 * - ✏️ 코인 수정하기
 * - 🗑️ 코인 삭제하기
 * - 🔍 코인 검색하기
 * 
 * 쉽게 말해서, 코인과 관련된 모든 일을 처리하는 "코인 관리자" 역할을 해요!
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final StoryRepository storyRepository;
    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;

    public Card findById(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found with id: " + cardId));
    }

    @Transactional(readOnly = true)
    public List<Card> findByOwnerId(UUID ownerId) {
        // 새로운 구조에서는 사용하지 않는 메서드 - 주석처리
        // return cardRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
        throw new UnsupportedOperationException("이 메서드는 더 이상 사용되지 않습니다. findByOwner를 사용하세요.");
    }

    @Transactional(readOnly = true)
    public List<Card> findByCreatorId(UUID creatorId) {
        // 새로운 구조에서는 사용하지 않는 메서드 - 주석처리
        // return cardRepository.findByCreatorIdOrderByCreatedAtDesc(creatorId);
        throw new UnsupportedOperationException("이 메서드는 더 이상 사용되지 않습니다. findByCreator를 사용하세요.");
    }

    @Transactional(readOnly = true)
    public Page<Card> findByOwnerId(UUID ownerId, Pageable pageable) {
        // 새로운 구조에서는 사용하지 않는 메서드 - 주석처리
        // return cardRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
        throw new UnsupportedOperationException("이 메서드는 더 이상 사용되지 않습니다.");
    }

    @Transactional(readOnly = true)
    public Page<Card> searchByContent(String keyword, Pageable pageable) {
        return cardRepository.searchByContentAndIsPublicTrue(keyword, pageable);
    }

    @Transactional(readOnly = true)
    public List<Card> findPublicCards() {
        // 새로운 구조에서는 사용하지 않는 메서드 - 주석처리
        // return cardRepository.findByOwnerIdOrderByCreatedAtDesc(currentMember.getId());
        throw new UnsupportedOperationException("이 메서드는 더 이상 사용되지 않습니다.");
    }

    public List<Card> findMyCards() {
        Member currentMember = getCurrentMember();
        return cardRepository.findByOwnerOrderByCreatedAtDesc(currentMember);
    }

    /**
     * 📋 내 카드 목록을 Story 정보와 함께 조회
     * 
     * 현재 로그인한 사용자가 소유한 모든 카드를 Story 정보와 함께 반환합니다.
     * 경험 돌아보기로 만든 카드의 경우 answer1, answer2 정보도 포함됩니다.
     * 
     * @return 카드와 Story 정보가 포함된 데이터 목록
     */
    public List<Map<String, Object>> getMyCardsWithStoryData() {
        Member currentMember = getCurrentMember();
        List<Card> cards = cardRepository.findByOwnerOrderByCreatedAtDesc(currentMember);
        
        return cards.stream().map(card -> {
            Map<String, Object> cardData = new HashMap<>();
            
            // 기본 카드 정보
            cardData.put("id", card.getId());
            cardData.put("content", card.getContent());
            cardData.put("createdAt", card.getCreatedAt());
            cardData.put("isPublic", card.getIsPublic());
            cardData.put("isGift", card.getIsGift());
            cardData.put("situationContext", card.getSituationContext());
            
            // 키워드 정보
            if (card.getKeyword() != null) {
                Map<String, Object> keywordData = new HashMap<>();
                keywordData.put("id", card.getKeyword().getId());
                keywordData.put("name", card.getKeyword().getName());
                keywordData.put("description", card.getKeyword().getDescription());
                
                // 코인 정보
                if (card.getKeyword().getCoin() != null) {
                    Map<String, Object> coinData = new HashMap<>();
                    coinData.put("id", card.getKeyword().getCoin().getId());
                    coinData.put("name", card.getKeyword().getCoin().getName());
                    coinData.put("description", card.getKeyword().getCoin().getDescription());
                    coinData.put("color", card.getKeyword().getCoin().getColor());
                    keywordData.put("coin", coinData);
                }
                
                cardData.put("keyword", keywordData);
            }
            
            // Story 정보 (경험 돌아보기의 answer1, answer2 포함)
            if (card.getStory() != null) {
                Map<String, Object> storyData = new HashMap<>();
                storyData.put("id", card.getStory().getId());
                storyData.put("title", card.getStory().getTitle());
                storyData.put("content", card.getStory().getContent());
                storyData.put("type", card.getStory().getStoryType().name());
                
                // 경험 돌아보기인 경우 추가 정보 포함
                if (card.getStory().getStoryType() == StoryType.EXPERIENCE_REFLECTION) {
                    if (card.getStory().getAnswer1() != null) {
                        storyData.put("answer1", card.getStory().getAnswer1());
                    }
                    if (card.getStory().getAnswer2() != null) {
                        storyData.put("answer2", card.getStory().getAnswer2());
                    }
                    if (card.getStory().getSituationContext() != null) {
                        storyData.put("situationContext", card.getStory().getSituationContext());
                    }
                }
                
                cardData.put("story", storyData);
            }
            
            return cardData;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Card createCard(CardCreateRequest request) {
        Member currentMember = getCurrentMember();

        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + request.getStoryId()));

        if (request.getKeywordId() == null) {
            throw new IllegalArgumentException("Keyword ID는 필수입니다.");
        }

        Long keywordId = request.getKeywordId();
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new ResourceNotFoundException("Keyword not found with id: " + keywordId));

        if (!keyword.getCoin().getId().equals(request.getCoinId())) {
             throw new IllegalArgumentException("선택된 키워드가 해당 코인에 속해있지 않습니다.");
        }

        // 경험 돌아보기로 생성된 카드인지 확인하여 상황 맥락 설정
        String situationContext = null;
        if (story.getStoryType() == StoryType.EXPERIENCE_REFLECTION) {
            situationContext = story.getSituationContext();
        }

        // 자신에 대한 카드 생성
        Card card = Card.builder()
                .creator(currentMember)
                .owner(currentMember)
                .targetMember(currentMember)
                .story(story)
                .keyword(keyword)
                .content(story.getContent())
                .isPublic(false)
                .isGift(false)
                .situationContext(situationContext)
                .build();
        
        Card savedCard = cardRepository.save(card);
        log.info("Card created: id={}, keyword={}", savedCard.getId(), keyword.getName());
        return savedCard;
    }

    @Transactional
    public Card updateCard(Long cardId, Card updatedCard) {
        Card existingCard = findById(cardId);
        
        Card updatedEntity = Card.builder()
                .id(existingCard.getId())
                .creator(existingCard.getCreator())
                .owner(existingCard.getOwner())
                .targetMember(existingCard.getTargetMember())
                .story(existingCard.getStory())
                .keyword(existingCard.getKeyword())
                .content(updatedCard.getContent() != null ? updatedCard.getContent() : existingCard.getContent())
                .isPublic(updatedCard.getIsPublic() != null ? updatedCard.getIsPublic() : existingCard.getIsPublic())
                .isGift(existingCard.getIsGift())
                .situationContext(existingCard.getSituationContext())
                .build();
        
        return cardRepository.save(updatedEntity);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = findById(cardId);
        cardRepository.delete(card);
        log.info("Deleted card: {}", cardId);
    }

    // ===== 코인 찾기 플로우 메서드들 =====

    @Transactional
    public Long saveDiaryStory(String diaryContent) {
        Member currentMember = getCurrentMember();
        
        // 사용자가 입력한 일기 내용이 비어있으면 기본 메시지를 사용합니다.
        String content = (diaryContent != null && !diaryContent.trim().isEmpty()) 
                                ? diaryContent 
                                : "작성된 일기 내용이 없습니다.";

        Story story = Story.createDiary(
            currentMember.getId(),
            "오늘의 일기",
            content
        );
        
        Story savedStory = storyRepository.save(story);
        
        log.info("Saved diary story: {} for member: {} with content: {}", 
                savedStory.getId(), currentMember.getId(), 
                content.substring(0, Math.min(content.length(), 20)) + "...");
        return savedStory.getId();
    }

    /**
     * 📄 Story 데이터 조회
     * 
     * @param storyId 조회할 Story의 ID
     * @return Story 데이터 Map
     */
    public Map<String, Object> getStoryData(Long storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        
        Map<String, Object> storyData = new HashMap<>();
        storyData.put("id", story.getId());
        storyData.put("title", story.getTitle());
        storyData.put("content", story.getContent());
        storyData.put("type", story.getStoryType().name());
        
        // 경험 돌아보기인 경우 추가 정보 포함
        if (story.getStoryType() == StoryType.EXPERIENCE_REFLECTION) {
            if (story.getSituationContext() != null) {
                storyData.put("situationContext", story.getSituationContext());
            }
            if (story.getAnswer1() != null) {
                storyData.put("answer1", story.getAnswer1());
            }
            if (story.getAnswer2() != null) {
                storyData.put("answer2", story.getAnswer2());
            }
        }
        
        log.info("Retrieved story data for ID: {}, type: {}", storyId, story.getStoryType());
        return storyData;
    }

    // ===== 사례 돌아보기 플로우 메서드들 =====

    /**
     * 🎯 상황 맥락 목록 조회
     * 
     * 사례 돌아보기에서 사용할 수 있는 6가지 상황 맥락을 반환합니다.
     */
    public Map<String, Object> getSituationContexts() {
        List<Map<String, Object>> contexts = new ArrayList<>();
        
        for (SituationContext context : SituationContext.getAll()) {
            Map<String, Object> contextInfo = Map.of(
                "id", context.getId(),
                "subtitle", context.getSubtitle(),
                "title", context.getTitle()
            );
            contexts.add(contextInfo);
        }
        
        return Map.of("contexts", contexts);
    }

    /**
     * 📝 사례 돌아보기 1단계 저장 (상황 맥락 + 행동 질문)
     * 
     * @param situationContextId 선택한 상황 맥락 ID (1~6)
     * @param actionDescription 첫 번째 질문에 대한 답변 (어떤 행동을 했는지)
     * @return 생성된 Story의 ID
     */
    @Transactional
    public Long saveExperienceStep1(Integer situationContextId, String actionDescription) {
        Member currentMember = getCurrentMember();
        
        // 상황 맥락 유효성 검사
        SituationContext situationContext = SituationContext.findById(situationContextId);
        
        Story story = Story.createExperienceReflection(
            currentMember.getId(),
            "경험 돌아보기",
            situationContext.getTitle(),
            actionDescription
        );
        
        Story savedStory = storyRepository.save(story);
        
        log.info("Saved experience step1 story: {} for member: {} with context: {}", 
                savedStory.getId(), currentMember.getId(), situationContext.getTitle());
        return savedStory.getId();
    }

    /**
     * 💭 사례 돌아보기 2단계 저장 (생각 질문)
     * 
     * @param storyId 1단계에서 생성된 Story ID
     * @param thoughtDescription 두 번째 질문에 대한 답변 (행동에 대한 생각)
     */
    @Transactional
    public void saveExperienceStep2(Long storyId, String thoughtDescription) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        
        // answer2 필드에 두 번째 답변 저장
        story.updateAnswer2(thoughtDescription);
        
        storyRepository.save(story);
        
        log.info("Updated experience step2 for story: {} with thought response", storyId);
    }

    @Transactional
    public Long saveFriendStep1(Integer situationId, String friendActionDescription) {
        Member currentMember = getCurrentMember();
        
        Story story = Story.builder()
                .memberId(currentMember.getId())
                .title("함께한 추억 떠올리기")
                .content("친구와의 추억")
                .storyType(StoryType.EXPERIENCE_REFLECTION) // 친구 장점도 경험 돌아보기로 분류
                .situationContext("친구와의 순간 " + situationId)
                .answer1(friendActionDescription)
                .build();
        
        Story savedStory = storyRepository.save(story);
        
        log.info("Saved friend step1 story: {} for member: {}", savedStory.getId(), currentMember.getId());
        return savedStory.getId();
    }

    @Transactional
    public void saveFriendStep2(Long storyId, String friendThoughtDescription) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + storyId));
        
        // answer2 필드에 두 번째 답변 저장
        story.updateAnswer2(friendThoughtDescription);
        
        storyRepository.save(story);
        
        log.info("Updated friend step2 for story: {}", storyId);
    }

    /**
     * 🪙 코인과 키워드 옵션 조회
     * 
     * 사용자가 카드를 생성할 때 선택할 수 있는 모든 코인과 각 코인에 속한 키워드들을 반환합니다.
     */
    public Map<String, Object> getCoinAndKeywordOptions() {
        List<Coin> allCoins = coinRepository.findAllByOrderByName();
        
        Map<String, Object> coinOptions = new HashMap<>();
        
        for (Coin coin : allCoins) {
            List<Keyword> keywords = keywordRepository.findByCoinId(coin.getId());
            
            List<Map<String, Object>> keywordOptions = keywords.stream()
                    .map(keyword -> {
                        Map<String, Object> keywordMap = new HashMap<>();
                        keywordMap.put("id", keyword.getId());
                        keywordMap.put("name", keyword.getName());
                        keywordMap.put("description", keyword.getDescription() != null ? keyword.getDescription() : "");
                        return keywordMap;
                    })
                    .collect(Collectors.toList());
            
            Map<String, Object> coinInfo = new HashMap<>();
            coinInfo.put("id", coin.getId());
            coinInfo.put("name", coin.getName());
            coinInfo.put("description", coin.getDescription() != null ? coin.getDescription() : "");
            coinInfo.put("color", coin.getColor() != null ? coin.getColor() : "#CCCCCC");
            coinInfo.put("keywords", keywordOptions);
            
            coinOptions.put(coin.getName(), coinInfo);
        }
        
        return Map.of("coins", coinOptions);
    }

    /**
     * 스토리로부터 카드 생성
     */
    public CardResponse createCardFromStory(CardCreateRequest request) {
        UUID currentMemberId = getCurrentMemberId();
        
        // 스토리 조회 및 소유자 확인
        Story story = storyRepository.findById(request.getStoryId())
                .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다."));
        
        if (!story.getMemberId().equals(currentMemberId)) {
            throw new RuntimeException("해당 스토리에 대한 권한이 없습니다.");
        }
        
        // 대상 회원 조회 (본인)
        Member targetMember = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
        
        // 키워드 조회
        Keyword keyword = null;
        if (request.getKeywordId() != null) {
            keyword = keywordRepository.findById(request.getKeywordId())
                    .orElseThrow(() -> new RuntimeException("키워드를 찾을 수 없습니다."));
        }
        
        // 카드 생성
        Card card = Card.builder()
                .creator(targetMember)
                .owner(targetMember)
                .targetMember(targetMember)
                .story(story)
                .keyword(keyword)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .build();
        
        Card savedCard = cardRepository.save(card);
        log.info("Card created from story: cardId={}, storyId={}", savedCard.getId(), story.getId());
        
        return convertToCardResponse(savedCard, keyword != null ? List.of(keyword) : new ArrayList<>());
    }

    /**
     * 카드 조회 (소유자 확인 포함)
     */
    public CardResponse getCard(Long cardId) {
        UUID currentMemberId = getCurrentMemberId();
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));
        
        // 공개 카드가 아니고 소유자가 다르면 접근 불가
        if (!card.getIsPublic() && !card.getOwner().getId().equals(currentMemberId)) {
            throw new RuntimeException("해당 카드에 대한 권한이 없습니다.");
        }
        
        return convertToCardResponse(card, new ArrayList<>());
    }

    /**
     * 공개 카드 목록 조회 (페이징)
     */
    public Page<CardResponse> getPublicCards(Pageable pageable) {
        Page<Card> cards = cardRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
        return cards.map(card -> convertToCardResponse(card, new ArrayList<>()));
    }

    /**
     * 카드 공개/비공개 설정 변경
     */
    public CardResponse updateCardVisibility(Long cardId, Boolean isPublic) {
        UUID currentMemberId = getCurrentMemberId();
        
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("카드를 찾을 수 없습니다."));
        
        // 소유자 확인
        if (!card.getOwner().getId().equals(currentMemberId)) {
            throw new RuntimeException("해당 카드에 대한 권한이 없습니다.");
        }
        
        if (Boolean.TRUE.equals(isPublic)) {
            card.makePublic();
        } else {
            card.makePrivate();
        }
        Card updatedCard = cardRepository.save(card);
        
        log.info("Card visibility updated: cardId={}, isPublic={}", cardId, isPublic);
        return convertToCardResponse(updatedCard, new ArrayList<>());
    }



    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
    private UUID getCurrentMemberId() {
        // SecurityContextHolder를 사용하는 것이 좋지만, 기존 세션 방식도 지원
        try {
            String memberId = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication().getName();
            return UUID.fromString(memberId);
        } catch (Exception e) {
            // 세션 방식 fallback
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                Object memberIdObj = session.getAttribute("memberId");
                if (memberIdObj instanceof UUID) {
                    return (UUID) memberIdObj;
                }
            }
            throw new RuntimeException("로그인이 필요합니다.");
        }
    }

    /**
     * Card 엔티티를 CardResponse DTO로 변환
     */
    private CardResponse convertToCardResponse(Card card, List<Keyword> keywords) {
        // 소유자 닉네임 조회
        String ownerNickname = "Unknown";
        try {
            if (card.getOwner() != null) {
                ownerNickname = card.getOwner().getNickname();
            }
        } catch (Exception e) {
            log.warn("Failed to get owner nickname for card: {}", card.getId());
        }
        
        return CardResponse.builder()
                .id(card.getId())
                .ownerId(card.getOwner() != null ? card.getOwner().getId() : null)
                .ownerNickname(ownerNickname)
                .storyId(card.getStory() != null ? card.getStory().getId() : null)
                .coinName(card.getCoinName())
                .keywords(keywords.stream().map(Keyword::getName).collect(Collectors.toList()))
                .content(card.getContent())
                .isPublic(card.getIsPublic())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    /**
     * 현재 로그인한 회원을 조회합니다.
     * 
     * TODO: 실제 인증 시스템이 구현되면 JWT 토큰이나 세션에서 회원 정보를 가져오도록 수정해야 합니다.
     * 현재는 테스트를 위해 세션에서 memberId를 조회하거나, 없으면 첫 번째 회원을 반환합니다.
     */
    private Member getCurrentMember() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            HttpSession session = request.getSession(false);
            
            if (session != null && session.getAttribute("memberId") != null) {
                String memberIdStr = (String) session.getAttribute("memberId");
                UUID memberId = UUID.fromString(memberIdStr);
                return memberRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
            }
        } catch (Exception e) {
            log.warn("Failed to get current member from session: {}", e.getMessage());
        }
        
        // 세션에서 찾을 수 없으면 테스트용으로 첫 번째 회원 반환
        return memberRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No members found in database"));
    }
} 