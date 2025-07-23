package com.voin.repository;

import com.voin.constant.FriendStatus;
import com.voin.entity.Friend;
import com.voin.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    
    boolean existsByFromMemberAndToMemberAndStatus(Member fromMember, Member toMember, FriendStatus status);
    
    List<Friend> findByToMemberAndStatus(Member toMember, FriendStatus status);
    
    @Query("SELECT DISTINCT CASE " +
           "WHEN f.fromMember = :member THEN f.toMember " +
           "WHEN f.toMember = :member THEN f.fromMember " +
           "END " +
           "FROM Friend f " +
           "WHERE (f.fromMember = :member OR f.toMember = :member) " +
           "AND f.status = 'ACCEPTED'")
    List<Member> findAcceptedFriends(@Param("member") Member member);

    @Query("SELECT f FROM Friend f " +
            "WHERE f.status = 'ACCEPTED' AND " +
            "((f.fromMember.id = :memberId1 AND f.toMember.id = :memberId2) OR " +
            " (f.fromMember.id = :memberId2 AND f.toMember.id = :memberId1))")
    Optional<Friend> findAcceptedFriendBetween(UUID memberId1, UUID memberId2);

} 