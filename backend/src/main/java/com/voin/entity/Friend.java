package com.voin.entity;

import com.voin.constant.FriendStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원 간의 친구 관계를 나타내는 엔티티
 * 친구 요청, 수락, 거절, 차단 등의 상태를 관리합니다.
 */
@Entity
@Table(name = "friends",
       indexes = {
           @Index(name = "idx_friend_requester_id", columnList = "requester_id"),
           @Index(name = "idx_friend_receiver_id", columnList = "receiver_id"),
           @Index(name = "idx_friend_status", columnList = "status"),
           @Index(name = "idx_friend_unique", columnList = "requester_id, receiver_id", unique = true)
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Friend extends BaseEntity {

    /**
     * 친구 관계 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 친구 요청을 보낸 회원의 ID
     */
    @NotNull(message = "요청자 ID는 필수입니다")
    @Column(name = "requester_id", nullable = false, columnDefinition = "uuid")
    private UUID requesterId;

    /**
     * 친구 요청을 받은 회원의 ID
     */
    @NotNull(message = "수신자 ID는 필수입니다")
    @Column(name = "receiver_id", nullable = false, columnDefinition = "uuid")
    private UUID receiverId;

    /**
     * 친구 관계 상태 (PENDING, ACCEPTED, DECLINED, BLOCKED)
     */
    @NotNull(message = "상태는 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private FriendStatus status = FriendStatus.PENDING;

    /**
     * 코인 공유 횟수
     */
    @Min(value = 0, message = "코인 공유 횟수는 0 이상이어야 합니다")
    @Column(name = "coin_share_count", nullable = false)
    @Builder.Default
    private Integer coinShareCount = 0;

    /**
     * 친구 관계가 수락된 시간
     */
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    // === 생성/수정 시 자동 처리 ===

    @PrePersist
    protected void onCreate() {
        if (coinShareCount == null) {
            coinShareCount = 0;
        }
        if (status == null) {
            status = FriendStatus.PENDING;
        }
    }

    // === 비즈니스 메서드 ===

    /**
     * 친구 요청을 수락합니다
     */
    public void accept() {
        if (this.status != FriendStatus.PENDING) {
            throw new IllegalStateException("대기 중인 요청만 수락할 수 있습니다");
        }
        this.status = FriendStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * 친구 요청을 거절합니다
     */
    public void decline() {
        if (this.status != FriendStatus.PENDING) {
            throw new IllegalStateException("대기 중인 요청만 거절할 수 있습니다");
        }
        this.status = FriendStatus.DECLINED;
    }

    /**
     * 친구를 차단합니다
     */
    public void block() {
        this.status = FriendStatus.BLOCKED;
    }

    /**
     * 차단을 해제하고 친구 관계로 복원합니다
     */
    public void unblock() {
        if (this.status != FriendStatus.BLOCKED) {
            throw new IllegalStateException("차단된 관계만 차단 해제할 수 있습니다");
        }
        this.status = FriendStatus.ACCEPTED;
        if (this.acceptedAt == null) {
            this.acceptedAt = LocalDateTime.now();
        }
    }

    /**
     * 코인 공유 횟수를 증가시킵니다
     */
    public void incrementCoinShareCount() {
        if (!isAccepted()) {
            throw new IllegalStateException("수락된 친구 관계에서만 코인을 공유할 수 있습니다");
        }
        this.coinShareCount++;
    }

    /**
     * 특정 수만큼 코인 공유 횟수를 증가시킵니다
     */
    public void addCoinShareCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("추가할 횟수는 0보다 커야 합니다");
        }
        if (!isAccepted()) {
            throw new IllegalStateException("수락된 친구 관계에서만 코인을 공유할 수 있습니다");
        }
        this.coinShareCount += count;
    }

    // === 상태 확인 메서드 ===

    /**
     * 대기 중인 요청인지 확인합니다
     */
    public boolean isPending() {
        return this.status == FriendStatus.PENDING;
    }

    /**
     * 수락된 친구 관계인지 확인합니다
     */
    public boolean isAccepted() {
        return this.status == FriendStatus.ACCEPTED;
    }

    /**
     * 거절된 요청인지 확인합니다
     */
    public boolean isDeclined() {
        return this.status == FriendStatus.DECLINED;
    }

    /**
     * 차단된 관계인지 확인합니다
     */
    public boolean isBlocked() {
        return this.status == FriendStatus.BLOCKED;
    }

    /**
     * 특정 회원이 이 관계의 요청자인지 확인합니다
     */
    public boolean isRequester(UUID memberId) {
        return requesterId != null && requesterId.equals(memberId);
    }

    /**
     * 특정 회원이 이 관계의 수신자인지 확인합니다
     */
    public boolean isReceiver(UUID memberId) {
        return receiverId != null && receiverId.equals(memberId);
    }

    /**
     * 특정 회원이 이 관계에 포함되는지 확인합니다
     */
    public boolean involvesMember(UUID memberId) {
        return isRequester(memberId) || isReceiver(memberId);
    }

    /**
     * 특정 회원의 상대방 ID를 반환합니다
     */
    public UUID getOtherMemberId(UUID memberId) {
        if (isRequester(memberId)) {
            return receiverId;
        } else if (isReceiver(memberId)) {
            return requesterId;
        } else {
            throw new IllegalArgumentException("해당 회원은 이 친구 관계에 포함되지 않습니다");
        }
    }

    /**
     * 친구 관계의 간단한 정보를 문자열로 반환합니다
     */
    public String getFriendshipInfo() {
        return String.format("친구 관계 [%s -> %s] 상태: %s, 공유: %d회", 
                           requesterId, receiverId, status, coinShareCount);
    }

    // === 정적 팩토리 메서드 ===

    /**
     * 새로운 친구 요청을 생성합니다
     */
    public static Friend createRequest(UUID requesterId, UUID receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("자기 자신에게 친구 요청을 보낼 수 없습니다");
        }
        return Friend.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(FriendStatus.PENDING)
                .coinShareCount(0)
                .build();
    }

    /**
     * 즉시 수락된 친구 관계를 생성합니다 (테스트용)
     */
    public static Friend createAcceptedFriendship(UUID requesterId, UUID receiverId) {
        LocalDateTime now = LocalDateTime.now();
        return Friend.builder()
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(FriendStatus.ACCEPTED)
                .coinShareCount(0)
                .acceptedAt(now)
                .build();
    }
} 