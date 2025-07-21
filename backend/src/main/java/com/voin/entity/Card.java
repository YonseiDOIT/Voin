package com.voin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 사용자가 생성한 장점 카드를 나타내는 엔티티
 * 
 * 카드의 소유권 모델:
 * - creator: 카드를 최초로 생성한 회원 (변경되지 않음)
 * - owner: 현재 카드를 소유한 회원 (선물을 통해 변경 가능)
 * - targetMember: 카드에 기록된 장점의 대상이 되는 회원 (변경되지 않음)
 * - keyword: 카드와 1:1 관계를 맺는 키워드 (1개 카드 = 1개 코인 + 1개 키워드)
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
     * 카드를 최초로 생성한 회원 (변경되지 않음)
     */
    @NotNull(message = "생성자는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_creator"))
    private Member creator;

    /**
     * 현재 카드를 소유한 회원 (선물을 통해 변경 가능)
     */
    @NotNull(message = "소유자는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_owner"))
    private Member owner;

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
     * 선택된 키워드 (1:1 관계 - 1개 카드는 1개 키워드만 가짐)
     */
    @NotNull(message = "키워드는 필수입니다")
    @OneToOne(fetch = FetchType.LAZY)
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
     * 카드가 비공개인지 확인합니다
     */
    public boolean isPrivate() {
        return !isPublic();
    }

    /**
     * 카드가 선물인지 확인합니다
     */
    public boolean isGift() {
        return Boolean.TRUE.equals(this.isGift);
    }

    /**
     * 카드를 선물로 설정합니다
     */
    public void markAsGift() {
        this.isGift = true;
    }

    /**
     * 카드 소유권을 변경합니다 (선물 기능)
     */
    public void transferOwnership(Member newOwner) {
        if (newOwner == null) {
            throw new IllegalArgumentException("새로운 소유자는 null일 수 없습니다");
        }
        this.owner = newOwner;
        this.markAsGift();
    }

    /**
     * 특정 회원이 이 카드의 소유자인지 확인합니다
     */
    public boolean isOwnedBy(Member member) {
        return this.owner != null && this.owner.equals(member);
    }

    /**
     * 특정 회원이 이 카드의 생성자인지 확인합니다
     */
    public boolean isCreatedBy(Member member) {
        return this.creator != null && this.creator.equals(member);
    }

    /**
     * 특정 회원이 이 카드의 대상자인지 확인합니다
     */
    public boolean isTargetedTo(Member member) {
        return this.targetMember != null && this.targetMember.equals(member);
    }

    /**
     * 카드의 코인 정보를 반환합니다 (키워드를 통해 접근)
     */
    public Coin getCoin() {
        return this.keyword != null ? this.keyword.getCoin() : null;
    }

    /**
     * 카드의 코인 이름을 반환합니다
     */
    public String getCoinName() {
        Coin coin = getCoin();
        return coin != null ? coin.getName() : "Unknown";
    }
} 