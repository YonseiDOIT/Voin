package com.voin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 장점 카테고리를 나타내는 코인 엔티티
 * 총 6가지 고정된 카테고리가 있습니다:
 * 1. 관리와 성장, 2. 감정과 태도, 3. 창의와 몰입, 
 * 4. 사고와 해결, 5. 관계와 공감, 6. 신념과 실행
 */
@Entity
@Table(name = "coins",
       indexes = {
           @Index(name = "idx_coin_name", columnList = "name")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Coin extends BaseEntity {

    /**
     * 코인 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 코인 카테고리 이름
     */
    @NotBlank(message = "코인 이름은 필수입니다")
    @Size(max = 50, message = "코인 이름은 50자를 초과할 수 없습니다")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    /**
     * 코인 카테고리 설명
     */
    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 코인을 나타내는 색상 코드 (HEX 형식: #RRGGBB)
     */
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "색상은 HEX 형식이어야 합니다 (예: #FF0000)")
    @Column(name = "color", length = 7)
    private String color;

    // === 비즈니스 메서드 ===

    /**
     * 코인 정보를 업데이트합니다
     */
    public void updateInfo(String name, String description, String color) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        if (color != null && isValidColorCode(color)) {
            this.color = color.toUpperCase();
        }
    }

    /**
     * 색상 코드가 유효한지 확인합니다
     */
    private boolean isValidColorCode(String color) {
        return color != null && color.matches("^#[0-9A-Fa-f]{6}$");
    }

    /**
     * 코인 이름과 설명을 포함한 전체 정보를 반환합니다
     */
    public String getFullInfo() {
        if (description != null && !description.trim().isEmpty()) {
            return name + " - " + description;
        }
        return name;
    }
} 