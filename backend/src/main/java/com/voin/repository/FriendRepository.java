package com.voin.repository;

import com.voin.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query("SELECT f FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = 'ACCEPTED'")
    List<Friend> findAcceptedFriendsByMemberId(@Param("memberId") UUID memberId);

    @Query("SELECT f FROM Friend f WHERE f.receiverId = :memberId AND f.status = 'PENDING'")
    List<Friend> findPendingRequestsByReceiverId(@Param("memberId") UUID memberId);

    @Query("SELECT f FROM Friend f WHERE f.requesterId = :memberId AND f.status = 'PENDING'")
    List<Friend> findPendingRequestsByRequesterId(@Param("memberId") UUID memberId);

    Optional<Friend> findByRequesterIdAndReceiverId(UUID requesterId, UUID receiverId);

    @Query("SELECT COUNT(f) FROM Friend f WHERE (f.requesterId = :memberId OR f.receiverId = :memberId) AND f.status = 'ACCEPTED'")
    long countAcceptedFriendsByMemberId(@Param("memberId") UUID memberId);
} 