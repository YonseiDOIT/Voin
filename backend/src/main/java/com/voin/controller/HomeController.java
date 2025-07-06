package com.voin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 🏠 홈 페이지 컨트롤러
 * 
 * 이 클래스는 웹사이트의 메인 페이지들을 관리합니다.
 * 사용자가 "/", "/cards" 같은 주소로 접속했을 때 어떤 페이지를 보여줄지 결정해요.
 * 
 * 쉽게 말해서, 이 클래스는 "웹사이트의 길잡이" 역할을 합니다!
 */
@Controller
@Tag(name = "홈 API", description = "메인 페이지 관련 API")
public class HomeController {

    /**
     * 🏠 메인 홈페이지 보여주기
     * 
     * 사용자가 웹사이트 주소(/)로 들어왔을 때 첫 번째로 보는 페이지입니다.
     * 여러 버튼들이 있어서 원하는 기능을 선택할 수 있어요.
     */
    @Operation(summary = "홈 페이지", description = "메인 홈 페이지를 반환합니다.")
    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html 파일을 보여줍니다
    }

    /**
     * 🪙 나의 코인 목록 페이지 보여주기
     * 
     * 사용자가 만든 모든 코인(카드)들을 목록으로 보여주는 페이지입니다.
     * 각 코인을 클릭하면 상세 내용을 볼 수 있어요.
     */
    @Operation(summary = "카드 목록 페이지", description = "생성된 카드 목록 페이지를 반환합니다.")
    @GetMapping("/cards")
    public String cardListPage() {
        return "card-list"; // templates/card-list.html 파일을 보여줍니다
    }

    /**
     * 📖 특정 코인의 상세 정보 페이지 보여주기
     * 
     * 사용자가 코인 목록에서 하나의 코인을 클릭했을 때 보는 페이지입니다.
     * 그 코인에 담긴 자세한 이야기와 정보를 볼 수 있어요.
     * 
     * @param cardId 보고 싶은 코인의 ID 번호
     * @param model 페이지에 데이터를 전달하는 도구
     */
    @Operation(summary = "카드 상세 페이지", description = "개별 카드의 상세 정보를 보여주는 페이지를 반환합니다.")
    @GetMapping("/cards/{cardId}")
    public String cardDetailPage(@PathVariable Long cardId, Model model) {
        model.addAttribute("cardId", cardId); // 페이지에 코인 ID를 전달합니다
        return "card-detail"; // templates/card-detail.html 파일을 보여줍니다
    }

    /**
     * ✨ 새로운 코인 만들기 페이지로 이동
     * 
     * 사용자가 새로운 코인을 만들고 싶을 때 사용하는 페이지입니다.
     * 카카오 로그인부터 코인 생성까지 전체 과정을 안내해줘요.
     */
    @Operation(summary = "카드 생성 플로우 페이지", description = "카드 생성을 위한 전체 플로우 테스트 페이지를 반환합니다.")
    @GetMapping("/create-card")
    public String createCardFlowPage() {
        return "redirect:/flow-test.html"; // 다른 페이지로 이동시킵니다
    }

    /**
     * 🔍 서버 상태 확인하기
     * 
     * 우리 웹사이트가 정상적으로 작동하고 있는지 확인하는 기능입니다.
     * 개발자나 관리자가 문제가 없는지 체크할 때 사용해요.
     */
    @Operation(summary = "상태 체크", description = "서버 상태를 확인합니다.")
    @SecurityRequirements
    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK"); // "OK"라고 응답하면 정상이라는 뜻입니다
    }
}