package com.voin.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 각 코인 카테고리에 속하는 키워드를 나타내는 엔티티
 * 총 55개의 고정된 키워드가 6개 코인에 분배되어 있습니다.
 */
@Entity
@Table(name = "keywords",
       indexes = {
           @Index(name = "idx_keyword_coin_id", columnList = "coin_id"),
           @Index(name = "idx_keyword_name", columnList = "name"),
           @Index(name = "idx_keyword_coin_name", columnList = "coin_id, name")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Keyword extends BaseEntity {

    /**
     * 키워드 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 키워드가 속한 코인 카테고리
     */
    @NotNull(message = "코인은 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_keyword_coin"))
    private Coin coin;

    /**
     * 키워드 이름
     */
    @NotBlank(message = "키워드 이름은 필수입니다")
    @Size(max = 30, message = "키워드 이름은 30자를 초과할 수 없습니다")
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    /**
     * 키워드 설명
     */
    @Size(max = 300, message = "설명은 300자를 초과할 수 없습니다")
    @Column(name = "description", length = 300)
    private String description;

    // === 비즈니스 메서드 ===

    /**
     * 키워드 정보를 업데이트합니다
     */
    public void updateInfo(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
    }

    /**
     * 코인을 변경합니다
     */
    public void changeCoin(Coin newCoin) {
        if (newCoin == null) {
            throw new IllegalArgumentException("코인은 null일 수 없습니다");
        }
        this.coin = newCoin;
    }

    /**
     * 키워드 이름과 설명을 포함한 전체 정보를 반환합니다
     */
    public String getFullInfo() {
        if (description != null && !description.trim().isEmpty()) {
            return name + " - " + description;
        }
        return name;
    }

    /**
     * 속한 코인의 이름을 반환합니다
     */
    public String getCoinName() {
        return coin != null ? coin.getName() : null;
    }
} 