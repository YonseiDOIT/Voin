package com.voin.repository;

import com.voin.constant.FriendStatus;
import com.voin.entity.Friend;
import com.voin.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
} 