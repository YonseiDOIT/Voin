package com.voin.repository;

import com.voin.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 질문(Question) 엔티티에 대한 데이터 접근 계층
 * 폼별 질문 조회, 순서 관리 등의 기능을 제공합니다.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 특정 폼의 모든 질문을 순서대로 조회합니다
     * @param formId 폼 ID
     * @return 해당 폼의 질문 목록 (순서대로)
     */
    @Query("SELECT q FROM Question q WHERE q.form.id = :formId ORDER BY q.orderIndex ASC")
    List<Question> findByFormIdOrderByOrderIndexAsc(@Param("formId") Long formId);

    /**
     * 특정 폼의 특정 순서의 질문을 조회합니다
     * @param formId 폼 ID
     * @param orderIndex 질문 순서
     * @return 해당 조건의 질문 (Optional)
     */
    Optional<Question> findByFormIdAndOrderIndex(Long formId, Integer orderIndex);

    /**
     * 특정 폼의 질문 개수를 조회합니다
     * @param formId 폼 ID
     * @return 해당 폼의 질문 개수
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.form.id = :formId")
    long countByFormId(@Param("formId") Long formId);

    /**
     * 내용에 키워드가 포함된 질문을 조회합니다
     * @param keyword 검색 키워드
     * @return 검색 조건에 맞는 질문 목록
     */
    @Query("SELECT q FROM Question q WHERE q.content LIKE %:keyword% ORDER BY q.form.id, q.orderIndex")
    List<Question> findByContentContaining(@Param("keyword") String keyword);

    /**
     * 특정 폼의 첫 번째 질문을 조회합니다
     * @param formId 폼 ID
     * @return 첫 번째 질문 (Optional)
     */
    @Query("SELECT q FROM Question q WHERE q.form.id = :formId AND q.orderIndex = 1")
    Optional<Question> findFirstQuestionByFormId(@Param("formId") Long formId);

    /**
     * 특정 폼의 마지막 질문을 조회합니다
     * @param formId 폼 ID
     * @return 마지막 질문 (Optional)
     */
    @Query("SELECT q FROM Question q WHERE q.form.id = :formId ORDER BY q.orderIndex DESC LIMIT 1")
    Optional<Question> findLastQuestionByFormId(@Param("formId") Long formId);

    /**
     * 특정 순서 이후의 질문들을 조회합니다
     * @param formId 폼 ID
     * @param afterOrder 기준 순서
     * @return 해당 순서 이후의 질문 목록
     */
    @Query("SELECT q FROM Question q WHERE q.form.id = :formId AND q.orderIndex > :afterOrder ORDER BY q.orderIndex ASC")
    List<Question> findQuestionsAfterOrder(@Param("formId") Long formId, @Param("afterOrder") Integer afterOrder);

    /**
     * 특정 순서 범위의 질문들을 조회합니다
     * @param formId 폼 ID
     * @param startOrder 시작 순서
     * @param endOrder 끝 순서
     * @return 해당 범위의 질문 목록
     */
    @Query("SELECT q FROM Question q WHERE q.form.id = :formId AND q.orderIndex BETWEEN :startOrder AND :endOrder ORDER BY q.orderIndex ASC")
    List<Question> findQuestionsBetweenOrder(@Param("formId") Long formId, 
                                           @Param("startOrder") Integer startOrder, 
                                           @Param("endOrder") Integer endOrder);

    /**
     * 모든 폼의 첫 번째 질문들을 조회합니다
     * @return 각 폼의 첫 번째 질문 목록
     */
    @Query("SELECT q FROM Question q WHERE q.orderIndex = 1 ORDER BY q.form.id")
    List<Question> findAllFirstQuestions();

    /**
     * 질문이 없는 폼들의 ID를 조회합니다
     * @return 질문이 없는 폼 ID 목록
     */
    @Query("SELECT f.id FROM Form f WHERE f.id NOT IN (SELECT DISTINCT q.form.id FROM Question q)")
    List<Long> findFormIdsWithoutQuestions();

    /**
     * 폼별 질문 개수 통계를 조회합니다
     * @return 폼 ID와 질문 개수 목록 (Object[] 형태: [formId, questionCount])
     */
    @Query("SELECT q.form.id, COUNT(q) FROM Question q GROUP BY q.form.id ORDER BY q.form.id")
    List<Object[]> countQuestionsByForm();
} 