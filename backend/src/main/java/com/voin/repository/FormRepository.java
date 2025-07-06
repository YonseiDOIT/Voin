package com.voin.repository;

import com.voin.constant.FormType;
import com.voin.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 폼(Form) 엔티티에 대한 데이터 접근 계층
 * 폼 조회, 검색, 타입별 분류 등의 기능을 제공합니다.
 */
@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    /**
     * 특정 타입의 폼들을 최신 순으로 조회합니다
     * @param type 폼 타입
     * @return 해당 타입의 폼 목록 (최신순)
     */
    List<Form> findByTypeOrderByCreatedAtDesc(FormType type);

    /**
     * 모든 폼을 최신 순으로 조회합니다
     * @return 전체 폼 목록 (최신순)
     */
    List<Form> findByOrderByCreatedAtDesc();

    /**
     * 제목에 키워드가 포함된 폼을 조회합니다
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 폼 목록
     */
    @Query("SELECT f FROM Form f WHERE f.title LIKE %:keyword% ORDER BY f.createdAt DESC")
    List<Form> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * 특정 타입의 폼 개수를 조회합니다
     * @param type 폼 타입
     * @return 해당 타입의 폼 개수
     */
    @Query("SELECT COUNT(f) FROM Form f WHERE f.type = :type")
    long countByType(@Param("type") FormType type);

    /**
     * 제목으로 폼을 조회합니다
     * @param title 폼 제목
     * @return 해당 제목의 폼 (Optional)
     */
    Optional<Form> findByTitle(String title);

    /**
     * 제목이나 설명에 검색어가 포함된 폼을 조회합니다
     * @param searchTerm 검색어
     * @return 검색 조건에 맞는 폼 목록
     */
    @Query("SELECT f FROM Form f WHERE f.title LIKE %:searchTerm% OR f.description LIKE %:searchTerm% ORDER BY f.createdAt DESC")
    List<Form> searchForms(@Param("searchTerm") String searchTerm);

    /**
     * 특정 타입의 폼을 이름 순으로 조회합니다
     * @param type 폼 타입
     * @return 해당 타입의 폼 목록 (이름순)
     */
    List<Form> findByTypeOrderByTitle(FormType type);

    /**
     * 설명이 있는 폼들을 조회합니다
     * @return 설명이 있는 폼 목록
     */
    @Query("SELECT f FROM Form f WHERE f.description IS NOT NULL AND f.description != '' ORDER BY f.title")
    List<Form> findFormsWithDescription();

    /**
     * 가장 많이 사용된 폼들을 조회합니다 (카드 작성 기준)
     * @param limit 조회할 폼 수
     * @return 사용 빈도 순 폼 목록
     */
    @Query("SELECT f FROM Form f " +
           "LEFT JOIN Card c ON c.form.id = f.id " +
           "GROUP BY f.id " +
           "ORDER BY COUNT(c) DESC " +
           "LIMIT :limit")
    List<Form> findMostUsedForms(@Param("limit") int limit);

    /**
     * 사용되지 않은 폼들을 조회합니다 (카드가 없는 폼)
     * @return 사용되지 않은 폼 목록
     */
    @Query("SELECT f FROM Form f WHERE f.id NOT IN (SELECT DISTINCT c.form.id FROM Card c) ORDER BY f.title")
    List<Form> findUnusedForms();

    /**
     * 폼별 사용 통계를 조회합니다
     * @return 폼과 사용 횟수 목록 (Object[] 형태: [form, usageCount])
     */
    @Query("SELECT f, COUNT(c) FROM Form f LEFT JOIN Card c ON c.form.id = f.id GROUP BY f.id ORDER BY COUNT(c) DESC")
    List<Object[]> findFormUsageStatistics();
} 