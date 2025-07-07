package com.voin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 사용자가 생성한 장점 카드를 나타내는 엔티티
 */
@Entity
@Table(name = "cards",
       indexes = {
           @Index(name = "idx_card_member_id", columnList = "member_id"),
           @Index(name = "idx_card_target_member_id", columnList = "target_member_id"),
           @Index(name = "idx_card_form_id", columnList = "form_id"),
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
     * 카드를 작성한 회원
     */
    @NotNull(message = "작성자는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_member"))
    private Member member;

    /**
     * 카드의 대상이 되는 회원 (장점을 받는 사람)
     */
    @NotNull(message = "대상 회원은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_target_member"))
    private Member targetMember;

    /**
     * 카드 생성에 사용된 폼
     */
    @NotNull(message = "폼은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_card_form"))
    private Form form;

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
     * 폼 작성 응답 내용 (JSON 형태 등)
     */
    @Column(name = "form_response", columnDefinition = "TEXT")
    private String formResponse;

    /**
     * 카드 공개 여부
     */
    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = false;

    /**
     * 카드가 생성된 상황 맥락 (사례 돌아보기에서 사용)
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
     * 폼 응답을 업데이트합니다
     */
    public void updateFormResponse(String newFormResponse) {
        this.formResponse = newFormResponse;
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
     * 작성자와 대상이 같은 카드인지 확인합니다 (자신에 대한 카드)
     */
    public boolean isSelfCard() {
        return member != null && targetMember != null && 
               member.getId().equals(targetMember.getId());
    }

    /**
     * 특정 회원이 이 카드의 작성자인지 확인합니다
     */
    public boolean isWrittenBy(Member checkMember) {
        return member != null && checkMember != null && 
               member.getId().equals(checkMember.getId());
    }

    /**
     * 특정 회원이 이 카드의 대상인지 확인합니다
     */
    public boolean isTargetOf(Member checkMember) {
        return targetMember != null && checkMember != null && 
               targetMember.getId().equals(checkMember.getId());
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
        return String.format("[%s] %s님의 카드", keywordName, targetName);
    }
} 