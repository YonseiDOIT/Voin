package com.voin.repository;

import com.voin.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    List<Card> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") UUID memberId);

    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    Page<Card> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") UUID memberId, Pageable pageable);

    Page<Card> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.content LIKE %:keyword% AND c.isPublic = true ORDER BY c.createdAt DESC")
    Page<Card> findByContentContainingAndIsPublicTrue(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId AND c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    List<Card> findByMemberIdAndContentContaining(@Param("memberId") UUID memberId, @Param("keyword") String keyword);

    @Query("SELECT c FROM Card c WHERE c.form.id = :formId")
    List<Card> findByFormId(@Param("formId") Long formId);

    @Query("SELECT c FROM Card c WHERE c.keyword.id = :keywordId")
    List<Card> findByKeywordId(@Param("keywordId") Long keywordId);

    @Query("SELECT COUNT(c) FROM Card c WHERE c.member.id = :memberId")
    long countByMemberId(@Param("memberId") UUID memberId);
} 