package com.voin.service;

import com.voin.config.GptConfig;
import com.voin.dto.common.GptMessage;
import com.voin.dto.request.GptRequest;
import com.voin.dto.response.GptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

    private final GptConfig gptConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> classifyValue(String userInput) {
        String url = "https://api.openai.com/v1/chat/completions";

        GptRequest request = new GptRequest();
        request.setModel(gptConfig.getModel());
        request.setMessages(List.of(
                new GptMessage("system", """
                        너는 사용자가 작성한 다양한 유형의 글(일상 기록, 자신의 사례 회고, 친구의 사례 회고)에서 장점 카테고리와 키워드를 정확하게 분류하고, 50~60자 분량의 존댓말로 자연스럽고 풍부하게 요약하는 AI야. 반드시 아래 절차와 정의를 따르며, 입력이 동일하면 항상 동일한 결과를 내야 해.

                        ### 절차
                        1. 사용자의 문단에서 가장 중심이 되는 가치, 신념, 행동의 동기를 파악합니다.
                        2. 아래 '장점 카테고리와 키워드 정의' 중 가장 적절한 카테고리 1개, 키워드 1개를 선택합니다.
                        3. 반드시 정의된 목록 내에서만 선택하고, 정의와 일치하는지 검토합니다.
                        4. 선택 후, 사용자의 문단을 50~60자 사이의 분량으로, 자연스럽고 풍부하게 요약합니다.
                        - 반드시 50자 미만이 되지 않도록 하며, 가능하면 60자에 가깝게 작성합니다.
                        - 사용자의 행동, 감정, 결과가 함께 드러나도록 충분한 정보를 담아야 합니다.
                        - 불필요하게 간략하게 줄이지 말고, 사용자의 가치와 변화를 드러내는 한 문장으로 작성하세요.

                        ### 입력 유형 안내
                        - 사용자의 입력은 자유로운 일상 기록, 과거 사례 회고, 친구의 사례 회고일 수 있습니다.
                        - 사례 회고의 경우 '상황 맥락', '행동', '그 행동에 대한 생각'의 순서로 작성됩니다.
                        - 나누어진 입력을 종합적으로 해석해 가장 중심이 되는 가치와 신념을 도출하세요.

                        ### 키워드 선택의 원칙
                        - 여러 키워드가 유사할 경우, 가장 중심적이고 본질적인 가치, 행동, 동기를 기준으로 1개만 선택합니다.
                        - 유사한 키워드라도 정의에 가장 정확히 부합하는 키워드만 선택하며, 억지 해석을 하지 않습니다.
                        - 입력이 동일한 경우 반드시 동일한 카테고리와 키워드, 요약 결과를 출력합니다.
                        - 판단이 애매한 경우, 가장 두드러진 가치나 행동의 성격을 기준으로 판단합니다.

                        ### 요약 작성 지침
                        - 반드시 존댓말("~했어요") 어미를 사용합니다.
                        - '~셨어요'와 같은 높임 표현은 사용하지 말고, '~했어요' 형식으로만 작성합니다.
                        - 감탄형 표현(예: 멋져요, 대단해요 등)은 사용하지 않습니다.
                        - 사용자의 행동과 태도를 긍정적으로 조명하되, 평가하지 않고 사실 중심으로 따뜻하게 표현합니다.
                        - 부정적 어휘는 사용하지 말고, 긍정적인 의미, 노력, 성찰, 변화로 해석하여 표현합니다.
                        - 요약의 마무리는 긍정적 결과나 변화가 있었음을 자연스럽게 담아야 합니다.
                        - 설명체, 나열형은 피하고, 따뜻하고 공감할 수 있는 UX Writing 스타일로 작성합니다.

                        ### 출력 형식
                        장점 카테고리: [카테고리명]
                        키워드: [키워드명]
                        요약 내용: [요약]

                        ### 장점 카테고리와 키워드 정의

                        관리와 성장
                        - 끈기: 목표를 향해 포기하지 않고 꾸준히 나아가는 힘
                        - 인내심: 어려움이나 불편함을 참고 견디는 태도
                        - 성실함: 맡은 일에 꾸준히 최선을 다하는 태도
                        - 절제력: 욕구나 충동을 이성적으로 통제하는 힘
                        - 침착함: 감정이나 상황에 흔들리지 않고 차분히 대응하는 태도
                        - 학습력: 새로운 지식이나 경험을 빠르게 이해하고 익히는 능력
                        - 성찰력: 자신을 되돌아보고 의미를 찾는 내면적 사고 태도
                        - 적응력: 새로운 변화에 빠르고 유연하게 적응하는 능력
                        - 수용성: 피드백이나 의견을 열린 태도로 받아들이는 자세

                        감정과 태도
                        - 유머 감각: 주변을 웃게 만드는 센스
                        - 감수성: 섬세한 감정과 풍부한 감성으로 세상을 바라보는 능력
                        - 표현력: 감정이나 생각을 솔직하고 풍부하게 표현하는 능력
                        - 밝은 에너지: 주변까지 환하게 만드는 활기찬 에너지
                        - 긍정성: 상황을 긍정적으로 받아들이는 태도
                        - 열정: 무언가에 강한 의욕과 에너지를 갖고 임하는 태도

                        창의와 몰입
                        - 호기심: 새로운 것에 관심을 갖고 반응하는 태도
                        - 탐구력: 알고자 하는 대상을 깊게 연구하고 파고드는 태도
                        - 창의력: 기존 틀을 넘어 새로운 아이디어를 떠올리는 능력
                        - 집중력: 하나의 일에 주의를 모아 지속하는 태도
                        - 몰입력: 하나의 일에 깊이 빠져 몰두하는 태도
                        - 기획력: 아이디어를 구체적으로 구조화하는 능력

                        사고와 해결
                        - 판단력: 주어진 조건에서 가장 적절한 결정을 내리는 능력
                        - 논리력: 생각의 근거를 정리하고 조리 있게 전개하는 능력
                        - 분석력: 자료나 현상을 논리적으로 파악하고 해석하는 능력
                        - 통찰력: 본질을 꿰뚫어보고 전체를 이해하는 사고력
                        - 신중성: 충동보다 깊은 사고로 판단하는 태도
                        - 문제해결력: 문제를 분석하고 구조적으로 해결해나가는 능력
                        - 융통성: 상황에 맞게 사고와 행동을 유연하게 조절하는 능력

                        관계와 공감
                        - 공감력: 타인의 감정을 이해하고 반응하는 능력
                        - 배려심: 상대의 입장을 생각하고 배려하는 마음
                        - 포용력: 다양성을 인정하고 수용하는 태도
                        - 경청 태도: 진심으로 귀 기울여 듣는 자세
                        - 친화력: 자연스럽게 어울리고 편안한 관계를 만드는 능력
                        - 지지력: 타인을 흔들림 없이 믿고 응원하는 마음의 힘
                        - 온화함: 따뜻한 태도로 주변 사람에게 안정감을 주는 성향
                        - 중재력: 갈등을 균형 있게 조율하고 해결로 이끄는 능력
                        - 조율력: 다양한 의견이나 입장을 균형 있게 조화시키는 능력
                        - 겸손함: 자기를 과시하지 않고 타인을 존중하며 소통하는 태도
                        - 예의 바름: 예절과 배려를 지키며 상대방을 존중하는 태도

                        신념과 실행
                        - 신념: 자신의 가치와 믿음을 지키는 태도
                        - 주체성: 주변에 휘둘리지 않고, 스스로 판단하는 태도
                        - 정직함: 사실을 왜곡하지 않고, 신뢰를 지키는 태도
                        - 정의감: 옳고 그름에 민감히 반응하고, 불의에 맞서는 태도
                        - 도덕심: 사회적으로 바른 가치 기준을 지키려는 마음가짐
                        - 용기: 두려움을 이기고 행동으로 옮기는 힘
                        - 결단력: 과감하게 결정을 내리고 실행으로 옮기는 힘
                        - 주도성: 팀이나 상황을 이끌고 먼저 실행하는 힘
                        - 실행력: 계획한 일을 실제로 옮겨서 실행해 나가는 추진력
                        - 리더십: 사람들을 이끌고 방향을 제시하는 능력
                        - 공정성: 편견 없이 균형 있게 판단하고 존중하는 태도
                        - 책임감: 맡은 일이나 역할을 끝까지 해내려는 태도와 의지
                        - 계획성: 목표 달성을 위해 체계적으로 준비하는 능력
                        - 도전력: 새로운 가능성에 적극적으로 뛰어드는 태도
                        """),
                new GptMessage("user", userInput)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(gptConfig.getSecretKey());

        HttpEntity<GptRequest> httpRequest = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GptResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpRequest,
                    GptResponse.class
            );

            var choices = response.getBody().getChoices();
            if (choices != null && !choices.isEmpty()) {
                var message = (Map<String, Object>) choices.get(0).get("message");
                String content = message.get("content").toString().trim();
                
                // GPT 응답을 파싱하여 Map으로 변환
                return parseGptResponse(content);
            }
        } catch (Exception e) {
            log.error("GPT API 호출 중 오류 발생", e);
            return createErrorResponse("GPT API 호출 중 오류가 발생했습니다.");
        }

        return createErrorResponse("응답이 없습니다.");
    }

    /**
     * GPT 응답을 파싱하여 Map<String, String> 형태로 변환
     */
    private Map<String, String> parseGptResponse(String content) {
        Map<String, String> result = new HashMap<>();
        
        try {
            // 정규식을 사용하여 각 항목 추출
            Pattern categoryPattern = Pattern.compile("장점 카테고리:\\s*(.+)", Pattern.CASE_INSENSITIVE);
            Pattern keywordPattern = Pattern.compile("키워드:\\s*(.+)", Pattern.CASE_INSENSITIVE);
            Pattern summaryPattern = Pattern.compile("요약 내용:\\s*(.+)", Pattern.CASE_INSENSITIVE);
            
            Matcher categoryMatcher = categoryPattern.matcher(content);
            Matcher keywordMatcher = keywordPattern.matcher(content);
            Matcher summaryMatcher = summaryPattern.matcher(content);
            
            if (categoryMatcher.find()) {
                result.put("category", categoryMatcher.group(1).trim());
            }
            
            if (keywordMatcher.find()) {
                result.put("keyword", keywordMatcher.group(1).trim());
            }
            
            if (summaryMatcher.find()) {
                result.put("summary", summaryMatcher.group(1).trim());
            }
            
            // 파싱 실패 시 원본 텍스트도 포함
            if (result.isEmpty()) {
                // 기존 형식 "감정과 태도" : (유머 감각) 파싱 시도
                Pattern oldFormatPattern = Pattern.compile("\"(.+?)\"\\s*:\\s*\\((.+?)\\)");
                Matcher oldFormatMatcher = oldFormatPattern.matcher(content);
                
                if (oldFormatMatcher.find()) {
                    result.put("category", oldFormatMatcher.group(1).trim());
                    result.put("keyword", oldFormatMatcher.group(2).trim());
                    result.put("summary", ""); // 기존 형식에는 요약이 없음
                } else {
                    result.put("rawContent", content);
                    result.put("error", "응답 형식을 파싱할 수 없습니다.");
                }
            }
            
        } catch (Exception e) {
            log.error("GPT 응답 파싱 중 오류 발생: {}", content, e);
            result.put("rawContent", content);
            result.put("error", "파싱 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 오류 응답 생성
     */
    private Map<String, String> createErrorResponse(String errorMessage) {
        Map<String, String> result = new HashMap<>();
        result.put("error", errorMessage);
        return result;
    }
}
