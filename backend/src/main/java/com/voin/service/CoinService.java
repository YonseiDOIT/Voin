package com.voin.service;

import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.repository.CoinRepository;
import com.voin.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinService {

    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;

    public List<CoinWithKeywordsDto> getCoinsWithKeywords() {
        List<Coin> coins = coinRepository.findAll();
        List<Keyword> allKeywords = keywordRepository.findAll();

        Map<Long, List<Keyword>> keywordsByCoinId = allKeywords.stream()
                .collect(Collectors.groupingBy(keyword -> keyword.getCoin().getId()));

        return coins.stream()
                .map(coin -> {
                    List<Keyword> keywordsForCoin = keywordsByCoinId.getOrDefault(coin.getId(), List.of());
                    List<KeywordDto> keywordDtos = keywordsForCoin.stream()
                            .map(keyword -> new KeywordDto(keyword.getId(), keyword.getName()))
                            .collect(Collectors.toList());
                    return new CoinWithKeywordsDto(coin.getId(), coin.getName(), keywordDtos);
                })
                .collect(Collectors.toList());
    }

    public static class CoinWithKeywordsDto {
        private final Long id;
        private final String name;
        private final List<KeywordDto> keywords;

        public CoinWithKeywordsDto(Long id, String name, List<KeywordDto> keywords) {
            this.id = id;
            this.name = name;
            this.keywords = keywords;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public List<KeywordDto> getKeywords() { return keywords; }
    }

    public static class KeywordDto {
        private final Long id;
        private final String name;

        public KeywordDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }
} 