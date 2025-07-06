package com.voin.repository;

import com.voin.entity.Keyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 키워드(Keyword) 엔티티에 대한 데이터 접근 계층
 * 키워드 조회, 검색, 코인별 분류 등의 기능을 제공합니다.
 */
@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    
    // === 기본 조회 메서드 ===
    
    /**
     * 특정 코인에 속하는 모든 키워드를 조회합니다
     * @param coinId 코인 ID
     * @return 해당 코인의 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.coin.id = :coinId ORDER BY k.name")
    List<Keyword> findByCoinId(@Param("coinId") Long coinId);
    
    /**
     * 이름에 검색어가 포함된 키워드를 조회합니다
     * @param name 검색할 키워드 이름
     * @return 검색 조건에 맞는 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.name LIKE %:name% ORDER BY k.name")
    List<Keyword> findByNameContaining(@Param("name") String name);
    
    /**
     * 특정 코인의 특정 이름을 가진 키워드를 조회합니다
     * @param coinId 코인 ID
     * @param name 키워드 이름
     * @return 해당 조건의 키워드 (Optional)
     */
    Optional<Keyword> findByCoinIdAndName(Long coinId, String name);

    // === 코인별 조회 ===

    /**
     * 특정 코인의 키워드를 페이징으로 조회합니다
     * @param coinId 코인 ID
     * @param pageable 페이징 정보
     * @return 해당 코인의 키워드 페이지
     */
    @Query("SELECT k FROM Keyword k WHERE k.coin.id = :coinId ORDER BY k.name")
    Page<Keyword> findByCoinIdOrderByName(@Param("coinId") Long coinId, Pageable pageable);

    /**
     * 여러 코인 ID에 해당하는 키워드들을 조회합니다
     * @param coinIds 코인 ID 목록
     * @return 해당 코인들의 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.coin.id IN :coinIds ORDER BY k.coin.id, k.name")
    List<Keyword> findByCoinIds(@Param("coinIds") List<Long> coinIds);

    /**
     * 특정 코인 이름으로 키워드들을 조회합니다
     * @param coinName 코인 이름
     * @return 해당 코인의 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.coin.name = :coinName ORDER BY k.name")
    List<Keyword> findByCoinName(@Param("coinName") String coinName);

    // === 검색 기능 ===

    /**
     * 키워드 이름이나 설명에 검색어가 포함된 키워드를 조회합니다
     * @param searchTerm 검색어
     * @return 검색 조건에 맞는 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.name LIKE %:searchTerm% OR k.description LIKE %:searchTerm% ORDER BY k.name")
    List<Keyword> searchKeywords(@Param("searchTerm") String searchTerm);

    /**
     * 특정 코인 내에서 키워드를 검색합니다
     * @param coinId 코인 ID
     * @param searchTerm 검색어
     * @return 검색 조건에 맞는 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.coin.id = :coinId AND (k.name LIKE %:searchTerm% OR k.description LIKE %:searchTerm%) ORDER BY k.name")
    List<Keyword> searchKeywordsInCoin(@Param("coinId") Long coinId, @Param("searchTerm") String searchTerm);

    // === 통계 및 집계 ===

    /**
     * 특정 코인의 키워드 개수를 조회합니다
     * @param coinId 코인 ID
     * @return 해당 코인의 키워드 개수
     */
    @Query("SELECT COUNT(k) FROM Keyword k WHERE k.coin.id = :coinId")
    long countByCoinId(@Param("coinId") Long coinId);

    /**
     * 코인별 키워드 개수를 조회합니다
     * @return 코인 ID와 키워드 개수 맵핑 (Object[] 형태: [coinId, count])
     */
    @Query("SELECT k.coin.id, COUNT(k) FROM Keyword k GROUP BY k.coin.id ORDER BY k.coin.id")
    List<Object[]> countKeywordsByCoin();

    /**
     * 설명이 있는 키워드 개수를 조회합니다
     * @return 설명이 있는 키워드 개수
     */
    @Query("SELECT COUNT(k) FROM Keyword k WHERE k.description IS NOT NULL AND k.description != ''")
    long countKeywordsWithDescription();

    // === 특수 조회 ===

    /**
     * 설명이 없는 키워드들을 조회합니다
     * @return 설명이 없는 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.description IS NULL OR k.description = '' ORDER BY k.coin.id, k.name")
    List<Keyword> findKeywordsWithoutDescription();

    /**
     * 특정 키워드 이름들에 해당하는 키워드들을 조회합니다
     * @param keywordNames 키워드 이름 목록
     * @return 해당 이름들의 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.name IN :keywordNames ORDER BY k.name")
    List<Keyword> findByNames(@Param("keywordNames") List<String> keywordNames);

    /**
     * 가장 많이 사용된 키워드들을 조회합니다 (카드 작성 기준)
     * @param limit 조회할 키워드 수
     * @return 사용 빈도 순 키워드 목록
     */
    @Query("SELECT k FROM Keyword k " +
           "LEFT JOIN Card c ON c.keyword.id = k.id " +
           "GROUP BY k.id " +
           "ORDER BY COUNT(c) DESC " +
           "LIMIT :limit")
    List<Keyword> findMostUsedKeywords(@Param("limit") int limit);

    /**
     * 사용되지 않은 키워드들을 조회합니다 (카드가 없는 키워드)
     * @return 사용되지 않은 키워드 목록
     */
    @Query("SELECT k FROM Keyword k WHERE k.id NOT IN (SELECT DISTINCT c.keyword.id FROM Card c) ORDER BY k.coin.id, k.name")
    List<Keyword> findUnusedKeywords();

    /**
     * 특정 코인의 랜덤 키워드를 조회합니다
     * @param coinId 코인 ID
     * @param limit 조회할 키워드 수
     * @return 랜덤 키워드 목록
     */
    @Query(value = "SELECT k.* FROM keywords k WHERE k.coin_id = :coinId ORDER BY RANDOM() LIMIT :limit", 
           nativeQuery = true)
    List<Keyword> findRandomKeywordsByCoinId(@Param("coinId") Long coinId, @Param("limit") int limit);

    // === 존재 여부 확인 ===

    /**
     * 특정 코인에 해당 이름의 키워드가 존재하는지 확인합니다
     * @param coinId 코인 ID
     * @param name 키워드 이름
     * @return 존재 여부
     */
    boolean existsByCoinIdAndName(Long coinId, String name);

    /**
     * 특정 이름의 키워드가 존재하는지 확인합니다
     * @param name 키워드 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);
} 