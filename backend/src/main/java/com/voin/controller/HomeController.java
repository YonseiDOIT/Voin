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
@Tag(name = "🏠 Home", description = "홈페이지 및 정적 페이지 라우팅")
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
     * 🎯 상황 맥락 선택 페이지 보여주기
     * 
     * 사례 돌아보기 플로우의 첫 번째 단계입니다.
     * 사용자가 어떤 상황에서의 경험을 돌아볼지 선택하는 페이지예요.
     */
    @Operation(summary = "상황 맥락 선택 페이지", description = "사례 돌아보기를 위한 상황 맥락 선택 페이지를 반환합니다.")
    @GetMapping("/situation-context-selection")
    public String situationContextSelectionPage() {
        return "situation-context-selection"; // templates/situation-context-selection.html 파일을 보여줍니다
    }

    /**
     * 📝 사례 돌아보기 1단계 페이지 보여주기
     * 
     * 첫 번째 질문 "그때 나는 어떤 행동을 했었나요?"에 답하는 페이지입니다.
     * 사용자가 선택한 상황 맥락에서 했던 행동을 구체적으로 작성해요.
     */
    @Operation(summary = "사례 돌아보기 1단계 페이지", description = "사례 돌아보기 첫 번째 질문 페이지를 반환합니다.")
    @GetMapping("/experience-step1")
    public String experienceStep1Page() {
        return "experience-step1"; // templates/experience-step1.html 파일을 보여줍니다
    }

    /**
     * 💭 사례 돌아보기 2단계 페이지 보여주기
     * 
     * 두 번째 질문 "내 행동에 대해 어떻게 생각하시나요?"에 답하는 페이지입니다.
     * 1단계에서 작성한 행동에 대한 생각이나 느낌을 자유롭게 작성해요.
     */
    @Operation(summary = "사례 돌아보기 2단계 페이지", description = "사례 돌아보기 두 번째 질문 페이지를 반환합니다.")
    @GetMapping("/experience-step2")
    public String experienceStep2Page() {
        return "experience-step2"; // templates/experience-step2.html 파일을 보여줍니다
    }

    /**
     * 📝 오늘의 일기 작성 페이지
     * 
     * 사용자가 오늘 있었던 일을 기록하고 그 경험에서 장점을 찾는 페이지입니다.
     * 일기를 작성한 후 장점 선택 단계로 이동할 수 있어요.
     */
    @Operation(summary = "오늘의 일기 작성 페이지", description = "오늘의 일기를 작성하여 코인을 생성하는 페이지를 반환합니다.")
    @GetMapping("/diary")
    public String diaryPage() {
        return "diary"; // templates/diary.html 파일을 보여줍니다
    }

    /**
     * ⭐ 장점 선택 페이지 (공통)
     * 
     * 모든 코인 생성 플로우에서 공통으로 사용하는 장점 선택 페이지입니다.
     * 오늘의 일기, 사례 돌아보기 등에서 완료된 Form을 바탕으로 코인과 키워드를 선택해요.
     */
    @Operation(summary = "장점 선택 페이지", description = "코인과 키워드를 선택하여 카드를 생성하는 공통 페이지를 반환합니다.")
    @GetMapping("/strength-selection")
    public String strengthSelectionPage() {
        return "strength-selection"; // templates/strength-selection.html 파일을 보여줍니다
    }

    /**
     * 🎯 사례 돌아보기 시작 페이지
     * 
     * 사례 돌아보기 플로우의 시작점입니다.
     * 상황 맥락 선택 페이지로 리다이렉트합니다.
     */
    @Operation(summary = "사례 돌아보기 시작", description = "사례 돌아보기 플로우를 시작합니다.")
    @GetMapping("/experience/select-context")
    public String experienceSelectContext() {
        return "redirect:/situation-context-selection"; // 기존 페이지로 리다이렉트
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