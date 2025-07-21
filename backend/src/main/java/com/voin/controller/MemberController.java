package com.voin.controller;

import com.voin.dto.request.MemberUpdateRequest;
import com.voin.dto.request.NicknameUpdateRequest;
import com.voin.dto.request.ProfileImageUpdateRequest;
import com.voin.dto.response.ApiResponse;
import com.voin.dto.response.MemberResponse;
import com.voin.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "👤 Member", description = "회원 정보 관리")
public class MemberController {

    private final MemberService memberService;

    /**
     * 내 정보 조회
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> getMyInfo() {
        log.info("Getting current member info");
        MemberResponse member = memberService.getMyInfo();
        return ResponseEntity.ok(ApiResponse.success("내 정보를 조회했습니다.", member));
    }

    /**
     * 내 정보 수정
     */
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyInfo(
            @Valid @RequestBody MemberUpdateRequest request) {
        log.info("Updating current member info");
        MemberResponse member = memberService.updateMyInfo(request);
        return ResponseEntity.ok(ApiResponse.success("내 정보가 수정되었습니다.", member));
    }

    /**
     * 회원 검색 (닉네임)
     */
    @Operation(summary = "회원 검색", description = "닉네임으로 회원을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<MemberResponse>>> searchMembers(
            @Parameter(description = "검색할 닉네임") @RequestParam String nickname) {
        log.info("Searching members by nickname: {}", nickname);
        List<MemberResponse> members = memberService.searchByNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success("회원 검색이 완료되었습니다.", members));
    }

    /**
     * 친구 코드로 회원 찾기
     */
    @Operation(summary = "친구 코드로 회원 찾기", description = "친구 코드로 특정 회원을 찾습니다.")
    @GetMapping("/by-friend-code")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberByFriendCode(
            @Parameter(description = "친구 코드") @RequestParam String friendCode) {
        log.info("Getting member by friend code: {}", friendCode);
        MemberResponse member = memberService.getMemberByFriendCode(friendCode);
        return ResponseEntity.ok(ApiResponse.success("회원을 찾았습니다.", member));
    }

    /**
     * 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount() {
        log.info("Deleting current member account");
        memberService.deleteMyAccount();
        return ResponseEntity.ok(ApiResponse.<Void>success("회원 탈퇴가 완료되었습니다.", null));
    }

    /**
     * 닉네임만 변경
     */
    @Operation(summary = "닉네임 변경", description = "현재 로그인한 사용자의 닉네임만 변경합니다.")
    @PutMapping("/me/nickname")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyNickname(
            @Valid @RequestBody NicknameUpdateRequest request) {
        log.info("Updating current member nickname to: {}", request.getNickname());
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .nickname(request.getNickname())
                .build();
        MemberResponse member = memberService.updateMyInfo(updateRequest);
        return ResponseEntity.ok(ApiResponse.success("닉네임이 변경되었습니다.", member));
    }

    /**
     * 프로필 이미지만 변경
     */
    @Operation(summary = "프로필 이미지 변경", description = "현재 로그인한 사용자의 프로필 이미지만 변경합니다.")
    @PutMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMyProfileImage(
            @Valid @RequestBody ProfileImageUpdateRequest request) {
        log.info("Updating current member profile image to: {}", request.getProfileImage());
        MemberUpdateRequest updateRequest = MemberUpdateRequest.builder()
                .profileImage(request.getProfileImage())
                .build();
        MemberResponse member = memberService.updateMyInfo(updateRequest);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 변경되었습니다.", member));
    }

    /**
     * 내 통계 정보 조회
     */
    @Operation(summary = "내 통계 조회", description = "내 카드 수, 친구 수 등 통계 정보를 조회합니다.")
    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<Object>> getMyStats() {
        log.info("Getting current member statistics");
        Object stats = memberService.getMyStats();
        return ResponseEntity.ok(ApiResponse.success("내 통계를 조회했습니다.", stats));
    }
} 