package com.voin.service;

import com.voin.constant.StoryType;
import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.repository.CoinRepository;
import com.voin.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MasterDataService {

    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;

    /**
     * 모든 코인 목록 조회
     */
    public List<Coin> getAllCoins() {
        return coinRepository.findAll();
    }

    /**
     * 특정 코인의 키워드 목록 조회
     */
    public List<Keyword> getKeywordsByCoin(Long coinId) {
        return keywordRepository.findByCoinId(coinId);
    }

    /**
     * 모든 키워드를 코인별로 그룹화하여 조회
     */
    public Map<String, List<Keyword>> getKeywordsGroupedByCoin() {
        List<Coin> coins = coinRepository.findAll();
        Map<String, List<Keyword>> result = new LinkedHashMap<>();
        
        for (Coin coin : coins) {
            List<Keyword> keywords = keywordRepository.findByCoinId(coin.getId());
            result.put(coin.getName(), keywords);
        }
        
        return result;
    }

    /**
     * 상황 맥락 옵션 조회 (사례 돌아보기용)
     */
    public List<Map<String, Object>> getSituationContexts() {
        List<Map<String, Object>> contexts = new ArrayList<>();
        
        contexts.add(Map.of("id", 1, "name", "학교/학업", "description", "학교나 학업과 관련된 상황"));
        contexts.add(Map.of("id", 2, "name", "직장/업무", "description", "직장이나 업무와 관련된 상황"));
        contexts.add(Map.of("id", 3, "name", "가족/친구", "description", "가족이나 친구와의 관계 상황"));
        contexts.add(Map.of("id", 4, "name", "취미/여가", "description", "취미나 여가 활동 상황"));
        contexts.add(Map.of("id", 5, "name", "일상생활", "description", "일반적인 일상생활 상황"));
        contexts.add(Map.of("id", 6, "name", "기타", "description", "기타 특별한 상황"));
        
        return contexts;
    }

    /**
     * 스토리 타입 옵션 조회
     */
    public List<Map<String, Object>> getStoryTypes() {
        List<Map<String, Object>> storyTypes = new ArrayList<>();
        
        storyTypes.add(Map.of(
            "type", StoryType.DAILY_DIARY.name(),
            "title", "오늘의 일기",
            "description", "오늘의 일상을 기록하면서 내 장점을 찾아봐요",
            "category", "나의 장점 코인"
        ));
        
        storyTypes.add(Map.of(
            "type", StoryType.EXPERIENCE_REFLECTION.name(),
            "title", "사례 돌아보기",
            "description", "이전 경험을 돌아보면서 내 장점을 찾아봐요",
            "category", "나의 장점 코인"
        ));
        
        return storyTypes;
    }

    /**
     * 전체 마스터 데이터 조회
     */
    public Map<String, Object> getAllMasterData() {
        Map<String, Object> masterData = new HashMap<>();
        
        masterData.put("coins", getAllCoins());
        masterData.put("keywordsByCoin", getKeywordsGroupedByCoin());
        masterData.put("situationContexts", getSituationContexts());
        masterData.put("storyTypes", getStoryTypes());
        
        return masterData;
    }
} 