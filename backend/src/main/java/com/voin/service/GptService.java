package com.voin.service;

import com.voin.config.GptConfig;
import com.voin.dto.common.GptMessage;
import com.voin.dto.request.GptRequest;
import com.voin.dto.response.GptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    private final GptConfig gptConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public String classifyValue(String userInput) {
        String url = "https://api.openai.com/v1/chat/completions";

        GptRequest request = new GptRequest();
        request.setModel(gptConfig.getModel());
        request.setMessages(List.of(
                new GptMessage("system", "너는 입력된 문단에서 장점을 도출하고 분류하는 AI야. " +
                        "다음의 6개의 카테고리 안에 있는 53개의 키워드 중 입력된 문단에서 가장 돋보이는 장점을 키워드 단 하나만 선택해서 그 카테고리와 키워드를 카테고리, 키워드 형식으로 대답해줘. " +
                        "카테고리와 키워드는 다음과 같아 \n" +
                        "“관리와 성장” : (끈기, 인내심, 성실함, 절제력, 침착함, 학습력, 성찰력, 적응력, 수용성)\n" +
                        "“감정과 태도” : (유머 감각, 감수성, 표현력, 밝은 에너지, 긍정성, 열정)\n" +
                        "“창의와 몰입” : (호기심, 탐구력, 창의력, 집중력, 몰입력, 기획력)\n" +
                        "“사고와 해결” : (판단력, 논리력, 분석력, 통찰력, 신중성, 문제해결력, 융통성)\n" +
                        "“관계와 공감” : (공감력, 배려심, 포용력, 경청 태도, 친화력, 지지력, 온화함, 중재력, 조율력, 겸손함, 예의 바름)\n" +
                        "“신념과 실행” : (신념, 주체성, 정직함, 정의감, 도덕심, 용기, 결단력, 주도성, 실행력, 리더십, 공정성, 책임감, 계획성, 도전력)"),
                new GptMessage("user", userInput)
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(gptConfig.getSecretKey());

        HttpEntity<GptRequest> httpRequest = new HttpEntity<>(request, headers);

        ResponseEntity<GptResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpRequest,
                GptResponse.class
        );

        var choices = response.getBody().getChoices();
        if (choices != null && !choices.isEmpty()) {
            var message = (Map<String, Object>) choices.get(0).get("message");
            return message.get("content").toString().trim();
        }

        return "응답이 없습니다 😥";
    }
}
