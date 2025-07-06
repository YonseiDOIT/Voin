package com.voin.repository;

import com.voin.entity.Card;
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

    // === 회원별 카드 조회 ===

    /**
     * 특정 회원이 작성한 모든 카드를 조회합니다 (최신순)
     * @param memberId 회원 ID
     * @return 해당 회원이 작성한 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    List<Card> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") UUID memberId);

    /**
     * 특정 회원이 작성한 카드를 페이징으로 조회합니다 (최신순)
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 해당 회원이 작성한 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    Page<Card> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") UUID memberId, Pageable pageable);

    /**
     * 특정 회원이 대상인 모든 카드를 조회합니다 (최신순)
     * @param targetMemberId 대상 회원 ID
     * @return 해당 회원이 대상인 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.targetMember.id = :targetMemberId ORDER BY c.createdAt DESC")
    List<Card> findByTargetMemberIdOrderByCreatedAtDesc(@Param("targetMemberId") UUID targetMemberId);

    /**
     * 특정 회원이 대상인 카드를 페이징으로 조회합니다 (최신순)
     * @param targetMemberId 대상 회원 ID
     * @param pageable 페이징 정보
     * @return 해당 회원이 대상인 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.targetMember.id = :targetMemberId ORDER BY c.createdAt DESC")
    Page<Card> findByTargetMemberIdOrderByCreatedAtDesc(@Param("targetMemberId") UUID targetMemberId, Pageable pageable);

    // === 공개 카드 조회 ===

    /**
     * 모든 공개 카드를 조회합니다 (최신순)
     * @param pageable 페이징 정보
     * @return 공개 카드 페이지
     */
    Page<Card> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 최근 공개된 카드들을 조회합니다
     * @param limit 조회할 카드 수
     * @return 최근 공개 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.isPublic = true ORDER BY c.createdAt DESC LIMIT :limit")
    List<Card> findRecentPublicCards(@Param("limit") int limit);

    // === 검색 기능 ===

    /**
     * 내용에 키워드가 포함된 공개 카드를 조회합니다
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 공개 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.content LIKE %:keyword% AND c.isPublic = true ORDER BY c.createdAt DESC")
    Page<Card> findByContentContainingAndIsPublicTrue(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 특정 회원의 카드 중 내용에 키워드가 포함된 카드를 조회합니다
     * @param memberId 회원 ID
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId AND c.content LIKE %:keyword% ORDER BY c.createdAt DESC")
    List<Card> findByMemberIdAndContentContaining(@Param("memberId") UUID memberId, @Param("keyword") String keyword);

    /**
     * 키워드 이름으로 공개 카드를 검색합니다
     * @param keywordName 키워드 이름
     * @param pageable 페이징 정보
     * @return 해당 키워드를 사용한 공개 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.keyword.name LIKE %:keywordName% AND c.isPublic = true ORDER BY c.createdAt DESC")
    Page<Card> findByKeywordNameContainingAndIsPublicTrue(@Param("keywordName") String keywordName, Pageable pageable);

    // === 폼/키워드별 조회 ===

    /**
     * 특정 폼으로 작성된 모든 카드를 조회합니다
     * @param formId 폼 ID
     * @return 해당 폼으로 작성된 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.form.id = :formId ORDER BY c.createdAt DESC")
    List<Card> findByFormId(@Param("formId") Long formId);

    /**
     * 특정 키워드로 작성된 모든 카드를 조회합니다
     * @param keywordId 키워드 ID
     * @return 해당 키워드로 작성된 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.keyword.id = :keywordId ORDER BY c.createdAt DESC")
    List<Card> findByKeywordId(@Param("keywordId") Long keywordId);

    /**
     * 특정 코인 카테고리의 카드를 조회합니다
     * @param coinId 코인 ID
     * @param pageable 페이징 정보
     * @return 해당 코인 카테고리의 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.keyword.coin.id = :coinId ORDER BY c.createdAt DESC")
    Page<Card> findByCoinId(@Param("coinId") Long coinId, Pageable pageable);

    // === 통계 및 집계 ===

    /**
     * 특정 회원이 작성한 카드 수를 조회합니다
     * @param memberId 회원 ID
     * @return 작성한 카드 수
     */
    @Query("SELECT COUNT(c) FROM Card c WHERE c.member.id = :memberId")
    long countByMemberId(@Param("memberId") UUID memberId);

    /**
     * 특정 회원이 받은 카드 수를 조회합니다
     * @param targetMemberId 대상 회원 ID
     * @return 받은 카드 수
     */
    @Query("SELECT COUNT(c) FROM Card c WHERE c.targetMember.id = :targetMemberId")
    long countByTargetMemberId(@Param("targetMemberId") UUID targetMemberId);

    /**
     * 전체 공개 카드 수를 조회합니다
     * @return 공개 카드 수
     */
    long countByIsPublicTrue();

    /**
     * 오늘 생성된 카드 수를 조회합니다
     * @param startOfDay 오늘 시작 시간
     * @param endOfDay 오늘 끝 시간
     * @return 오늘 생성된 카드 수
     */
    @Query("SELECT COUNT(c) FROM Card c WHERE c.createdAt >= :startOfDay AND c.createdAt < :endOfDay")
    long countCardsCreatedToday(@Param("startOfDay") LocalDateTime startOfDay, 
                               @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 특정 기간 동안 생성된 카드 수를 조회합니다
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 생성된 카드 수
     */
    @Query("SELECT COUNT(c) FROM Card c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    long countCardsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);

    // === 특수 조회 ===

    /**
     * 두 회원 간의 모든 카드를 조회합니다
     * @param memberId1 첫 번째 회원 ID
     * @param memberId2 두 번째 회원 ID
     * @return 두 회원 간의 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE " +
           "(c.member.id = :memberId1 AND c.targetMember.id = :memberId2) OR " +
           "(c.member.id = :memberId2 AND c.targetMember.id = :memberId1) " +
           "ORDER BY c.createdAt DESC")
    List<Card> findCardsBetweenMembers(@Param("memberId1") UUID memberId1, 
                                      @Param("memberId2") UUID memberId2);

    /**
     * 자신에 대한 카드(셀프 카드)를 조회합니다
     * @param memberId 회원 ID
     * @return 자신에 대한 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.member.id = :memberId AND c.targetMember.id = :memberId ORDER BY c.createdAt DESC")
    List<Card> findSelfCardsByMemberId(@Param("memberId") UUID memberId);

    /**
     * 내용이 없는 카드들을 조회합니다
     * @return 내용이 없는 카드 목록
     */
    @Query("SELECT c FROM Card c WHERE c.content IS NULL OR c.content = '' ORDER BY c.createdAt DESC")
    List<Card> findCardsWithoutContent();

    /**
     * 특정 키워드들로 작성된 카드를 조회합니다
     * @param keywordIds 키워드 ID 목록
     * @param pageable 페이징 정보
     * @return 해당 키워드들로 작성된 카드 페이지
     */
    @Query("SELECT c FROM Card c WHERE c.keyword.id IN :keywordIds ORDER BY c.createdAt DESC")
    Page<Card> findByKeywordIds(@Param("keywordIds") List<Long> keywordIds, Pageable pageable);

    // === 업데이트 메서드 ===

    /**
     * 카드의 공개 상태를 일괄 업데이트합니다
     * @param cardIds 대상 카드 ID들
     * @param isPublic 새로운 공개 상태
     * @return 업데이트된 카드 수
     */
    @Modifying
    @Query("UPDATE Card c SET c.isPublic = :isPublic WHERE c.id IN :cardIds")
    int updateCardPublicStatus(@Param("cardIds") List<Long> cardIds, 
                              @Param("isPublic") boolean isPublic);

    /**
     * 특정 회원의 모든 카드를 비공개로 설정합니다
     * @param memberId 회원 ID
     * @return 업데이트된 카드 수
     */
    @Modifying
    @Query("UPDATE Card c SET c.isPublic = false WHERE c.member.id = :memberId")
    int makeAllCardsPrivateByMemberId(@Param("memberId") UUID memberId);
} 