package com.voin.repository;

import com.voin.entity.MemberCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberCoinRepository extends JpaRepository<MemberCoin, Long> {
    
    List<MemberCoin> findByMemberId(UUID memberId);
    
    Optional<MemberCoin> findByMemberIdAndCoinId(UUID memberId, Long coinId);
} 