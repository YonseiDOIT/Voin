package com.voin.repository;

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
import java.util.Optional;
import java.util.UUID;

/**
 * 회원(Member) 엔티티에 대한 데이터 접근 계층
 * 회원 조회, 검색, 통계 등의 기능을 제공합니다.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

    // === 기본 조회 메서드 ===

    /**
     * 카카오 ID로 회원을 조회합니다
     * @param kakaoId 카카오 사용자 ID
     * @return 해당 카카오 ID를 가진 회원 (Optional)
     */
    Optional<Member> findByKakaoId(String kakaoId);

    /**
     * 친구 코드로 회원을 조회합니다
     * @param friendCode 8자리 친구 코드
     * @return 해당 친구 코드를 가진 회원 (Optional)
     */
    Optional<Member> findByFriendCode(String friendCode);

    /**
     * 닉네임으로 회원을 조회합니다
     * @param nickname 회원 닉네임
     * @return 해당 닉네임을 가진 회원 (Optional)
     */
    Optional<Member> findByNickname(String nickname);

    // === 존재 여부 확인 메서드 ===

    /**
     * 카카오 ID 존재 여부를 확인합니다
     * @param kakaoId 카카오 사용자 ID
     * @return 존재 여부
     */
    boolean existsByKakaoId(String kakaoId);

    /**
     * 친구 코드 존재 여부를 확인합니다
     * @param friendCode 8자리 친구 코드
     * @return 존재 여부
     */
    boolean existsByFriendCode(String friendCode);

    /**
     * 닉네임 존재 여부를 확인합니다
     * @param nickname 회원 닉네임
     * @return 존재 여부
     */
    boolean existsByNickname(String nickname);

    // === 활성 회원 조회 메서드 ===

    /**
     * 모든 활성 회원을 조회합니다
     * @return 활성 상태인 모든 회원 목록
     */
    List<Member> findByIsActiveTrue();

    /**
     * 활성 회원을 페이징으로 조회합니다
     * @param pageable 페이징 정보
     * @return 활성 회원 페이지
     */
    Page<Member> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 닉네임에 키워드가 포함된 활성 회원을 조회합니다
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 활성 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.nickname LIKE %:keyword% AND m.isActive = true ORDER BY m.nickname")
    List<Member> findByNicknameContainingAndIsActiveTrue(@Param("keyword") String keyword);

    /**
     * 닉네임에 키워드가 포함된 활성 회원을 페이징으로 조회합니다
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 검색 조건에 맞는 활성 회원 페이지
     */
    @Query("SELECT m FROM Member m WHERE m.nickname LIKE %:keyword% AND m.isActive = true ORDER BY m.nickname")
    Page<Member> findByNicknameContainingAndIsActiveTrue(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 여러 회원 ID에 해당하는 활성 회원들을 조회합니다
     * @param memberIds 회원 ID 목록
     * @return 해당 ID들의 활성 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.id IN :memberIds AND m.isActive = true ORDER BY m.nickname")
    List<Member> findByIdInAndIsActiveTrue(@Param("memberIds") List<UUID> memberIds);

    // === 통계 및 집계 메서드 ===

    /**
     * 총 활성 회원 수를 조회합니다
     * @return 활성 회원 수
     */
    long countByIsActiveTrue();

    /**
     * 오늘 가입한 회원 수를 조회합니다
     * @param startOfDay 오늘 시작 시간
     * @param endOfDay 오늘 끝 시간
     * @return 오늘 가입한 회원 수
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.createdAt >= :startOfDay AND m.createdAt < :endOfDay")
    long countMembersJoinedToday(@Param("startOfDay") LocalDateTime startOfDay, 
                                @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 특정 기간 동안 가입한 회원 수를 조회합니다
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 가입 회원 수
     */
    @Query("SELECT COUNT(m) FROM Member m WHERE m.createdAt >= :startDate AND m.createdAt <= :endDate")
    long countMembersJoinedBetween(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);

    // === 업데이트 메서드 ===

    /**
     * 회원 상태를 일괄 업데이트합니다
     * @param memberIds 대상 회원 ID들
     * @param isActive 새로운 활성 상태
     * @return 업데이트된 회원 수
     */
    @Modifying
    @Query("UPDATE Member m SET m.isActive = :isActive WHERE m.id IN :memberIds")
    int updateMemberActiveStatus(@Param("memberIds") List<UUID> memberIds, 
                                @Param("isActive") boolean isActive);

    /**
     * 회원의 프로필 이미지를 업데이트합니다
     * @param memberId 회원 ID
     * @param profileImage 새로운 프로필 이미지 URL
     * @return 업데이트된 회원 수
     */
    @Modifying
    @Query("UPDATE Member m SET m.profileImage = :profileImage WHERE m.id = :memberId")
    int updateMemberProfileImage(@Param("memberId") UUID memberId, 
                                @Param("profileImage") String profileImage);

    // === 특수 조회 메서드 ===

    /**
     * 최근에 가입한 활성 회원들을 조회합니다
     * @param limit 조회할 회원 수
     * @return 최근 가입한 활성 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true ORDER BY m.createdAt DESC LIMIT :limit")
    List<Member> findRecentActiveMembers(@Param("limit") int limit);

    /**
     * 프로필 이미지가 없는 활성 회원들을 조회합니다
     * @return 프로필 이미지가 없는 활성 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.isActive = true AND (m.profileImage IS NULL OR m.profileImage = '')")
    List<Member> findActiveMembersWithoutProfileImage();

    /**
     * 특정 친구 코드 패턴과 일치하는 회원들을 조회합니다
     * @param pattern 친구 코드 패턴 (예: "VN%")
     * @return 패턴과 일치하는 회원 목록
     */
    @Query("SELECT m FROM Member m WHERE m.friendCode LIKE :pattern AND m.isActive = true ORDER BY m.friendCode")
    List<Member> findByFriendCodePattern(@Param("pattern") String pattern);
} 