package com.voin.repository;

import com.voin.entity.Card;
import com.voin.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 장점 카드(Card) 엔티티에 대한 데이터 접근 계층
 * 카드 조회, 검색, 통계 등의 기능을 제공합니다.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    // === 소유자별 카드 조회 ===

    /**
     * 특정 회원이 소유한 모든 카드를 조회합니다 (최신순)
     * @param owner 소유자
     * @return 해당 회원이 소유한 카드 목록
     */
    List<Card> findByOwnerOrderByCreatedAtDesc(Member owner);

    /**
     * 특정 회원이 소유한 공개 카드만 조회합니다 (최신순)
     * @param owner 소유자
     * @return 해당 회원이 소유한 공개 카드 목록
     */
    List<Card> findByOwnerAndIsPublicTrueOrderByCreatedAtDesc(Member owner);

    /**
     * 특정 회원이 생성한 모든 카드를 조회합니다 (최신순)
     * @param creator 생성자
     * @return 해당 회원이 생성한 카드 목록
     */
    List<Card> findByCreatorOrderByCreatedAtDesc(Member creator);

    // === 공개 카드 조회 ===

    /**
     * 모든 공개 카드를 조회합니다 (최신순, 페이징)
     * @param pageable 페이징 정보
     * @return 공개 카드 페이지
     */
    Page<Card> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 모든 공개 카드를 조회합니다 (최신순)
     * @return 공개 카드 목록
     */
    List<Card> findByIsPublicTrueOrderByCreatedAtDesc();

    // === 친구들의 공개 카드 조회 (Friend 기능용) ===

    /**
     * 여러 회원들의 공개 카드를 조회합니다 (최신순)
     * @param owners 소유자 목록
     * @return 해당 회원들의 공개 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.owner IN :owners AND c.isPublic = true ORDER BY c.createdAt DESC")
    List<Card> findByOwnerInAndIsPublicTrueOrderByCreatedAtDesc(@Param("owners") List<Member> owners);

    // === 카드 검색 ===

    /**
     * 내용으로 공개 카드를 검색합니다 (페이징)
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색된 공개 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.isPublic = true AND c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    Page<Card> searchByContentAndIsPublicTrue(@Param("keyword") String keyword, Pageable pageable);

    // === 통계 쿼리 ===

    /**
     * 특정 회원이 소유한 카드 수를 조회합니다
     * @param owner 소유자
     * @return 카드 수
     */
    long countByOwner(Member owner);

    /**
     * 특정 회원이 소유한 공개 카드 수를 조회합니다
     * @param owner 소유자
     * @return 공개 카드 수
     */
    long countByOwnerAndIsPublicTrue(Member owner);

    /**
     * 전체 공개 카드 수를 조회합니다
     * @return 전체 공개 카드 수
     */
    long countByIsPublicTrue();

    // === 삭제 쿼리 ===

    /**
     * 특정 회원이 소유한 모든 카드를 삭제합니다
     * @param owner 소유자
     */
    @Modifying
    @Query("DELETE FROM Card c WHERE c.owner = :owner")
    void deleteByOwner(@Param("owner") Member owner);

    /**
     * 특정 회원이 생성한 모든 카드를 삭제합니다
     * @param creator 생성자
     */
    @Modifying
    @Query("DELETE FROM Card c WHERE c.creator = :creator")
    void deleteByCreator(@Param("creator") Member creator);
} 