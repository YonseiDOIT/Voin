package com.voin.repository;

import com.voin.constant.FriendStatus;
import com.voin.entity.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 친구 관계(Friend) 엔티티에 대한 데이터 접근 계층
 * 친구 요청, 수락, 거절, 차단 등의 기능을 제공합니다.
 */
@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // === 기본 조회 메서드 ===

    /**
     * 두 회원 간의 친구 관계를 조회합니다
     * @param requesterId 요청자 ID
     * @param receiverId 수신자 ID
     * @return 친구 관계 (Optional)
     */
    Optional<Friend> findByRequesterIdAndReceiverId(UUID requesterId, UUID receiverId);

    /**
     * 두 회원 간의 친구 관계를 양방향으로 조회합니다
     * @param memberId1 첫 번째 회원 ID
     * @param memberId2 두 번째 회원 ID
     * @return 친구 관계 (Optional)
     */
    @Query("SELECT f FROM Friend f WHERE " +
           "(f.requesterId = :memberId1 AND f.receiverId = :memberId2) OR " +
           "(f.requesterId = :memberId2 AND f.receiverId = :memberId1)")
    Optional<Friend> findFriendshipBetween(@Param("memberId1") UUID memberId1, 
                                          @Param("memberId2") UUID memberId2);

    // === 수락된 친구 관계 조회 ===

    /**
     * 특정 회원의 모든 수락된 친구들을 조회합니다
     * @param memberId 회원 ID
     * @return 수락된 친구 관계 목록
     */
    @Query("SELECT f FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = :status ORDER BY f.acceptedAt DESC")
    List<Friend> findAcceptedFriendsByMemberId(@Param("memberId") UUID memberId, 
                                              @Param("status") FriendStatus status);

    /**
     * 특정 회원의 모든 수락된 친구들을 조회합니다 (기존 호환성용)
     * @param memberId 회원 ID
     * @return 수락된 친구 관계 목록
     */
    @Query("SELECT f FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = 'ACCEPTED' ORDER BY f.acceptedAt DESC")
    List<Friend> findAcceptedFriendsByMemberId(@Param("memberId") UUID memberId);

    /**
     * 특정 회원의 수락된 친구들을 페이징으로 조회합니다
     * @param memberId 회원 ID
     * @param status 친구 상태
     * @param pageable 페이징 정보
     * @return 수락된 친구 관계 페이지
     */
    @Query("SELECT f FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = :status ORDER BY f.acceptedAt DESC")
    Page<Friend> findAcceptedFriendsByMemberId(@Param("memberId") UUID memberId, 
                                              @Param("status") FriendStatus status,
                                              Pageable pageable);

    // === 대기 중인 요청 조회 ===

    /**
     * 특정 회원이 받은 대기 중인 친구 요청들을 조회합니다
     * @param memberId 회원 ID
     * @return 받은 대기 중인 요청 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.receiverId = :memberId AND f.status = :status ORDER BY f.createdAt DESC")
    List<Friend> findPendingRequestsByReceiverId(@Param("memberId") UUID memberId, 
                                                 @Param("status") FriendStatus status);

    /**
     * 특정 회원이 받은 대기 중인 친구 요청들을 조회합니다 (기존 호환성용)
     * @param memberId 회원 ID
     * @return 받은 대기 중인 요청 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.receiverId = :memberId AND f.status = 'PENDING' ORDER BY f.createdAt DESC")
    List<Friend> findPendingRequestsByReceiverId(@Param("memberId") UUID memberId);

    /**
     * 특정 회원이 보낸 대기 중인 친구 요청들을 조회합니다
     * @param memberId 회원 ID
     * @return 보낸 대기 중인 요청 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.requesterId = :memberId AND f.status = :status ORDER BY f.createdAt DESC")
    List<Friend> findPendingRequestsByRequesterId(@Param("memberId") UUID memberId, 
                                                  @Param("status") FriendStatus status);

    /**
     * 특정 회원이 보낸 대기 중인 친구 요청들을 조회합니다 (기존 호환성용)
     * @param memberId 회원 ID
     * @return 보낸 대기 중인 요청 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.requesterId = :memberId AND f.status = 'PENDING' ORDER BY f.createdAt DESC")
    List<Friend> findPendingRequestsByRequesterId(@Param("memberId") UUID memberId);

    // === 통계 및 집계 ===

    /**
     * 특정 회원의 수락된 친구 수를 조회합니다
     * @param memberId 회원 ID
     * @return 수락된 친구 수
     */
    @Query("SELECT COUNT(f) FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = :status")
    long countAcceptedFriendsByMemberId(@Param("memberId") UUID memberId, 
                                       @Param("status") FriendStatus status);

    /**
     * 특정 회원의 수락된 친구 수를 조회합니다 (기존 호환성용)
     * @param memberId 회원 ID
     * @return 수락된 친구 수
     */
    @Query("SELECT COUNT(f) FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = 'ACCEPTED'")
    long countAcceptedFriendsByMemberId(@Param("memberId") UUID memberId);

    /**
     * 특정 회원이 받은 대기 중인 요청 수를 조회합니다
     * @param memberId 회원 ID
     * @return 받은 대기 중인 요청 수
     */
    @Query("SELECT COUNT(f) FROM Friend f WHERE f.receiverId = :memberId AND f.status = :status")
    long countPendingRequestsByReceiverId(@Param("memberId") UUID memberId, 
                                         @Param("status") FriendStatus status);

    /**
     * 특정 회원이 보낸 대기 중인 요청 수를 조회합니다
     * @param memberId 회원 ID
     * @return 보낸 대기 중인 요청 수
     */
    @Query("SELECT COUNT(f) FROM Friend f WHERE f.requesterId = :memberId AND f.status = :status")
    long countPendingRequestsByRequesterId(@Param("memberId") UUID memberId, 
                                          @Param("status") FriendStatus status);

    // === 상태별 조회 ===

    /**
     * 특정 상태의 모든 친구 관계를 조회합니다
     * @param status 친구 상태
     * @return 해당 상태의 친구 관계 목록
     */
    List<Friend> findByStatus(FriendStatus status);

    /**
     * 차단된 관계를 조회합니다
     * @param memberId 회원 ID
     * @return 차단된 관계 목록
     */
    @Query("SELECT f FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = :status")
    List<Friend> findBlockedRelationshipsByMemberId(@Param("memberId") UUID memberId, 
                                                   @Param("status") FriendStatus status);

    // === 특수 조회 ===

    /**
     * 가장 많은 코인을 공유한 친구 관계들을 조회합니다
     * @param limit 조회할 관계 수
     * @return 코인 공유 순 친구 관계 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.status = :status ORDER BY f.coinShareCount DESC LIMIT :limit")
    List<Friend> findTopCoinSharingFriendships(@Param("status") FriendStatus status, 
                                              @Param("limit") int limit);

    /**
     * 특정 기간 동안 수락된 친구 관계를 조회합니다
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 수락된 친구 관계 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.acceptedAt >= :startDate AND f.acceptedAt <= :endDate ORDER BY f.acceptedAt DESC")
    List<Friend> findFriendshipsAcceptedBetween(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);

    /**
     * 오래된 대기 중인 요청들을 조회합니다
     * @param beforeDate 기준 날짜 (이전 요청들)
     * @return 오래된 대기 중인 요청 목록
     */
    @Query("SELECT f FROM Friend f WHERE f.status = :status AND f.createdAt < :beforeDate ORDER BY f.createdAt ASC")
    List<Friend> findOldPendingRequests(@Param("status") FriendStatus status, 
                                       @Param("beforeDate") LocalDateTime beforeDate);

    /**
     * 특정 회원의 상호 친구들을 조회합니다 (양방향 친구)
     * @param memberId 회원 ID
     * @return 상호 친구 관계 목록
     */
    @Query("SELECT f1 FROM Friend f1 WHERE f1.status = :status AND f1.requesterId = :memberId " +
           "AND EXISTS (SELECT f2 FROM Friend f2 WHERE f2.status = :status " +
           "AND f2.requesterId = f1.receiverId AND f2.receiverId = :memberId)")
    List<Friend> findMutualFriendships(@Param("memberId") UUID memberId, 
                                      @Param("status") FriendStatus status);

    // === 업데이트 메서드 ===

    /**
     * 친구 관계의 상태를 업데이트합니다
     * @param friendId 친구 관계 ID
     * @param newStatus 새로운 상태
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE Friend f SET f.status = :newStatus WHERE f.id = :friendId")
    int updateFriendStatus(@Param("friendId") Long friendId, 
                          @Param("newStatus") FriendStatus newStatus);

    /**
     * 친구 관계의 코인 공유 횟수를 증가시킵니다
     * @param friendId 친구 관계 ID
     * @param increment 증가량
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE Friend f SET f.coinShareCount = f.coinShareCount + :increment WHERE f.id = :friendId")
    int incrementCoinShareCount(@Param("friendId") Long friendId, 
                               @Param("increment") int increment);

    /**
     * 특정 회원의 모든 친구 관계를 비활성화합니다 (차단)
     * @param memberId 회원 ID
     * @param newStatus 새로운 상태
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE Friend f SET f.status = :newStatus WHERE f.requesterId = :memberId OR f.receiverId = :memberId")
    int updateAllFriendshipsByMemberId(@Param("memberId") UUID memberId, 
                                      @Param("newStatus") FriendStatus newStatus);

    // === 존재 여부 확인 ===

    /**
     * 두 회원 간에 어떤 형태의 관계든 존재하는지 확인합니다
     * @param memberId1 첫 번째 회원 ID
     * @param memberId2 두 번째 회원 ID
     * @return 관계 존재 여부
     */
    @Query("SELECT COUNT(f) > 0 FROM Friend f WHERE " +
           "(f.requesterId = :memberId1 AND f.receiverId = :memberId2) OR " +
           "(f.requesterId = :memberId2 AND f.receiverId = :memberId1)")
    boolean existsAnyRelationshipBetween(@Param("memberId1") UUID memberId1, 
                                        @Param("memberId2") UUID memberId2);

    /**
     * 두 회원이 수락된 친구 관계인지 확인합니다
     * @param memberId1 첫 번째 회원 ID
     * @param memberId2 두 번째 회원 ID
     * @return 친구 관계 여부
     */
    @Query("SELECT COUNT(f) > 0 FROM Friend f WHERE " +
           "(f.requesterId = :memberId1 AND f.receiverId = :memberId2 AND f.status = :status) OR " +
           "(f.requesterId = :memberId2 AND f.receiverId = :memberId1 AND f.status = :status)")
    boolean areAcceptedFriends(@Param("memberId1") UUID memberId1, 
                              @Param("memberId2") UUID memberId2, 
                              @Param("status") FriendStatus status);
} 