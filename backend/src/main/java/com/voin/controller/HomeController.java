package com.voin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "홈 API", description = "메인 페이지 관련 API")
public class HomeController {

    @Operation(summary = "메인 페이지", description = "VOIN 메인 페이지를 반환합니다.")
    @SecurityRequirements // 인증 불필요
    @GetMapping("/")
    public String home() {
        return "index";
    }
} 