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

        Coin managementGrowth = savedCoins.get(0); // 관리와 성장
        Coin emotionAttitude = savedCoins.get(1);  // 감정과 태도  
        Coin creativityFocus = savedCoins.get(2);   // 창의와 몰입
        Coin thinkingSolving = savedCoins.get(3);   // 사고와 해결
        Coin relationshipEmpathy = savedCoins.get(4); // 관계와 공감
        Coin beliefExecution = savedCoins.get(5);   // 신념과 실행

        // 관리와 성장 키워드
        List<Keyword> managementKeywords = Arrays.asList(
                Keyword.builder().coin(managementGrowth).name("계획성").description("체계적으로 계획을 세우고 실행하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("조직력").description("일이나 물건을 효율적으로 정리하고 관리하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("끈기").description("어려움이 있어도 포기하지 않고 지속하는 의지").build(),
                Keyword.builder().coin(managementGrowth).name("성장마인드").description("배움과 발전에 대한 열린 자세와 의지").build(),
                Keyword.builder().coin(managementGrowth).name("목표지향").description("명확한 목표를 설정하고 달성하려는 의지").build(),
                Keyword.builder().coin(managementGrowth).name("자기관리").description("자신의 시간과 에너지를 효과적으로 관리하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("학습능력").description("새로운 지식과 기술을 빠르게 습득하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("개선의지").description("현재 상황을 더 나은 방향으로 개선하려는 의지").build(),
                Keyword.builder().coin(managementGrowth).name("자기성찰").description("자신의 행동과 생각을 돌아보고 분석하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("인내심").description("힘든 상황에서도 참고 견디는 정신력").build(),
                Keyword.builder().coin(managementGrowth).name("자율성").description("스스로 판단하고 행동할 수 있는 독립적 사고력").build(),
                Keyword.builder().coin(managementGrowth).name("완결성").description("시작한 일을 끝까지 완료하려는 책임감").build()
        );

        // 감정과 태도 키워드
        List<Keyword> emotionKeywords = Arrays.asList(
                Keyword.builder().coin(emotionAttitude).name("긍정성").description("밝고 낙관적인 마음가짐으로 상황을 바라보는 태도").build(),
                Keyword.builder().coin(emotionAttitude).name("유머감각").description("상황을 재미있게 만들고 분위기를 밝게 하는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("열정").description("일이나 목표에 대한 뜨거운 관심과 의욕").build(),
                Keyword.builder().coin(emotionAttitude).name("친근함").description("다른 사람에게 편안함과 호감을 주는 성격").build(),
                Keyword.builder().coin(emotionAttitude).name("차분함").description("급하지 않고 침착하게 상황을 대하는 태도").build(),
                Keyword.builder().coin(emotionAttitude).name("안정감").description("다른 사람에게 심리적 편안함을 주는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("활력").description("생기있고 에너지 넘치는 모습").build(),
                Keyword.builder().coin(emotionAttitude).name("겸손함").description("자신을 낮추고 다른 사람을 존중하는 태도").build(),
                Keyword.builder().coin(emotionAttitude).name("정서조절").description("자신의 감정을 적절히 관리하고 표현하는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("회복탄력성").description("어려운 상황에서 빠르게 회복하는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("감사마음").description("주변에 대한 고마움을 느끼고 표현하는 태도").build(),
                Keyword.builder().coin(emotionAttitude).name("자신감").description("자신의 능력과 가치를 믿는 마음").build()
        );

        // 창의와 몰입 키워드
        List<Keyword> creativityKeywords = Arrays.asList(
                Keyword.builder().coin(creativityFocus).name("창의력").description("기존과 다른 새로운 아이디어를 만들어내는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("호기심").description("새로운 것에 대한 관심과 탐구하려는 마음").build(),
                Keyword.builder().coin(creativityFocus).name("집중력").description("한 가지 일에 깊이 몰입할 수 있는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("상상력").description("현실을 넘어서는 새로운 것을 그려내는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("예술감각").description("아름다움과 조화를 느끼고 표현하는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("실험정신").description("새로운 방법을 시도해보려는 도전적 태도").build(),
                Keyword.builder().coin(creativityFocus).name("몰입력").description("시간 가는 줄 모르고 빠져드는 집중 능력").build(),
                Keyword.builder().coin(creativityFocus).name("유연성").description("상황에 따라 생각과 행동을 조정하는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("독창성").description("남과 다른 자신만의 특별한 관점과 아이디어").build(),
                Keyword.builder().coin(creativityFocus).name("탐구력").description("깊이 있게 파고들어 알아내려는 의지").build(),
                Keyword.builder().coin(creativityFocus).name("표현력").description("자신의 생각과 감정을 효과적으로 드러내는 능력").build(),
                Keyword.builder().coin(creativityFocus).name("관찰력").description("세밀한 부분까지 주의 깊게 살펴보는 능력").build()
        );

        // 사고와 해결 키워드
        List<Keyword> thinkingKeywords = Arrays.asList(
                Keyword.builder().coin(thinkingSolving).name("논리력").description("체계적이고 합리적으로 생각하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("문제해결").description("복잡한 문제를 분석하고 해결책을 찾는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("분석력").description("정보를 세밀하게 분석하고 패턴을 찾는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("비판적사고").description("정보를 객관적으로 평가하고 판단하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("통찰력").description("사물의 본질을 꿰뚫어 보는 깊은 이해력").build(),
                Keyword.builder().coin(thinkingSolving).name("전략적사고").description("장기적 관점에서 계획을 세우는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("추론능력").description("주어진 정보로부터 결론을 도출하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("체계화").description("복잡한 정보를 정리하고 구조화하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("판단력").description("상황을 정확히 파악하고 올바른 결정을 내리는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("효율성").description("적은 노력으로 최대의 결과를 얻는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("정확성").description("실수 없이 정밀하게 일을 처리하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("합리성").description("감정보다는 이성에 기반하여 판단하는 태도").build()
        );

        // 관계와 공감 키워드
        List<Keyword> relationshipKeywords = Arrays.asList(
                Keyword.builder().coin(relationshipEmpathy).name("공감능력").description("다른 사람의 감정과 상황을 이해하는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("소통능력").description("상대방과 효과적으로 의사소통하는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("배려심").description("다른 사람을 생각하고 챙기는 마음").build(),
                Keyword.builder().coin(relationshipEmpathy).name("협력").description("다른 사람과 힘을 합쳐 일을 해내는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("사교성").description("사람들과 어울리고 관계를 맺는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("포용력").description("다른 사람의 다름을 받아들이는 넓은 마음").build(),
                Keyword.builder().coin(relationshipEmpathy).name("친화력").description("사람들과 쉽게 친해지는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("경청").description("다른 사람의 말을 주의 깊게 듣는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("화합").description("서로 다른 사람들을 조화롭게 어우르는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("신뢰감").description("다른 사람에게 믿음과 안정감을 주는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("중재력").description("갈등 상황에서 조정하고 해결하는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("격려").description("다른 사람에게 용기와 힘을 주는 능력").build()
        );

        // 신념과 실행 키워드
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