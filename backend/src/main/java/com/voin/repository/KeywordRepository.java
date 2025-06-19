package com.voin.repository;

import com.voin.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    
    List<Keyword> findByCoinId(Long coinId);
    
    List<Keyword> findByNameContaining(String name);
} 