package com.voin.entity;

import com.voin.constant.FriendStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "friends")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_id", nullable = false)
    private UUID requesterId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "coin_share_count", nullable = false)
    private Integer coinShareCount;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
        if (coinShareCount == null) {
            coinShareCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(UUID requesterId) {
        this.requesterId = requesterId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCoinShareCount() {
        return coinShareCount;
    }

    public void setCoinShareCount(Integer coinShareCount) {
        this.coinShareCount = coinShareCount;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public OffsetDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(OffsetDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    // 비즈니스 메서드
    public void accept() {
        this.status = FriendStatus.ACCEPTED.toString();
    }

    public void decline() {
        this.status = FriendStatus.DECLINED.toString();
    }

    public void block() {
        this.status = FriendStatus.BLOCKED.toString();
    }

    public boolean isPending() {
        return this.status.equals(FriendStatus.PENDING.toString());
    }

    public boolean isAccepted() {
        return this.status.equals(FriendStatus.ACCEPTED.toString());
    }
} 