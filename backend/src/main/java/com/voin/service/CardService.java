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
import com.fasterxml.jackson.databind.ObjectMapper;
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

        // 사례 돌아보기로 생성된 카드인지 확인하여 상황 맥락 설정
        String situationContext = null;
        if (form.getType() == FormType.EXPERIENCE_REFLECTION && form.getFormResponse() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseData = objectMapper.readValue(form.getFormResponse(), Map.class);
                situationContext = (String) responseData.get("situationContextTitle");
            } catch (Exception e) {
                log.warn("Failed to parse form response for situation context: {}", e.getMessage());
            }
        }

        Card card = Card.builder()
                .member(currentMember)
                .targetMember(currentMember) // '나의 장점'이므로 대상도 자신
                .form(form)
                .keyword(keyword)
                .content(form.getDescription())
                .situationContext(situationContext)
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
        
        // 사용자가 입력한 일기 내용이 비어있으면 기본 메시지를 사용합니다.
        String description = (diaryContent != null && !diaryContent.trim().isEmpty()) 
                                ? diaryContent 
                                : "작성된 일기 내용이 없습니다.";

        Form form = Form.builder()
                .title("오늘의 일기")
                .description(description) // 실제 일기 내용을 저장합니다.
                .type(FormType.TODAY_DIARY)
                .build();
        
        Form savedForm = formRepository.save(form);
        
        log.info("Saved diary form: {} for member: {} with content: {}", 
                savedForm.getId(), currentMember.getId(), 
                description.substring(0, Math.min(description.length(), 20)) + "...");
        return savedForm.getId();
    }

    /**
     * 📄 Form 데이터 조회
     * 
     * @param formId 조회할 Form의 ID
     * @return Form 데이터 Map
     */
    public Map<String, Object> getFormData(Long formId) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));
        
        Map<String, Object> formData = new HashMap<>();
        formData.put("id", form.getId());
        formData.put("title", form.getTitle());
        formData.put("description", form.getDescription());
        formData.put("type", form.getType().name());
        
        // 사례 돌아보기인 경우 상세 응답 데이터 포함
        if (form.getType() == FormType.EXPERIENCE_REFLECTION && form.getFormResponse() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseData = objectMapper.readValue(form.getFormResponse(), Map.class);
                formData.put("responseData", responseData);
            } catch (Exception e) {
                log.warn("Failed to parse form response for form {}: {}", formId, e.getMessage());
            }
        }
        
        log.info("Retrieved form data for ID: {}, type: {}", formId, form.getType());
        return formData;
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
     * @return 생성된 Form의 ID
     */
    @Transactional
    public Long saveExperienceStep1(Integer situationContextId, String actionDescription) {
        Member currentMember = getCurrentMember();
        
        // 상황 맥락 유효성 검사
        SituationContext situationContext = SituationContext.findById(situationContextId);
        
        // JSON 형태로 1단계 응답 저장
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("situationContextId", situationContextId);
        responseData.put("situationContextTitle", situationContext.getTitle());
        responseData.put("action", actionDescription);
        
        String formResponse;
        try {
            formResponse = objectMapper.writeValueAsString(responseData);
        } catch (Exception e) {
            throw new RuntimeException("JSON 변환 중 오류가 발생했습니다.", e);
        }
        
        Form form = Form.builder()
                .title("사례 돌아보기")
                .description("상황: " + situationContext.getTitle() + " / 행동: " + actionDescription)
                .type(FormType.EXPERIENCE_REFLECTION)
                .formResponse(formResponse)
                .build();
        
        Form savedForm = formRepository.save(form);
        
        log.info("Saved experience step1 form: {} for member: {} with context: {}", 
                savedForm.getId(), currentMember.getId(), situationContext.getTitle());
        return savedForm.getId();
    }

    /**
     * 💭 사례 돌아보기 2단계 저장 (생각 질문)
     * 
     * @param formId 1단계에서 생성된 Form ID
     * @param thoughtDescription 두 번째 질문에 대한 답변 (행동에 대한 생각)
     */
    @Transactional
    public void saveExperienceStep2(Long formId, String thoughtDescription) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("Form not found with id: " + formId));
        
        // 기존 JSON 응답에 2단계 응답 추가
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> responseData = objectMapper.readValue(form.getFormResponse(), Map.class);
            responseData.put("thought", thoughtDescription);
            
            String updatedFormResponse = objectMapper.writeValueAsString(responseData);
            
            // 새로운 Form 객체를 만들어 저장 (불변 객체 패턴)
            Form updatedForm = Form.builder()
                    .id(form.getId())
                    .title(form.getTitle())
                    .description(form.getDescription() + " / 생각: " + thoughtDescription)
                    .type(form.getType())
                    .formResponse(updatedFormResponse)
                    .build();
            
            formRepository.save(updatedForm);
            
            log.info("Updated experience step2 for form: {} with thought response", formId);
        } catch (Exception e) {
            throw new RuntimeException("JSON 처리 중 오류가 발생했습니다.", e);
        }
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
        // 세션에서 현재 사용자 ID 가져오기 시도
        try {
            // RequestContextHolder를 통해 현재 HTTP 세션에 접근
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            HttpSession session = request.getSession(false);
            
            if (session != null) {
                Object memberIdObj = session.getAttribute("memberId");
                if (memberIdObj != null) {
                    UUID memberId = (UUID) memberIdObj;
                    Optional<Member> member = memberRepository.findById(memberId);
                    if (member.isPresent()) {
                        log.info("세션에서 현재 사용자 조회: {} ({})", member.get().getNickname(), member.get().getId());
                        return member.get();
                    }
                }
            }
        } catch (Exception e) {
            log.warn("세션에서 사용자 정보를 가져올 수 없음: {}", e.getMessage());
        }
        
        // 세션에서 가져올 수 없는 경우 임시로 첫 번째 회원 사용
        List<Member> members = memberRepository.findAll();
        
        if (members.isEmpty()) {
            throw new RuntimeException("데이터베이스에 등록된 회원이 없습니다. 먼저 카카오 로그인을 통해 회원가입을 완료해주세요.");
        }
        
        Member firstMember = members.get(0);
        log.info("세션 정보 없음 - 임시로 첫 번째 회원 사용: {} ({})", firstMember.getNickname(), firstMember.getId());
        return firstMember;
    }
} 