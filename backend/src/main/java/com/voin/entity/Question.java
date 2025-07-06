package com.voin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 폼에 포함되는 질문을 나타내는 엔티티
 */
@Entity
@Table(name = "questions",
       indexes = {
           @Index(name = "idx_question_form_id", columnList = "form_id"),
           @Index(name = "idx_question_order", columnList = "form_id, order_index")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Question extends BaseEntity {

    /**
     * 질문 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 질문이 속한 폼
     */
    @NotNull(message = "폼은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "form_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_question_form"))
    private Form form;

    /**
     * 질문 내용
     */
    @NotBlank(message = "질문 내용은 필수입니다")
    @Size(max = 500, message = "질문 내용은 500자를 초과할 수 없습니다")
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    /**
     * 질문 순서 (1부터 시작)
     */
    @NotNull(message = "순서는 필수입니다")
    @Min(value = 1, message = "순서는 1 이상이어야 합니다")
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    // === 비즈니스 메서드 ===

    /**
     * 질문 내용을 업데이트합니다
     */
    public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("질문 내용은 빈 값일 수 없습니다");
        }
        if (newContent.length() > 500) {
            throw new IllegalArgumentException("질문 내용은 500자를 초과할 수 없습니다");
        }
        this.content = newContent.trim();
    }

    /**
     * 질문 순서를 변경합니다
     */
    public void updateOrder(int newOrder) {
        if (newOrder < 1) {
            throw new IllegalArgumentException("순서는 1 이상이어야 합니다");
        }
        this.orderIndex = newOrder;
    }

    /**
     * 폼을 변경합니다
     */
    public void changeForm(Form newForm) {
        if (newForm == null) {
            throw new IllegalArgumentException("폼은 null일 수 없습니다");
        }
        this.form = newForm;
    }

    /**
     * 특정 순서의 질문인지 확인합니다
     */
    public boolean isOrder(int checkOrder) {
        return this.orderIndex != null && this.orderIndex.equals(checkOrder);
    }

    /**
     * 첫 번째 질문인지 확인합니다
     */
    public boolean isFirstQuestion() {
        return isOrder(1);
    }

    /**
     * 특정 폼에 속하는 질문인지 확인합니다
     */
    public boolean belongsToForm(Form checkForm) {
        return this.form != null && checkForm != null && 
               this.form.getId().equals(checkForm.getId());
    }

    /**
     * 질문의 폼 타입을 반환합니다
     */
    public String getFormType() {
        return form != null && form.getType() != null ? 
               form.getType().toString() : null;
    }

    /**
     * 질문의 간단한 정보를 문자열로 반환합니다
     */
    public String getQuestionInfo() {
        String formTitle = form != null ? form.getTitle() : "알 수 없음";
        return String.format("[%d번] %s (폼: %s)", orderIndex, content, formTitle);
    }

    // === 정적 팩토리 메서드 ===

    /**
     * 새로운 질문을 생성합니다
     */
    public static Question create(Form form, String content, int orderIndex) {
        return Question.builder()
                .form(form)
                .content(content)
                .orderIndex(orderIndex)
                .build();
    }
} 