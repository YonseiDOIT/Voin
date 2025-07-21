package com.voin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voin.constant.StoryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 사용자의 경험이나 일기를 나타내는 스토리 엔티티
 * 카드 생성의 기반이 되는 사용자의 이야기를 저장합니다.
 */
@Entity
@Table(name = "stories",
       indexes = {
           @Index(name = "idx_story_member_id", columnList = "member_id"),
           @Index(name = "idx_story_type", columnList = "story_type"),
           @Index(name = "idx_story_created_at", columnList = "created_at")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Story extends BaseEntity {

    /**
     * 스토리 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 스토리를 작성한 회원
     */
    @NotNull(message = "작성자는 필수입니다")
    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    /**
     * 스토리 제목
     */
    @NotBlank(message = "스토리 제목은 필수입니다")
    @Size(max = 255, message = "스토리 제목은 255자를 초과할 수 없습니다")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    /**
     * 스토리 내용
     */
    @NotBlank(message = "스토리 내용은 필수입니다")
    @Size(max = 5000, message = "스토리 내용은 5000자를 초과할 수 없습니다")
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    /**
     * 스토리 타입 (DAILY_DIARY, EXPERIENCE_REFLECTION)
     */
    @NotNull(message = "스토리 타입은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "story_type", nullable = false, length = 30)
    private StoryType storyType;

    /**
     * 경험 돌아보기의 상황 맥락 (선택적)
     * 경험 돌아보기 타입인 경우에만 사용됩니다.
     */
    @Size(max = 100, message = "상황 맥락은 100자를 초과할 수 없습니다")
    @Column(name = "situation_context", length = 100)
    private String situationContext;

    /**
     * 다단계 질문의 첫 번째 답변 (선택적)
     * 경험 돌아보기, 친구 장점 찾기 등에서 사용됩니다.
     */
    @Size(max = 2000, message = "첫 번째 답변은 2000자를 초과할 수 없습니다")
    @Column(name = "answer1", length = 2000)
    private String answer1;

    /**
     * 다단계 질문의 두 번째 답변 (선택적)
     * 경험 돌아보기, 친구 장점 찾기 등에서 사용됩니다.
     */
    @Size(max = 2000, message = "두 번째 답변은 2000자를 초과할 수 없습니다")
    @Column(name = "answer2", length = 2000)
    private String answer2;

    /**
     * 이 스토리를 통해 생성된 카드들 (양방향 관계)
     */
    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Card> cards = new ArrayList<>();

    // === 비즈니스 메서드 ===

    /**
     * 스토리 정보를 업데이트합니다
     */
    public void updateInfo(String title, String content) {
        if (title != null && !title.trim().isEmpty()) {
            if (title.length() > 255) {
                throw new IllegalArgumentException("스토리 제목은 255자를 초과할 수 없습니다");
            }
            this.title = title.trim();
        }
        if (content != null && !content.trim().isEmpty()) {
            if (content.length() > 5000) {
                throw new IllegalArgumentException("스토리 내용은 5000자를 초과할 수 없습니다");
            }
            this.content = content.trim();
        }
    }

    /**
     * 상황 맥락을 설정합니다 (경험 돌아보기 타입에서만 사용)
     */
    public void setSituationContext(String situationContext) {
        if (situationContext != null && situationContext.length() > 100) {
            throw new IllegalArgumentException("상황 맥락은 100자를 초과할 수 없습니다");
        }
        this.situationContext = situationContext != null ? situationContext.trim() : null;
    }

    /**
     * 두 번째 답변을 업데이트합니다
     */
    public void updateAnswer2(String answer2) {
        if (answer2 != null && !answer2.trim().isEmpty()) {
            if (answer2.length() > 2000) {
                throw new IllegalArgumentException("두 번째 답변은 2000자를 초과할 수 없습니다");
            }
            this.answer2 = answer2.trim();
        }
    }

    /**
     * 특정 타입의 스토리인지 확인합니다
     */
    public boolean isType(StoryType checkType) {
        return this.storyType == checkType;
    }

    /**
     * 카드를 추가합니다 (양방향 관계 유지)
     */
    public void addCard(Card card) {
        cards.add(card);
        // card.setStory(this)는 Card 엔티티에서 처리
    }

    /**
     * 카드를 제거합니다 (양방향 관계 유지)
     */
    public void removeCard(Card card) {
        cards.remove(card);
        // card.setStory(null)는 Card 엔티티에서 처리
    }

    /**
     * 이 스토리로 생성된 카드 개수를 반환합니다
     */
    public int getCardCount() {
        return cards.size();
    }

    /**
     * 이 스토리에 연결된 카드가 있는지 확인합니다
     */
    public boolean hasCards() {
        return !cards.isEmpty();
    }

    /**
     * 일기 타입 스토리인지 확인합니다
     */
    public boolean isDiaryType() {
        return StoryType.DAILY_DIARY == this.storyType;
    }

    /**
     * 경험 돌아보기 타입 스토리인지 확인합니다
     */
    public boolean isExperienceReflectionType() {
        return StoryType.EXPERIENCE_REFLECTION == this.storyType;
    }

    /**
     * 스토리의 간단한 요약을 반환합니다
     */
    public String getSummary() {
        if (content == null || content.trim().isEmpty()) {
            return "내용 없음";
        }
        String trimmedContent = content.trim();
        return trimmedContent.length() > 50 ? 
               trimmedContent.substring(0, 50) + "..." : 
               trimmedContent;
    }

    /**
     * 스토리의 전체 정보를 문자열로 반환합니다
     */
    public String getFullInfo() {
        StringBuilder info = new StringBuilder();
        info.append("[").append(storyType.getDisplayName()).append("] ").append(title);
        if (situationContext != null && !situationContext.trim().isEmpty()) {
            info.append(" (").append(situationContext).append(")");
        }
        return info.toString();
    }

    /**
     * 특정 회원의 스토리인지 확인합니다
     */
    public boolean belongsToMember(UUID checkMemberId) {
        return this.memberId != null && this.memberId.equals(checkMemberId);
    }

    // === 정적 팩토리 메서드 ===

    /**
     * 일기 타입 스토리를 생성합니다
     */
    public static Story createDiary(UUID memberId, String title, String content) {
        return Story.builder()
                .memberId(memberId)
                .title(title)
                .content(content)
                .storyType(StoryType.DAILY_DIARY)
                .build();
    }

    /**
     * 경험 돌아보기 타입 스토리를 생성합니다 (1단계)
     */
    public static Story createExperienceReflection(UUID memberId, String title, String situationContext, String answer1) {
        return Story.builder()
                .memberId(memberId)
                .title(title)
                .content(String.format("상황: %s", situationContext)) // content는 요약용
                .storyType(StoryType.EXPERIENCE_REFLECTION)
                .situationContext(situationContext)
                .answer1(answer1)
                .build();
    }
} 