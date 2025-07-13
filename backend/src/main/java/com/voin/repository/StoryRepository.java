package com.voin.repository;

import com.voin.constant.StoryType;
import com.voin.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 스토리(Story) 엔티티에 대한 데이터 접근 계층
 * 스토리 조회, 검색, 타입별 분류 등의 기능을 제공합니다.
 */
@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    // === 기본 조회 메서드 ===

    /**
     * 특정 회원의 모든 스토리를 최신 순으로 조회합니다
     * @param memberId 회원 ID
     * @return 해당 회원의 스토리 목록 (최신순)
     */
    List<Story> findByMemberIdOrderByCreatedAtDesc(UUID memberId);

    /**
     * 특정 회원의 스토리를 페이징하여 조회합니다
     * @param memberId 회원 ID
     * @param pageable 페이징 정보
     * @return 페이징된 스토리 목록
     */
    Page<Story> findByMemberId(UUID memberId, Pageable pageable);

    /**
     * 특정 타입의 스토리들을 최신 순으로 조회합니다
     * @param storyType 스토리 타입
     * @return 해당 타입의 스토리 목록 (최신순)
     */
    List<Story> findByStoryTypeOrderByCreatedAtDesc(StoryType storyType);

    /**
     * 특정 회원의 특정 타입 스토리를 조회합니다
     * @param memberId 회원 ID
     * @param storyType 스토리 타입
     * @return 해당 조건의 스토리 목록
     */
    List<Story> findByMemberIdAndStoryTypeOrderByCreatedAtDesc(UUID memberId, StoryType storyType);

    // === 검색 메서드 ===

    /**
     * 제목에 키워드가 포함된 스토리를 조회합니다
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.title LIKE %:keyword% ORDER BY s.createdAt DESC")
    List<Story> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * 내용에 키워드가 포함된 스토리를 조회합니다
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.content LIKE %:keyword% ORDER BY s.createdAt DESC")
    List<Story> findByContentContaining(@Param("keyword") String keyword);

    /**
     * 제목이나 내용에 검색어가 포함된 스토리를 조회합니다
     * @param searchTerm 검색어
     * @return 검색 조건에 맞는 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.title LIKE %:searchTerm% OR s.content LIKE %:searchTerm% ORDER BY s.createdAt DESC")
    List<Story> searchStories(@Param("searchTerm") String searchTerm);

    /**
     * 특정 회원의 스토리에서 키워드 검색을 수행합니다
     * @param memberId 회원 ID
     * @param searchTerm 검색어
     * @return 검색 조건에 맞는 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.memberId = :memberId AND (s.title LIKE %:searchTerm% OR s.content LIKE %:searchTerm%) ORDER BY s.createdAt DESC")
    List<Story> searchStoriesByMember(@Param("memberId") UUID memberId, @Param("searchTerm") String searchTerm);

    // === 통계 및 개수 조회 메서드 ===

    /**
     * 특정 회원의 스토리 개수를 조회합니다
     * @param memberId 회원 ID
     * @return 해당 회원의 스토리 개수
     */
    long countByMemberId(UUID memberId);

    /**
     * 특정 타입의 스토리 개수를 조회합니다
     * @param storyType 스토리 타입
     * @return 해당 타입의 스토리 개수
     */
    long countByStoryType(StoryType storyType);

    /**
     * 특정 회원의 특정 타입 스토리 개수를 조회합니다
     * @param memberId 회원 ID
     * @param storyType 스토리 타입
     * @return 해당 조건의 스토리 개수
     */
    long countByMemberIdAndStoryType(UUID memberId, StoryType storyType);

    // === 기간별 조회 메서드 ===

    /**
     * 특정 기간 내에 생성된 스토리를 조회합니다
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<Story> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 특정 회원의 특정 기간 내 스토리를 조회합니다
     * @param memberId 회원 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 조건의 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.memberId = :memberId AND s.createdAt BETWEEN :startDate AND :endDate ORDER BY s.createdAt DESC")
    List<Story> findByMemberIdAndCreatedAtBetween(@Param("memberId") UUID memberId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // === 최근 활동 조회 메서드 ===

    /**
     * 특정 회원의 최근 스토리를 제한된 개수만큼 조회합니다
     * @param memberId 회원 ID
     * @param limit 조회할 개수
     * @return 최근 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.memberId = :memberId ORDER BY s.createdAt DESC LIMIT :limit")
    List<Story> findRecentStoriesByMember(@Param("memberId") UUID memberId, @Param("limit") int limit);

    /**
     * 모든 회원의 최근 스토리를 제한된 개수만큼 조회합니다
     * @param limit 조회할 개수
     * @return 최근 스토리 목록
     */
    @Query("SELECT s FROM Story s ORDER BY s.createdAt DESC LIMIT :limit")
    List<Story> findRecentStories(@Param("limit") int limit);

    // === 상황 맥락 관련 메서드 ===

    /**
     * 특정 상황 맥락을 가진 스토리를 조회합니다
     * @param situationContext 상황 맥락
     * @return 해당 상황 맥락의 스토리 목록
     */
    List<Story> findBySituationContextOrderByCreatedAtDesc(String situationContext);

    /**
     * 상황 맥락이 설정된 모든 스토리를 조회합니다
     * @return 상황 맥락이 있는 스토리 목록
     */
    @Query("SELECT s FROM Story s WHERE s.situationContext IS NOT NULL AND s.situationContext != '' ORDER BY s.createdAt DESC")
    List<Story> findStoriesWithSituationContext();

    // === 유효성 검사 메서드 ===

    /**
     * 특정 회원이 특정 제목의 스토리를 가지고 있는지 확인합니다
     * @param memberId 회원 ID
     * @param title 스토리 제목
     * @return 존재 여부
     */
    boolean existsByMemberIdAndTitle(UUID memberId, String title);
} 