package com.voin.repository;

import com.voin.entity.MemberCoin;
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
 * 회원-코인 관계(MemberCoin) 엔티티에 대한 데이터 접근 계층
 * 회원의 코인 보유 현황, 통계 등의 기능을 제공합니다.
 */
@Repository
public interface MemberCoinRepository extends JpaRepository<MemberCoin, Long> {
    
    /**
     * 특정 회원의 모든 코인 보유 현황을 조회합니다
     * @param memberId 회원 ID
     * @return 해당 회원의 코인 보유 목록
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.memberId = :memberId ORDER BY mc.coinId")
    List<MemberCoin> findByMemberId(@Param("memberId") UUID memberId);
    
    /**
     * 특정 회원의 특정 코인 보유 현황을 조회합니다
     * @param memberId 회원 ID
     * @param coinId 코인 ID
     * @return 해당 회원의 특정 코인 보유 정보 (Optional)
     */
    Optional<MemberCoin> findByMemberIdAndCoinId(UUID memberId, Long coinId);

    /**
     * 특정 회원이 코인을 보유하고 있는지 확인합니다
     * @param memberId 회원 ID
     * @param coinId 코인 ID
     * @return 보유 여부
     */
    boolean existsByMemberIdAndCoinId(UUID memberId, Long coinId);

    /**
     * 특정 회원이 보유한 총 코인 수를 조회합니다
     * @param memberId 회원 ID
     * @return 총 코인 보유 수
     */
    @Query("SELECT COALESCE(SUM(mc.count), 0) FROM MemberCoin mc WHERE mc.memberId = :memberId")
    long getTotalCoinCountByMemberId(@Param("memberId") UUID memberId);

    /**
     * 특정 회원의 코인 보유 종류 수를 조회합니다 (0개 이상인 코인들)
     * @param memberId 회원 ID
     * @return 보유 코인 종류 수
     */
    @Query("SELECT COUNT(mc) FROM MemberCoin mc WHERE mc.memberId = :memberId AND mc.count > 0")
    long getCoinTypesCountByMemberId(@Param("memberId") UUID memberId);

    /**
     * 특정 코인을 보유한 모든 회원을 조회합니다
     * @param coinId 코인 ID
     * @return 해당 코인을 보유한 회원-코인 관계 목록
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.coinId = :coinId AND mc.count > 0 ORDER BY mc.count DESC")
    List<MemberCoin> findMembersWithCoin(@Param("coinId") Long coinId);

    /**
     * 특정 코인의 총 보유량을 조회합니다
     * @param coinId 코인 ID
     * @return 해당 코인의 전체 보유량
     */
    @Query("SELECT COALESCE(SUM(mc.count), 0) FROM MemberCoin mc WHERE mc.coinId = :coinId")
    long getTotalCoinCountByCoinId(@Param("coinId") Long coinId);

    /**
     * 가장 많은 코인을 보유한 회원들을 조회합니다
     * @param limit 조회할 회원 수
     * @return 코인 보유량 순 회원-코인 목록
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.count > 0 ORDER BY mc.count DESC LIMIT :limit")
    List<MemberCoin> findTopCoinHolders(@Param("limit") int limit);

    /**
     * 특정 기간 동안 코인을 획득한 회원-코인 관계를 조회합니다
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간 코인 획득 목록
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.lastObtainedAt >= :startDate AND mc.lastObtainedAt <= :endDate ORDER BY mc.lastObtainedAt DESC")
    List<MemberCoin> findCoinObtainedBetween(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * 오늘 처음 코인을 획득한 회원들을 조회합니다
     * @param startOfDay 오늘 시작 시간
     * @param endOfDay 오늘 끝 시간
     * @return 오늘 처음 코인 획득 목록
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.firstObtainedAt >= :startOfDay AND mc.firstObtainedAt < :endOfDay ORDER BY mc.firstObtainedAt DESC")
    List<MemberCoin> findFirstTimeObtainedToday(@Param("startOfDay") LocalDateTime startOfDay, 
                                               @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * 특정 회원의 가장 많이 보유한 코인을 조회합니다
     * @param memberId 회원 ID
     * @return 가장 많이 보유한 코인 (Optional)
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.memberId = :memberId AND mc.count > 0 ORDER BY mc.count DESC LIMIT 1")
    Optional<MemberCoin> findTopCoinByMemberId(@Param("memberId") UUID memberId);

    /**
     * 특정 개수 이상의 코인을 보유한 회원-코인 관계를 조회합니다
     * @param minCount 최소 보유 개수
     * @return 조건에 맞는 회원-코인 목록
     */
    @Query("SELECT mc FROM MemberCoin mc WHERE mc.count >= :minCount ORDER BY mc.count DESC")
    List<MemberCoin> findByCountGreaterThanEqual(@Param("minCount") int minCount);

    /**
     * 특정 회원의 코인 보유 개수를 업데이트합니다
     * @param memberId 회원 ID
     * @param coinId 코인 ID
     * @param newCount 새로운 개수
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE MemberCoin mc SET mc.count = :newCount, mc.lastObtainedAt = CURRENT_TIMESTAMP WHERE mc.memberId = :memberId AND mc.coinId = :coinId")
    int updateCoinCount(@Param("memberId") UUID memberId, 
                       @Param("coinId") Long coinId, 
                       @Param("newCount") int newCount);

    /**
     * 특정 회원의 코인 개수를 증가시킵니다
     * @param memberId 회원 ID
     * @param coinId 코인 ID
     * @param increment 증가량
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE MemberCoin mc SET mc.count = mc.count + :increment, mc.lastObtainedAt = CURRENT_TIMESTAMP WHERE mc.memberId = :memberId AND mc.coinId = :coinId")
    int incrementCoinCount(@Param("memberId") UUID memberId, 
                          @Param("coinId") Long coinId, 
                          @Param("increment") int increment);

    /**
     * 코인별 보유 회원 수 통계를 조회합니다
     * @return 코인 ID와 보유 회원 수 목록 (Object[] 형태: [coinId, memberCount])
     */
    @Query("SELECT mc.coinId, COUNT(DISTINCT mc.memberId) FROM MemberCoin mc WHERE mc.count > 0 GROUP BY mc.coinId ORDER BY mc.coinId")
    List<Object[]> countMembersByCoin();

    /**
     * 코인을 전혀 보유하지 않은 회원들의 ID를 조회합니다
     * @param allMemberIds 전체 회원 ID 목록
     * @return 코인을 보유하지 않은 회원 ID 목록
     */
    @Query("SELECT m.id FROM Member m WHERE m.id IN :allMemberIds AND m.id NOT IN (SELECT DISTINCT mc.memberId FROM MemberCoin mc WHERE mc.count > 0)")
    List<UUID> findMembersWithoutCoins(@Param("allMemberIds") List<UUID> allMemberIds);
} 