package com.voin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * 사용자가 생성한 장점 카드를 나타내는 엔티티
 * 
 * 카드의 소유권 모델:
 * - creatorId: 카드를 최초로 생성한 회원 (변경되지 않음)
 * - ownerId: 현재 카드를 소유한 회원 (선물을 통해 변경 가능)
 * - targetMember: 카드에 기록된 장점의 대상이 되는 회원 (변경되지 않음)
 */
@Entity
@Table(name = "cards",
       indexes = {
           @Index(name = "idx_card_creator_id", columnList = "creator_id"),
           @Index(name = "idx_card_owner_id", columnList = "owner_id"),
           @Index(name = "idx_card_target_member_id", columnList = "target_member_id"),
           @Index(name = "idx_card_story_id", columnList = "story_id"),
           @Index(name = "idx_card_keyword_id", columnList = "keyword_id"),
           @Index(name = "idx_card_public", columnList = "is_public")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Card extends BaseEntity {

    /**
     * 카드 고유 식별자 (자동 증가 숫자 ID)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 카드를 최초로 생성한 회원의 ID (변경되지 않음)
     */
    @NotNull(message = "생성자는 필수입니다")
    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    /**
     * 현재 카드를 소유한 회원의 ID (선물을 통해 변경 가능)
     */
    @NotNull(message = "소유자는 필수입니다")
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    /**
     * 카드의 대상이 되는 회원 (장점을 받는 사람)
     */
    @NotNull(message = "대상 회원은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_target_member"))
    private Member targetMember;

    /**
     * 카드 생성에 사용된 스토리
     */
    @NotNull(message = "스토리는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_story"))
    private Story story;

    /**
     * 선택된 키워드
     */
    @NotNull(message = "키워드는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_keyword"))
    private Keyword keyword;

    /**
     * 카드의 주요 내용
     */
    @Size(max = 1000, message = "내용은 1000자를 초과할 수 없습니다")
    @Column(name = "content", length = 1000)
    private String content;

    /**
     * 카드 공개 여부
     */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    /**
     * 카드가 선물인지 여부
     */
    @Column(name = "is_gift", nullable = false)
    @Builder.Default
    private Boolean isGift = false;

    /**
     * 카드가 생성된 상황 맥락 (경험 돌아보기에서 사용)
     * 예: "일상적 행동, 습관", "다른 사람과 대화, 행동" 등
     */
    @Size(max = 100, message = "상황 맥락은 100자를 초과할 수 없습니다")
    @Column(name = "situation_context", length = 100)
    private String situationContext;

    // === 비즈니스 메서드 ===

    /**
     * 카드 내용을 업데이트합니다
     */
    public void updateContent(String newContent) {
        if (newContent != null) {
            if (newContent.length() > 1000) {
                throw new IllegalArgumentException("내용은 1000자를 초과할 수 없습니다");
            }
            this.content = newContent.trim();
        }
    }

    /**
     * 카드를 공개로 설정합니다
     */
    public void makePublic() {
        this.isPublic = true;
    }

    /**
     * 카드를 비공개로 설정합니다
     */
    public void makePrivate() {
        this.isPublic = false;
    }

    /**
     * 공개 여부를 토글합니다
     */
    public void togglePublic() {
        this.isPublic = !Boolean.TRUE.equals(this.isPublic);
    }

    /**
     * 카드가 공개인지 확인합니다
     */
    public boolean isPublic() {
        return Boolean.TRUE.equals(this.isPublic);
    }

    /**
     * 카드가 선물인지 확인합니다
     */
    public boolean isGift() {
        return Boolean.TRUE.equals(this.isGift);
    }

    /**
     * 생성자와 소유자가 같은 카드인지 확인합니다 (본인이 만들고 소유한 카드)
     */
    public boolean isOwnCard() {
        return creatorId != null && ownerId != null && creatorId.equals(ownerId);
    }

    /**
     * 생성자와 대상이 같은 카드인지 확인합니다 (자신에 대한 카드)
     */
    public boolean isSelfCard() {
        return targetMember != null && creatorId != null && 
               targetMember.getId().equals(creatorId);
    }

    /**
     * 특정 회원이 이 카드의 생성자인지 확인합니다
     */
    public boolean isCreatedBy(UUID memberId) {
        return creatorId != null && creatorId.equals(memberId);
    }

    /**
     * 특정 회원이 이 카드의 소유자인지 확인합니다
     */
    public boolean isOwnedBy(UUID memberId) {
        return ownerId != null && ownerId.equals(memberId);
    }

    /**
     * 특정 회원이 이 카드의 대상인지 확인합니다
     */
    public boolean isTargetOf(Member checkMember) {
        return targetMember != null && checkMember != null && 
               targetMember.getId().equals(checkMember.getId());
    }

    /**
     * 카드를 다른 회원에게 선물합니다
     */
    public void giftTo(UUID newOwnerId) {
        if (newOwnerId == null) {
            throw new IllegalArgumentException("새로운 소유자 ID는 null일 수 없습니다");
        }
        if (newOwnerId.equals(this.ownerId)) {
            throw new IllegalArgumentException("이미 해당 회원의 소유입니다");
        }
        
        this.ownerId = newOwnerId;
        this.isGift = true;
    }

    /**
     * 카드의 키워드가 속한 코인 이름을 반환합니다
     */
    public String getCoinName() {
        return keyword != null && keyword.getCoin() != null ? 
               keyword.getCoin().getName() : null;
    }

    /**
     * 카드의 간단한 정보를 문자열로 반환합니다
     */
    public String getCardInfo() {
        String keywordName = keyword != null ? keyword.getName() : "알 수 없음";
        String targetName = targetMember != null ? targetMember.getNickname() : "알 수 없음";
        String giftStatus = isGift() ? "(선물)" : "";
        return String.format("[%s] %s님의 카드 %s", keywordName, targetName, giftStatus);
    }

    // === 정적 팩토리 메서드 ===

    /**
     * 자신에 대한 카드를 생성합니다
     */
    public static Card createSelfCard(UUID creatorId, Member targetMember, Story story, Keyword keyword, String content) {
        return Card.builder()
                .creatorId(creatorId)
                .ownerId(creatorId) // 생성자가 소유자
                .targetMember(targetMember)
                .story(story)
                .keyword(keyword)
                .content(content)
                .isPublic(false)
                .isGift(false)
                .build();
    }

    /**
     * 다른 사람에 대한 카드를 생성합니다
     */
    public static Card createForOther(UUID creatorId, Member targetMember, Story story, Keyword keyword, String content) {
        return Card.builder()
                .creatorId(creatorId)
                .ownerId(targetMember.getId()) // 대상이 소유자
                .targetMember(targetMember)
                .story(story)
                .keyword(keyword)
                .content(content)
                .isPublic(false)
                .isGift(true) // 다른 사람을 위한 카드는 선물
                .build();
    }
} 