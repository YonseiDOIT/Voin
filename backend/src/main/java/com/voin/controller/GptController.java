package com.voin.controller;

import com.voin.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gpt")
public class GptController {

    private final GptService gptService;

    @PostMapping("/classify")
    public String classify(@RequestBody String userInput) {
        return gptService.classifyValue(userInput);
    }
}
