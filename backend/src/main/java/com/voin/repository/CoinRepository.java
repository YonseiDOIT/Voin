package com.voin.repository;

import com.voin.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 코인(Coin) 엔티티에 대한 데이터 접근 계층
 * 6가지 고정된 코인 카테고리에 대한 조회 기능을 제공합니다.
 */
@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    
    /**
     * 이름으로 코인을 조회합니다
     * @param name 코인 이름
     * @return 해당 이름의 코인 (Optional)
     */
    Optional<Coin> findByName(String name);
    
    /**
     * 특정 이름의 코인이 존재하는지 확인합니다
     * @param name 코인 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);

    /**
     * 모든 코인을 이름 순으로 조회합니다
     * @return 이름 순으로 정렬된 코인 목록
     */
    List<Coin> findAllByOrderByName();

    /**
     * 설명이 있는 코인들을 조회합니다
     * @return 설명이 있는 코인 목록
     */
    @Query("SELECT c FROM Coin c WHERE c.description IS NOT NULL AND c.description != '' ORDER BY c.name")
    List<Coin> findCoinsWithDescription();

    /**
     * 이름에 검색어가 포함된 코인을 조회합니다
     * @param namePattern 검색 패턴
     * @return 검색 조건에 맞는 코인 목록
     */
    @Query("SELECT c FROM Coin c WHERE c.name LIKE %:namePattern% ORDER BY c.name")
    List<Coin> findByNameContaining(@Param("namePattern") String namePattern);

    /**
     * 특정 색상을 가진 코인들을 조회합니다
     * @param color 색상 코드
     * @return 해당 색상의 코인 목록
     */
    @Query("SELECT c FROM Coin c WHERE c.color = :color ORDER BY c.name")
    List<Coin> findByColor(@Param("color") String color);

    /**
     * 각 코인별 키워드 개수와 함께 조회합니다
     * @return 코인과 키워드 개수 목록 (Object[] 형태: [coin, keywordCount])
     */
    @Query("SELECT c, COUNT(k) FROM Coin c LEFT JOIN Keyword k ON k.coin.id = c.id GROUP BY c.id ORDER BY c.name")
    List<Object[]> findAllWithKeywordCount();

    /**
     * 가장 많은 키워드를 가진 코인들을 조회합니다
     * @param limit 조회할 코인 수
     * @return 키워드 수 순으로 정렬된 코인 목록
     */
    @Query("SELECT c FROM Coin c LEFT JOIN Keyword k ON k.coin.id = c.id GROUP BY c.id ORDER BY COUNT(k) DESC LIMIT :limit")
    List<Coin> findCoinsWithMostKeywords(@Param("limit") int limit);
} 