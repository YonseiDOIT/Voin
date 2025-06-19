package com.voin.repository;

import com.voin.constant.FormType;
import com.voin.entity.Form;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    List<Form> findByTypeOrderByCreatedAtDesc(FormType type);

    List<Form> findByOrderByCreatedAtDesc();

    @Query("SELECT f FROM Form f WHERE f.title LIKE %:keyword%")
    List<Form> findByTitleContaining(@Param("keyword") String keyword);

    @Query("SELECT COUNT(f) FROM Form f WHERE f.type = :type")
    long countByType(@Param("type") FormType type);
} 