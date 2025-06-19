package com.voin.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "member_coins")
public class MemberCoin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(name = "coin_id", nullable = false)
    private Long coinId;

    @Column(name = "count", nullable = false)
    private Integer count;

    @Column(name = "first_obtained_at", nullable = false)
    private OffsetDateTime firstObtainedAt;

    @Column(name = "last_obtained_at", nullable = false)
    private OffsetDateTime lastObtainedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (firstObtainedAt == null) {
            firstObtainedAt = now;
        }
        lastObtainedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastObtainedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public Long getCoinId() {
        return coinId;
    }

    public void setCoinId(Long coinId) {
        this.coinId = coinId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public OffsetDateTime getFirstObtainedAt() {
        return firstObtainedAt;
    }

    public void setFirstObtainedAt(OffsetDateTime firstObtainedAt) {
        this.firstObtainedAt = firstObtainedAt;
    }

    public OffsetDateTime getLastObtainedAt() {
        return lastObtainedAt;
    }

    public void setLastObtainedAt(OffsetDateTime lastObtainedAt) {
        this.lastObtainedAt = lastObtainedAt;
    }
} 