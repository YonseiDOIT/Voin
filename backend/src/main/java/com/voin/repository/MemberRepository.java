package com.voin.repository;

import com.voin.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    Optional<Member> findByKakaoId(String kakaoId);

    Optional<Member> findByFriendCode(String friendCode);

    boolean existsByKakaoId(String kakaoId);

    boolean existsByFriendCode(String friendCode);

    boolean existsByNickname(String nickname);

    List<Member> findByIsActiveTrue();

    @Query("SELECT m FROM Member m WHERE m.nickname LIKE %:keyword% AND m.isActive = true")
    List<Member> findByNicknameContainingAndIsActiveTrue(@Param("keyword") String keyword);

    @Query("SELECT m FROM Member m WHERE m.id IN :memberIds AND m.isActive = true")
    List<Member> findByIdInAndIsActiveTrue(@Param("memberIds") List<UUID> memberIds);
} 