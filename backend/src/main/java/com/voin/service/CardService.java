package com.voin.service;

import com.voin.entity.Card;
import com.voin.entity.Form;
import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.entity.Member;
import com.voin.constant.FormType;
import com.voin.exception.ResourceNotFoundException;
import com.voin.repository.CardRepository;
import com.voin.repository.FormRepository;
import com.voin.repository.CoinRepository;
import com.voin.repository.KeywordRepository;
import com.voin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.voin.dto.request.CardCreateRequest;


import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final FormRepository formRepository;
    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;

    public Card findById(Long cardId) {
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

    public List<Card> findMyCards() {
        Member currentMember = getCurrentMember();
        return cardRepository.findByMemberIdOrderByCreatedAtDesc(currentMember.getId());
    }

    @Transactional
    public Card createCard(CardCreateRequest request) {
        Member currentMember = getCurrentMember();

        Form form = formRepository.findById(request.getFormId())
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + request.getFormId()));

        if (request.getKeywordIds() == null || request.getKeywordIds().isEmpty()) {
            throw new IllegalArgumentException("Keyword ID는 필수입니다.");
        }

        Long keywordId = request.getKeywordIds().get(0);
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new ResourceNotFoundException("Keyword not found with id: " + keywordId));

        if (!keyword.getCoin().getId().equals(request.getCoinId())) {
             throw new IllegalArgumentException("선택된 키워드가 해당 코인에 속해있지 않습니다.");
        }

        Card card = Card.builder()
                .member(currentMember)
                .targetMember(currentMember) // '나의 장점'이므로 대상도 자신
                .form(form)
                .keyword(keyword)
                .content(form.getDescription())
                .isPublic(false)
                .build();

        Card savedCard = cardRepository.save(card);
        log.info("Created card from request for member: {}", currentMember.getId());
        return savedCard;
    }

    @Transactional
    public Card updateCard(Long cardId, Card updatedCard) {
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
    public void deleteCard(Long cardId) {
        Card card = findById(cardId);
        cardRepository.delete(card);
        log.info("Deleted card: {}", cardId);
    }

    // ===== 코인 찾기 플로우 메서드들 =====

    @Transactional
    public Long saveDiaryForm(String diaryContent) {
        Member currentMember = getCurrentMember();
        
        Form form = Form.builder()
                .title("오늘의 일기")
                .description("오늘의 일상을 기록한 일기")
                .type(FormType.TODAY_DIARY)
                .build();
        
        Form savedForm = formRepository.save(form);
        
        // TODO: Form 엔티티에 formResponse 필드가 있다면 여기서 설정
        // savedForm.setFormResponse(diaryContent);
        
        log.info("Saved diary form: {} for member: {}", savedForm.getId(), currentMember.getId());
        return savedForm.getId();
    }

    @Transactional
    public Long saveExperienceStep1(Integer situationId, String actionDescription) {
        Member currentMember = getCurrentMember();
        
        Form form = Form.builder()
                .title("사례 돌아보기 - 1단계")
                .description("순간의 상황: " + situationId + ", 행동: " + actionDescription)
                .type(FormType.EXPERIENCE_REFLECTION)
                .build();
        
        Form savedForm = formRepository.save(form);
        
        log.info("Saved experience step1 form: {} for member: {}", savedForm.getId(), currentMember.getId());
        return savedForm.getId();
    }

    @Transactional
    public void saveExperienceStep2(Long formId, String thoughtDescription) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));
        
        // TODO: Form 엔티티에 step2 응답을 저장할 방법 구현
        // form.setStep2Response(thoughtDescription);
        
        log.info("Updated experience step2 for form: {}", formId);
    }

    @Transactional
    public Long saveFriendStep1(Integer situationId, String friendActionDescription) {
        Member currentMember = getCurrentMember();
        
        Form form = Form.builder()
                .title("함께한 추억 떠올리기 - 1단계")
                .description("순간의 상황: " + situationId + ", 친구 행동: " + friendActionDescription)
                .type(FormType.FRIEND_STRENGTH)
                .build();
        
        Form savedForm = formRepository.save(form);
        
        log.info("Saved friend step1 form: {} for member: {}", savedForm.getId(), currentMember.getId());
        return savedForm.getId();
    }

    @Transactional
    public void saveFriendStep2(Long formId, String friendThoughtDescription) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));
        
        // TODO: Form 엔티티에 step2 응답을 저장할 방법 구현
        // form.setStep2Response(friendThoughtDescription);
        
        log.info("Updated friend step2 for form: {}", formId);
    }

    public Map<String, Object> getCoinAndKeywordOptions() {
        List<Coin> coins = coinRepository.findAll();
        
        Map<String, Object> options = new HashMap<>();
        
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
            
            options.put(coin.getName(), coinInfo);
        }
        
        return Map.of("coins", options);
    }

    /**
     * 🙋‍♀️ 현재 접속한 사용자 정보 가져오기
     * 
     * 지금 웹사이트를 사용하고 있는 사람이 누구인지 알아내는 기능입니다.
     * 
     * ⚠️ 현재는 임시로 첫 번째 회원을 사용합니다.
     * 나중에 실제 로그인 기능이 완성되면 진짜 로그인한 사용자를 찾도록 바뀔 예정이에요!
     * 
     * @return 현재 사용자의 정보
     * @throws RuntimeException 회원이 아무도 없을 때 발생하는 오류
     */
    private Member getCurrentMember() {
        // 📝 모든 회원 목록을 가져옵니다
        List<Member> members = memberRepository.findAll();
        
        // 🔍 회원이 한 명도 없다면 오류 메시지를 보여줍니다
        if (members.isEmpty()) {
            throw new RuntimeException("데이터베이스에 등록된 회원이 없습니다. 먼저 카카오 로그인을 통해 회원가입을 완료해주세요.");
        }
        
        // 👤 임시로 첫 번째 회원을 현재 사용자로 설정합니다
        Member firstMember = members.get(0);
        log.info("테스트용으로 첫 번째 회원 사용 중: {} ({})", firstMember.getNickname(), firstMember.getId());
        return firstMember;
    }
} 