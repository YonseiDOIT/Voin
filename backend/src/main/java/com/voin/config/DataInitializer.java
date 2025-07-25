package com.voin.config;

import com.voin.entity.Coin;
import com.voin.entity.Keyword;
import com.voin.repository.CoinRepository;
import com.voin.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class DataInitializer {

    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeData() {
        log.info("Initializing data after application startup...");
        initializeCoins();
        initializeKeywords();
        log.info("Data initialization completed successfully!");
    }

    private void initializeCoins() {
        if (coinRepository.count() > 0) {
            log.info("Coins already initialized");
            return;
        }

        List<Coin> coins = Arrays.asList(
                Coin.builder()
                    .name("관리와 성장")
                    .description("목표를 향해 꾸준히 나아가며 체계적으로 성장하는 가치")
                    .color("#FF6B6B")
                    .build(),
                Coin.builder()
                    .name("감정과 태도")
                    .description("긍정적인 감정과 밝은 에너지로 주변을 환하게 만드는 가치")
                    .color("#4ECDC4")
                    .build(),
                Coin.builder()
                    .name("창의와 몰입")
                    .description("호기심과 창의력으로 새로운 것을 탐구하고 몰입하는 가치")
                    .color("#45B7D1")
                    .build(),
                Coin.builder()
                    .name("사고와 해결")
                    .description("논리적 사고와 통찰력으로 문제를 해결하는 가치")
                    .color("#F7DC6F")
                    .build(),
                Coin.builder()
                    .name("관계와 공감")
                    .description("타인을 이해하고 배려하며 따뜻한 관계를 만드는 가치")
                    .color("#BB8FCE")
                    .build(),
                Coin.builder()
                    .name("신념과 실행")
                    .description("자신의 가치와 믿음을 실행으로 옮기는 힘")
                    .color("#F1948A")
                    .build()
        );

        coinRepository.saveAll(coins);
        log.info("Initialized {} coins", coins.size());
    }

    private void initializeKeywords() {
        if (keywordRepository.count() > 0) {
            log.info("Keywords already initialized");
            return;
        }

        // 저장된 코인들을 다시 조회
        List<Coin> savedCoins = coinRepository.findAllByOrderByName();
        
        if (savedCoins.size() != 6) {
            throw new RuntimeException("Expected 6 coins but found " + savedCoins.size());
        }

        Coin managementGrowth = savedCoins.get(0);  // 관리와 성장
        Coin emotionAttitude = savedCoins.get(1);   // 감정과 태도  
        Coin creativityFocus = savedCoins.get(2);   // 창의와 몰입
        Coin thinkingSolving = savedCoins.get(3);   // 사고와 해결
        Coin relationshipEmpathy = savedCoins.get(4); // 관계와 공감
        Coin beliefExecution = savedCoins.get(5);   // 신념과 실행

        // 1 : 관리와 성장 키워드
        List<Keyword> managementKeywords = Arrays.asList(
                Keyword.builder().coin(managementGrowth).name("끈기").description("목표를 향해 포기하지 않고 꾸준히 나아가는 힘").build(),
                Keyword.builder().coin(managementGrowth).name("인내심").description("어려움이나 불편함을 참고 견디는 태도").build(),
                Keyword.builder().coin(managementGrowth).name("성실함").description("맡은 일에 꾸준히 최선을 다하는 태도").build(),
                Keyword.builder().coin(managementGrowth).name("절제력").description("욕구나 충동을 이성적으로 통제하는 힘").build(),
                Keyword.builder().coin(managementGrowth).name("침착함").description("감정이나 상황에 흔들리지 않고 차분히 대응하는 태도").build(),
                Keyword.builder().coin(managementGrowth).name("학습력").description("새로운 지식이나 경험을 빠르게 이해하고 익히는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("성찰력").description("자신을 되돌아보고 의미를 찾는 내면적 사고 태도").build(),
                Keyword.builder().coin(managementGrowth).name("적응력").description("새로운 변화에 빠르고 유연하게 적응하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("수용성").description("피드백이나 의견을 열린 태도로 받아들이는 자세").build(),
        );

        // 2 : 감정과 태도 키워드
        List<Keyword> emotionKeywords = Arrays.asList(
                Keyword.builder().coin(emotionAttitude).name("유머 감각").description("주변을 웃게 만드는 센스").build(),
                Keyword.builder().coin(emotionAttitude).name("감수성").description("섬세한 감정과 풍부한 감성으로 세상을 바라보는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("표현력").description("감정이나 생각을 솔직하고 풍부하게 표현하는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("밝은 에너지").description("주변까지 환하게 만드는 활기찬 에너지").build(),
                Keyword.builder().coin(emotionAttitude).name("긍정성").description("상황을 긍정적으로 받아들이는 태도").build(),
                Keyword.builder().coin(emotionAttitude).name("열정").description("무언가에 강한 의욕과 에너지를 갖고 임하는 태도").build(),
        );

        // 3 : 창의와 몰입 키워드
        List<Keyword> creativityKeywords = Arrays.asList(
                Keyword.builder().coin(creativityFocus).name("호기심").description("새로운 것에 관심을 갖고 반응하는 태도").build(),
                Keyword.builder().coin(creativityFocus).name("탐구력").description("알고자 하는 대상을 깊게 연구하고 파고드는 태도").build(),
                Keyword.builder().coin(creativityFocus).name("창의력").description("기존 틀을 넘어 새로운 아이디어를 떠올리는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("집중력").description("하나의 일에 주의를 모아 지속하는 태도").build(),
                Keyword.builder().coin(creativityFocus).name("몰입력").description("하나의 일에 깊이 빠져 몰두하는 태도").build(),
                Keyword.builder().coin(creativityFocus).name("기획력").description("아이디어를 구체적으로 구조화하는 능력").build(),
        );

        // 4 : 사고와 해결 키워드
        List<Keyword> thinkingKeywords = Arrays.asList(
                Keyword.builder().coin(thinkingSolving).name("판단력").description("주어진 조건에서 가장 적절한 결정을 내리는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("논리력").description("생각의 근거를 정리하고, 조리 있게 전개하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("분석력").description("자료나 현상을 논리적으로 파악하고 해석하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("통찰력").description("본질을 꿰뚫어보고 전체를 이해하는 사고력").build(),
                Keyword.builder().coin(thinkingSolving).name("신중성").description("충동보다 깊은 사고로 판단하는 태도").build(),
                Keyword.builder().coin(thinkingSolving).name("문제해결력").description("문제를 분석하고 구조적으로 해결해나가는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("융통성").description("상황에 맞게 사고와 행동을 유연하게 조절하는 능력").build(),
        );

        // 5 : 관계와 공감 키워드
        List<Keyword> relationshipKeywords = Arrays.asList(
                Keyword.builder().coin(relationshipEmpathy).name("공감력").description("다른 사람의 감정과 상황을 이해하는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("배려심").description("상대방과 효과적으로 의사소통하는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("포용력").description("다른 사람을 생각하고 챙기는 마음").build(),
                Keyword.builder().coin(relationshipEmpathy).name("경청 태도").description("다른 사람과 힘을 합쳐 일을 해내는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("친화력").description("사람들과 어울리고 관계를 맺는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("지지력").description("다른 사람의 다름을 받아들이는 넓은 마음").build(),
                Keyword.builder().coin(relationshipEmpathy).name("온화함").description("사람들과 쉽게 친해지는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("중재력").description("다른 사람의 말을 주의 깊게 듣는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("조율력").description("서로 다른 사람들을 조화롭게 어우르는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("겸손함").description("다른 사람에게 믿음과 안정감을 주는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("예의 바름").description("갈등 상황에서 조정하고 해결하는 능력").build(),
        );

        // 6 : 신념과 실행 키워드
        List<Keyword> beliefKeywords = Arrays.asList(
                Keyword.builder().coin(beliefExecution).name("신념").description("자신의 가치와 믿음을 지키는 태도").build(),
                Keyword.builder().coin(beliefExecution).name("주체성").description("주변에 휘둘리지 않고, 스스로 판단하는 태도").build(),
                Keyword.builder().coin(beliefExecution).name("정직함").description("사실을 왜곡하지 않고, 신뢰를 지키는 태도").build(),
                Keyword.builder().coin(beliefExecution).name("정의감").description("옳고 그름에 민감히 반응하고, 불의에 맞서는 태도").build(),
                Keyword.builder().coin(beliefExecution).name("도덕심").description("사회적으로 바른 가치 기준을 지키려는 마음가짐").build(),
                Keyword.builder().coin(beliefExecution).name("용기").description("두려움을 이기고 행동으로 옮기는 힘").build(),
                Keyword.builder().coin(beliefExecution).name("결단력").description("과감하게 결정을 내리고 실행으로 옮기는 힘").build(),
                Keyword.builder().coin(beliefExecution).name("주도성").description("팀이나 상황을 이끌고 먼저 실행하는 힘").build(),
                Keyword.builder().coin(beliefExecution).name("실행력").description("계획한 일을 실제로 옮겨서 실행해 나가는 추진력").build(),
                Keyword.builder().coin(beliefExecution).name("리더십").description("사람들을 이끌고 방향을 제시하는 능력").build(),
                Keyword.builder().coin(beliefExecution).name("공정성").description("편견 없이 균형 있게 판단하고 존중하는 태도").build(),
                Keyword.builder().coin(beliefExecution).name("책임감").description("맡은 일이나 역할을 끝까지 해내려는 태도와 의지").build()
                Keyword.builder().coin(beliefExecution).name("계획성").description("목표 달성을 위해 체계적으로 준비하는 능력").build()
                Keyword.builder().coin(beliefExecution).name("도전력").description("새로운 가능성에 적극적으로 뛰어드는 태도").build()
        );

        // 모든 키워드 저장
        keywordRepository.saveAll(managementKeywords);
        keywordRepository.saveAll(emotionKeywords);
        keywordRepository.saveAll(creativityKeywords);
        keywordRepository.saveAll(thinkingKeywords);
        keywordRepository.saveAll(relationshipKeywords);
        keywordRepository.saveAll(beliefKeywords);

        int totalKeywords = managementKeywords.size() + emotionKeywords.size() + creativityKeywords.size() + 
                           thinkingKeywords.size() + relationshipKeywords.size() + beliefKeywords.size();
        
        log.info("Initialized {} keywords for {} coins", totalKeywords, savedCoins.size());
    }
} 