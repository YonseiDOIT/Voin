package com.voin.controller;

import com.voin.dto.response.ApiResponse;
import com.voin.service.CoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coins")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CoinService.CoinWithKeywordsDto>>> getCoins() {
        return ResponseEntity.ok(ApiResponse.success(coinService.getCoinsWithKeywords()));
    }
} 