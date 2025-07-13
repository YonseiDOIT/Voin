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

    public List<Card> findByOwnerId(UUID ownerId) {
        return cardRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    public List<Card> findByCreatorId(UUID creatorId) {
        return cardRepository.findByCreatorIdOrderByCreatedAtDesc(creatorId);
    }

    public Page<Card> findByOwnerId(UUID ownerId, Pageable pageable) {
        return cardRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
    }

    public Page<Card> findPublicCards(Pageable pageable) {
        return cardRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }

    public Page<Card> searchByContent(String keyword, Pageable pageable) {
        return cardRepository.findByContentContainingAndIsPublicTrue(keyword, pageable);
    }

    public List<Card> findMyCards() {
        Member currentMember = getCurrentMember();
        return cardRepository.findByOwnerIdOrderByCreatedAtDesc(currentMember.getId());
    }

    /**
     * 📋 내 카드 목록 조회 (Story 정보 포함)
     * 
     * 사용자가 생성한 모든 카드와 함께 연결된 Story의 상세 정보를 반환합니다.
     * 경험 돌아보기의 경우 answer1, answer2 필드 정보도 포함됩니다.
     * 
     * @return 카드와 Story 정보가 포함된 데이터 목록
     */
    public List<Map<String, Object>> getMyCardsWithStoryData() {
        Member currentMember = getCurrentMember();
        List<Card> cards = cardRepository.findByOwnerIdOrderByCreatedAtDesc(currentMember.getId());
        
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
                storyData.put("storyType", card.getStory().getStoryType().name());
                
                // 경험 돌아보기인 경우 추가 정보 포함
                if (card.getStory().getStoryType() == StoryType.EXPERIENCE_REFLECTION) {
                    if (card.getStory().getSituationContext() != null) {
                        storyData.put("situationContext", card.getStory().getSituationContext());
                    }
                    if (card.getStory().getAnswer1() != null) {
                        storyData.put("answer1", card.getStory().getAnswer1());
                    }
                    if (card.getStory().getAnswer2() != null) {
                        storyData.put("answer2", card.getStory().getAnswer2());
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

        Story story = storyRepository.findById(request.getFormId())
                .orElseThrow(() -> new ResourceNotFoundException("Story not found with id: " + request.getFormId()));

        if (request.getKeywordIds() == null || request.getKeywordIds().isEmpty()) {
            throw new IllegalArgumentException("Keyword ID는 필수입니다.");
        }

        Long keywordId = request.getKeywordIds().get(0);
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
        Card card = Card.createSelfCard(
            currentMember.getId(),
            currentMember,
            story,
            keyword,
            story.getContent()
        );
        
        // 상황 맥락 설정
        if (situationContext != null && !situationContext.trim().isEmpty()) {
            card = Card.builder()
                    .creatorId(card.getCreatorId())
                    .ownerId(card.getOwnerId())
                    .targetMember(card.getTargetMember())
                    .story(card.getStory())
                    .keyword(card.getKeyword())
                    .content(card.getContent())
                    .isPublic(card.getIsPublic())
                    .isGift(card.getIsGift())
                    .situationContext(situationContext)
                    .build();
        }

        Card savedCard = cardRepository.save(card);
        log.info("Created card from story: {} for member: {}", story.getId(), currentMember.getId());
        return savedCard;
    }

    @Transactional
    public Card updateCard(Long cardId, Card updatedCard) {
        Card existingCard = findById(cardId);
        
        Card updatedEntity = Card.builder()
                .id(existingCard.getId())
                .creatorId(existingCard.getCreatorId())
                .ownerId(existingCard.getOwnerId())
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