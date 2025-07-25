package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import com.voin.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gpt")
public class GptController {

    private final GptService gptService;

    @PostMapping("/classify")
    public ResponseEntity<ApiResponse<Map<String, String>>> classify(@RequestBody String userInput) {
        Map<String, String> result = gptService.classifyValue(userInput);
        return ResponseEntity.ok(ApiResponse.success("분류가 완료되었습니다.", result));
    }

    @PostMapping("/summarize")
    public String summarize(@RequestBody String userInput) {
        return gptService.summarizeOnly(userInput);
    }

}
