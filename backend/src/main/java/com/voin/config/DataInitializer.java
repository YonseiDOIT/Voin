package com.voin.config;

import com.voin.entity.Coin;
import com.voin.entity.Form;
import com.voin.entity.Keyword;
import com.voin.entity.Question;
import com.voin.repository.CoinRepository;
import com.voin.repository.FormRepository;
import com.voin.repository.KeywordRepository;
import com.voin.repository.QuestionRepository;
import com.voin.constant.FormType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CoinRepository coinRepository;
    private final KeywordRepository keywordRepository;
    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeCoins();
        initializeKeywords();
        initializeForms();
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
                    .description("확고한 신념과 강한 실행력으로 목표를 달성하는 가치")
                    .color("#F8C471")
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

        // 1. 관리와 성장
        Coin managementGrowth = coinRepository.findByName("관리와 성장").orElseThrow();
        List<Keyword> managementKeywords = Arrays.asList(
                Keyword.builder().coin(managementGrowth).name("끈기").description("목표를 향해 포기하지 않고 꾸준히 나아가는 힘").build(),
                Keyword.builder().coin(managementGrowth).name("인내심").description("어려움이나 불편함을 참고 견디는 태도").build(),
                Keyword.builder().coin(managementGrowth).name("성실함").description("맡은 일에 꾸준히 최선을 다하는 태도").build(),
                Keyword.builder().coin(managementGrowth).name("절제력").description("욕구나 충동을 이성적으로 통제하는 힘").build(),
                Keyword.builder().coin(managementGrowth).name("침착함").description("감정이나 상황에 흔들리지 않고 차분히 대응하는 태도").build(),
                Keyword.builder().coin(managementGrowth).name("학습력").description("새로운 지식이나 경험을 빠르게 이해하고 익히는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("성찰력").description("자신을 되돌아보고 의미를 찾는 내면적 사고 태도").build(),
                Keyword.builder().coin(managementGrowth).name("적응력").description("새로운 변화에 빠르고 유연하게 적응하는 능력").build(),
                Keyword.builder().coin(managementGrowth).name("수용성").description("피드백이나 의견을 열린 태도로 받아들이는 자세").build()
        );

        // 2. 감정과 태도
        Coin emotionAttitude = coinRepository.findByName("감정과 태도").orElseThrow();
        List<Keyword> emotionKeywords = Arrays.asList(
                Keyword.builder().coin(emotionAttitude).name("유머 감각").description("주변을 웃게 만드는 센스").build(),
                Keyword.builder().coin(emotionAttitude).name("감수성").description("섬세한 감정과 풍부한 감성으로 세상을 바라보는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("표현력").description("감정이나 생각을 솔직하고 풍부하게 표현하는 능력").build(),
                Keyword.builder().coin(emotionAttitude).name("밝은 에너지").description("주변까지 환하게 만드는 활기찬 에너지").build(),
                Keyword.builder().coin(emotionAttitude).name("긍정성").description("상황을 긍정적으로 받아들이는 태도").build(),
                Keyword.builder().coin(emotionAttitude).name("열정").description("무언가에 강한 의욕과 에너지를 갖고 임하는 태도").build()
        );

        // 3. 창의와 몰입
        Coin creativityFlow = coinRepository.findByName("창의와 몰입").orElseThrow();
        List<Keyword> creativityKeywords = Arrays.asList(
                Keyword.builder().coin(creativityFlow).name("호기심").description("새로운 것에 관심을 갖고 반응하는 태도").build(),
                Keyword.builder().coin(creativityFlow).name("탐구력").description("알고자 하는 대상을 깊게 연구하고 파고드는 태도").build(),
                Keyword.builder().coin(creativityFlow).name("창의력").description("기존 틀을 넘어 새로운 아이디어를 떠올리는 능력").build(),
                Keyword.builder().coin(creativityFlow).name("집중력").description("하나의 일에 주의를 모아 지속하는 태도").build(),
                Keyword.builder().coin(creativityFlow).name("몰입력").description("하나의 일에 깊이 빠져 몰두하는 태도").build(),
                Keyword.builder().coin(creativityFlow).name("기획력").description("아이디어를 구체적으로 구조화하는 능력").build()
        );

        // 4. 사고와 해결
        Coin thinkingSolving = coinRepository.findByName("사고와 해결").orElseThrow();
        List<Keyword> thinkingKeywords = Arrays.asList(
                Keyword.builder().coin(thinkingSolving).name("판단력").description("주어진 조건에서 가장 적절한 결정을 내리는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("논리력").description("생각의 근거를 정리하고, 조리 있게 전개하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("분석력").description("자료나 현상을 논리적으로 파악하고 해석하는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("통찰력").description("본질을 꿰뚫어보고 전체를 이해하는 사고력").build(),
                Keyword.builder().coin(thinkingSolving).name("신중성").description("충동보다 깊은 사고로 판단하는 태도").build(),
                Keyword.builder().coin(thinkingSolving).name("문제해결력").description("문제를 분석하고 구조적으로 해결해나가는 능력").build(),
                Keyword.builder().coin(thinkingSolving).name("융통성").description("상황에 맞게 사고와 행동을 유연하게 조절하는 능력").build()
        );

        // 5. 관계와 공감
        Coin relationshipEmpathy = coinRepository.findByName("관계와 공감").orElseThrow();
        List<Keyword> relationshipKeywords = Arrays.asList(
                Keyword.builder().coin(relationshipEmpathy).name("공감력").description("타인의 감정을 이해하고 반응하는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("배려심").description("상대의 입장을 생각하고 배려하는 마음").build(),
                Keyword.builder().coin(relationshipEmpathy).name("포용력").description("다양성을 인정하고 수용하는 태도").build(),
                Keyword.builder().coin(relationshipEmpathy).name("경청 태도").description("진심으로 귀 기울여 듣는 자세").build(),
                Keyword.builder().coin(relationshipEmpathy).name("친화력").description("자연스럽게 어울리고 편안한 관계를 만드는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("지지력").description("타인을 흔들림 없이 믿고 응원하는 마음의 힘").build(),
                Keyword.builder().coin(relationshipEmpathy).name("온화함").description("따뜻한 태도로 주변 사람에게 안정감을 주는 성향").build(),
                Keyword.builder().coin(relationshipEmpathy).name("중재력").description("갈등을 균형 있게 조율하고 해결로 이끄는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("조율력").description("다양한 의견이나 입장을 균형 있게 조화시키는 능력").build(),
                Keyword.builder().coin(relationshipEmpathy).name("겸손함").description("자기를 과시하지 않고 타인을 존중하며 소통하는 태도").build(),
                Keyword.builder().coin(relationshipEmpathy).name("예의 바름").description("예절과 배려를 지키며 상대방을 존중하는 태도").build()
        );

        // 6. 신념과 실행
        Coin beliefExecution = coinRepository.findByName("신념과 실행").orElseThrow();
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
                Keyword.builder().coin(beliefExecution).name("책임감").description("맡은 일이나 역할을 끝까지 해내려는 태도와 의지").build(),
                Keyword.builder().coin(beliefExecution).name("계획성").description("목표 달성을 위해 체계적으로 준비하는 능력").build(),
                Keyword.builder().coin(beliefExecution).name("도전력").description("새로운 가능성에 적극적으로 뛰어드는 태도").build()
        );

        // 모든 키워드 저장
        keywordRepository.saveAll(managementKeywords);
        keywordRepository.saveAll(emotionKeywords);
        keywordRepository.saveAll(creativityKeywords);
        keywordRepository.saveAll(thinkingKeywords);
        keywordRepository.saveAll(relationshipKeywords);
        keywordRepository.saveAll(beliefKeywords);

        int totalKeywords = managementKeywords.size() + emotionKeywords.size() + creativityKeywords.size() 
                         + thinkingKeywords.size() + relationshipKeywords.size() + beliefKeywords.size();
        
        log.info("Initialized {} keywords for all 6 coins", totalKeywords);
        log.info("관리와 성장: {} keywords", managementKeywords.size());
        log.info("감정과 태도: {} keywords", emotionKeywords.size());
        log.info("창의와 몰입: {} keywords", creativityKeywords.size());
        log.info("사고와 해결: {} keywords", thinkingKeywords.size());
        log.info("관계와 공감: {} keywords", relationshipKeywords.size());
        log.info("신념과 실행: {} keywords", beliefKeywords.size());
    }

    private void initializeForms() {
        if (formRepository.count() > 0) {
            log.info("Forms already initialized");
            return;
        }

        // 오늘의 일기 폼
        Form todayDiaryForm = Form.builder()
                .title("오늘의 일기")
                .description("오늘 하루를 돌아보며 나의 장점을 발견해보세요")
                .type(FormType.TODAY_DIARY)
                .build();

        // 경험 돌아보기 폼
        Form experienceForm = Form.builder()
                .title("경험 돌아보기")
                .description("과거의 경험을 통해 나의 강점을 찾아보세요")
                .type(FormType.EXPERIENCE_REFLECTION)
                .build();

        // 친구의 장점 찾아주기 폼
        Form friendStrengthForm = Form.builder()
                .title("친구의 장점 찾아주기")
                .description("친구의 장점을 발견하고 공유해주세요")
                .type(FormType.FRIEND_STRENGTH)
                .build();

        formRepository.saveAll(Arrays.asList(todayDiaryForm, experienceForm, friendStrengthForm));
        log.info("Initialized 3 forms");

        // 폼별 질문 초기화
        initializeQuestions(todayDiaryForm, experienceForm, friendStrengthForm);
    }

    private void initializeQuestions(Form todayDiaryForm, Form experienceForm, Form friendStrengthForm) {
        // 오늘의 일기 질문들
        List<Question> todayDiaryQuestions = Arrays.asList(
                Question.builder().form(todayDiaryForm).content("오늘 가장 잘한 일은 무엇인가요?").orderIndex(1).build(),
                Question.builder().form(todayDiaryForm).content("어떤 순간에 나의 장점이 드러났나요?").orderIndex(2).build(),
                Question.builder().form(todayDiaryForm).content("오늘 느낀 긍정적인 감정은 무엇인가요?").orderIndex(3).build()
        );

        // 경험 돌아보기 질문들
        List<Question> experienceQuestions = Arrays.asList(
                Question.builder().form(experienceForm).content("기억에 남는 성공 경험을 공유해주세요").orderIndex(1).build(),
                Question.builder().form(experienceForm).content("그 경험에서 어떤 강점을 발휘했나요?").orderIndex(2).build(),
                Question.builder().form(experienceForm).content("그 강점을 어떻게 더 발전시킬 수 있을까요?").orderIndex(3).build()
        );

        // 친구의 장점 찾아주기 질문들
        List<Question> friendQuestions = Arrays.asList(
                Question.builder().form(friendStrengthForm).content("친구의 어떤 점이 가장 인상적인가요?").orderIndex(1).build(),
                Question.builder().form(friendStrengthForm).content("친구가 가진 특별한 능력은 무엇인가요?").orderIndex(2).build(),
                Question.builder().form(friendStrengthForm).content("친구에게 어떤 응원의 메시지를 전하고 싶나요?").orderIndex(3).build()
        );

        questionRepository.saveAll(todayDiaryQuestions);
        questionRepository.saveAll(experienceQuestions);
        questionRepository.saveAll(friendQuestions);

        log.info("Initialized questions for all forms");
    }
} 