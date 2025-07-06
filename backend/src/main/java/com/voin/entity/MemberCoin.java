package com.voin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원이 보유한 코인 정보를 나타내는 엔티티
 * 각 회원이 어떤 코인을 몇 개 보유하고 있는지, 언제 처음/마지막으로 획득했는지 추적합니다.
 */
@Entity
@Table(name = "member_coins",
       indexes = {
           @Index(name = "idx_member_coin_member_id", columnList = "member_id"),
           @Index(name = "idx_member_coin_coin_id", columnList = "coin_id"),
           @Index(name = "idx_member_coin_unique", columnList = "member_id, coin_id", unique = true)
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberCoin extends BaseEntity {

    /**
     * 회원-코인 관계 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 코인을 보유한 회원의 ID
     */
    @NotNull(message = "회원 ID는 필수입니다")
    @Column(name = "member_id", nullable = false, columnDefinition = "uuid")
    private UUID memberId;

    /**
     * 보유한 코인의 ID
     */
    @NotNull(message = "코인 ID는 필수입니다")
    @Column(name = "coin_id", nullable = false)
    private Long coinId;

    /**
     * 보유한 코인의 개수
     */
    @NotNull(message = "개수는 필수입니다")
    @Min(value = 0, message = "개수는 0 이상이어야 합니다")
    @Column(name = "count", nullable = false)
    @Builder.Default
    private Integer count = 0;

    /**
     * 최초 코인 획득 시간
     */
    @NotNull(message = "최초 획득 시간은 필수입니다")
    @Column(name = "first_obtained_at", nullable = false)
    private LocalDateTime firstObtainedAt;

    /**
     * 마지막 코인 획득 시간
     */
    @NotNull(message = "마지막 획득 시간은 필수입니다")
    @Column(name = "last_obtained_at", nullable = false)
    private LocalDateTime lastObtainedAt;

    // === 생성/수정 시 자동 처리 ===

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (firstObtainedAt == null) {
            firstObtainedAt = now;
        }
        if (lastObtainedAt == null) {
            lastObtainedAt = now;
        }
        if (count == null) {
            count = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastObtainedAt = LocalDateTime.now();
    }

    // === 비즈니스 메서드 ===

    /**
     * 코인 개수를 증가시킵니다
     */
    public void addCoin(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("추가할 개수는 0보다 커야 합니다");
        }
        this.count += amount;
        this.lastObtainedAt = LocalDateTime.now();
    }

    /**
     * 코인 1개를 추가합니다
     */
    public void addOneCoin() {
        addCoin(1);
    }

    /**
     * 코인 개수를 감소시킵니다
     */
    public void removeCoin(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("제거할 개수는 0보다 커야 합니다");
        }
        if (this.count < amount) {
            throw new IllegalArgumentException("보유한 코인보다 많이 제거할 수 없습니다");
        }
        this.count -= amount;
    }

    /**
     * 코인 1개를 제거합니다
     */
    public void removeOneCoin() {
        removeCoin(1);
    }

    /**
     * 특정 개수로 코인을 설정합니다
     */
    public void setCoinCount(int newCount) {
        if (newCount < 0) {
            throw new IllegalArgumentException("코인 개수는 0 이상이어야 합니다");
        }
        this.count = newCount;
        if (newCount > 0) {
            this.lastObtainedAt = LocalDateTime.now();
        }
    }

    /**
     * 코인을 보유하고 있는지 확인합니다
     */
    public boolean hasCoin() {
        return count != null && count > 0;
    }

    /**
     * 특정 개수 이상의 코인을 보유하고 있는지 확인합니다
     */
    public boolean hasCoinsAtLeast(int requiredCount) {
        return count != null && count >= requiredCount;
    }

    /**
     * 이 관계의 간단한 정보를 문자열로 반환합니다
     */
    public String getInfo() {
        return String.format("회원 %s - 코인 %d: %d개", 
                           memberId, coinId, count);
    }

    // === 정적 팩토리 메서드 ===

    /**
     * 새로운 MemberCoin을 생성합니다
     */
    public static MemberCoin create(UUID memberId, Long coinId, int initialCount) {
        LocalDateTime now = LocalDateTime.now();
        return MemberCoin.builder()
                .memberId(memberId)
                .coinId(coinId)
                .count(initialCount)
                .firstObtainedAt(now)
                .lastObtainedAt(now)
                .build();
    }

    /**
     * 코인 1개로 시작하는 새로운 MemberCoin을 생성합니다
     */
    public static MemberCoin createWithOneCoin(UUID memberId, Long coinId) {
        return create(memberId, coinId, 1);
    }
} 