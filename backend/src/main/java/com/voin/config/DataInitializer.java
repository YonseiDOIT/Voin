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
                Coin.builder().name("관리와 성장").description("체계적인 관리와 지속적인 성장을 추구하는 가치").build(),
                Coin.builder().name("감정과 태도").description("긍정적인 감정과 건설적인 태도를 유지하는 가치").build(),
                Coin.builder().name("창의와 몰입").description("창의적 사고와 깊은 몰입을 통한 혁신의 가치").build(),
                Coin.builder().name("사고와 해결").description("논리적 사고와 문제 해결 능력의 가치").build(),
                Coin.builder().name("관계와 공감").description("타인과의 관계 형성과 공감 능력의 가치").build(),
                Coin.builder().name("신념과 실행").description("확고한 신념과 실행력을 바탕으로 한 가치").build()
        );

        coinRepository.saveAll(coins);
        log.info("Initialized {} coins", coins.size());
    }

    private void initializeKeywords() {
        if (keywordRepository.count() > 0) {
            log.info("Keywords already initialized");
            return;
        }

        // 관리와 성장
        Coin managementGrowth = coinRepository.findByName("관리와 성장").orElseThrow();
        List<Keyword> managementKeywords = Arrays.asList(
                Keyword.builder().coin(managementGrowth).name("체계적인").build(),
                Keyword.builder().coin(managementGrowth).name("계획적인").build(),
                Keyword.builder().coin(managementGrowth).name("성장하는").build(),
                Keyword.builder().coin(managementGrowth).name("발전하는").build(),
                Keyword.builder().coin(managementGrowth).name("학습하는").build()
        );

        // 감정과 태도
        Coin emotionAttitude = coinRepository.findByName("감정과 태도").orElseThrow();
        List<Keyword> emotionKeywords = Arrays.asList(
                Keyword.builder().coin(emotionAttitude).name("긍정적인").build(),
                Keyword.builder().coin(emotionAttitude).name("낙관적인").build(),
                Keyword.builder().coin(emotionAttitude).name("차분한").build(),
                Keyword.builder().coin(emotionAttitude).name("안정적인").build(),
                Keyword.builder().coin(emotionAttitude).name("균형잡힌").build()
        );

        // 창의와 몰입
        Coin creativityFlow = coinRepository.findByName("창의와 몰입").orElseThrow();
        List<Keyword> creativityKeywords = Arrays.asList(
                Keyword.builder().coin(creativityFlow).name("창의적인").build(),
                Keyword.builder().coin(creativityFlow).name("혁신적인").build(),
                Keyword.builder().coin(creativityFlow).name("집중하는").build(),
                Keyword.builder().coin(creativityFlow).name("몰입하는").build(),
                Keyword.builder().coin(creativityFlow).name("독창적인").build()
        );

        // 사고와 해결
        Coin thinkingSolving = coinRepository.findByName("사고와 해결").orElseThrow();
        List<Keyword> thinkingKeywords = Arrays.asList(
                Keyword.builder().coin(thinkingSolving).name("논리적인").build(),
                Keyword.builder().coin(thinkingSolving).name("분석적인").build(),
                Keyword.builder().coin(thinkingSolving).name("해결하는").build(),
                Keyword.builder().coin(thinkingSolving).name("효율적인").build(),
                Keyword.builder().coin(thinkingSolving).name("전략적인").build()
        );

        // 관계와 공감
        Coin relationshipEmpathy = coinRepository.findByName("관계와 공감").orElseThrow();
        List<Keyword> relationshipKeywords = Arrays.asList(
                Keyword.builder().coin(relationshipEmpathy).name("공감하는").build(),
                Keyword.builder().coin(relationshipEmpathy).name("배려하는").build(),
                Keyword.builder().coin(relationshipEmpathy).name("소통하는").build(),
                Keyword.builder().coin(relationshipEmpathy).name("협력하는").build(),
                Keyword.builder().coin(relationshipEmpathy).name("이해하는").build()
        );

        // 신념과 실행
        Coin beliefExecution = coinRepository.findByName("신념과 실행").orElseThrow();
        List<Keyword> beliefKeywords = Arrays.asList(
                Keyword.builder().coin(beliefExecution).name("신념있는").build(),
                Keyword.builder().coin(beliefExecution).name("실행하는").build(),
                Keyword.builder().coin(beliefExecution).name("추진하는").build(),
                Keyword.builder().coin(beliefExecution).name("도전하는").build(),
                Keyword.builder().coin(beliefExecution).name("용기있는").build()
        );

        keywordRepository.saveAll(managementKeywords);
        keywordRepository.saveAll(emotionKeywords);
        keywordRepository.saveAll(creativityKeywords);
        keywordRepository.saveAll(thinkingKeywords);
        keywordRepository.saveAll(relationshipKeywords);
        keywordRepository.saveAll(beliefKeywords);

        log.info("Initialized keywords for all coins");
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