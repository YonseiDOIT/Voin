package com.voin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voin.constant.FormType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 카드 생성 시 사용되는 폼을 나타내는 엔티티
 * 오늘의 일기, 빨간펜 등 다양한 형태의 폼이 있습니다.
 */
@Entity
@Table(name = "forms",
       indexes = {
           @Index(name = "idx_form_type", columnList = "type"),
           @Index(name = "idx_form_title", columnList = "title")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Form extends BaseEntity {

    /**
     * 폼 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 폼 제목
     */
    @NotBlank(message = "폼 제목은 필수입니다")
    @Size(max = 100, message = "폼 제목은 100자를 초과할 수 없습니다")
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    /**
     * 폼 설명
     */
    @Size(max = 500, message = "폼 설명은 500자를 초과할 수 없습니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 폼 타입 (DIARY, RED_PEN 등)
     */
    @NotNull(message = "폼 타입은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private FormType type;

    // === 비즈니스 메서드 ===

    /**
     * 폼 정보를 업데이트합니다
     */
    public void updateInfo(String title, String description) {
        if (title != null && !title.trim().isEmpty()) {
            if (title.length() > 100) {
                throw new IllegalArgumentException("폼 제목은 100자를 초과할 수 없습니다");
            }
            this.title = title.trim();
        }
        if (description != null) {
            if (description.length() > 500) {
                throw new IllegalArgumentException("폼 설명은 500자를 초과할 수 없습니다");
            }
            this.description = description.trim();
        }
    }

    /**
     * 폼 타입을 변경합니다
     */
    public void changeType(FormType newType) {
        if (newType == null) {
            throw new IllegalArgumentException("폼 타입은 null일 수 없습니다");
        }
        this.type = newType;
    }

    /**
     * 특정 타입의 폼인지 확인합니다
     */
    public boolean isType(FormType checkType) {
        return this.type == checkType;
    }

    /**
     * 폼의 전체 정보를 문자열로 반환합니다
     */
    public String getFullInfo() {
        StringBuilder info = new StringBuilder();
        info.append("[").append(type).append("] ").append(title);
        if (description != null && !description.trim().isEmpty()) {
            info.append(" - ").append(description);
        }
        return info.toString();
    }

    /**
     * 일기 타입 폼인지 확인합니다
     */
    public boolean isDiaryType() {
        return FormType.TODAY_DIARY == this.type;
    }

    /**
     * 경험 돌아보기 타입 폼인지 확인합니다
     */
    public boolean isExperienceReflectionType() {
        return FormType.EXPERIENCE_REFLECTION == this.type;
    }

    /**
     * 친구 장점 찾기 타입 폼인지 확인합니다
     */
    public boolean isFriendStrengthType() {
        return FormType.FRIEND_STRENGTH == this.type;
    }
} 