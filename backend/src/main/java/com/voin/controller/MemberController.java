package com.voin.controller;

import com.voin.entity.Member;
import com.voin.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 조회", description = "회원 ID로 회원 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/{memberId}")
    public ResponseEntity<Member> getMember(
            @Parameter(description = "회원 ID", required = true) @PathVariable UUID memberId) {
        Member member = memberService.findById(memberId);
        return ResponseEntity.ok(member);
    }

    @Operation(summary = "카카오 ID로 회원 조회", description = "카카오 ID로 회원 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/kakao/{kakaoId}")
    public ResponseEntity<Member> getMemberByKakaoId(
            @Parameter(description = "카카오 ID", required = true) @PathVariable String kakaoId) {
        Optional<Member> member = memberService.findByKakaoId(kakaoId);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/friend-code/{friendCode}")
    public ResponseEntity<Member> getMemberByFriendCode(@PathVariable String friendCode) {
        Optional<Member> member = memberService.findByFriendCode(friendCode);
        return member.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Member> createMember(
            @Parameter(description = "회원 정보", required = true) @RequestBody Member member) {
        Member createdMember = memberService.createMember(member);
        return ResponseEntity.ok(createdMember);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<Member> updateMember(
            @PathVariable UUID memberId,
            @RequestBody Member member) {
        Member updatedMember = memberService.updateMember(memberId, member);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/kakao/{kakaoId}")
    public ResponseEntity<Boolean> existsByKakaoId(@PathVariable String kakaoId) {
        boolean exists = memberService.existsByKakaoId(kakaoId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/friend-code/{friendCode}")
    public ResponseEntity<Boolean> existsByFriendCode(@PathVariable String friendCode) {
        boolean exists = memberService.existsByFriendCode(friendCode);
        return ResponseEntity.ok(exists);
    }
} 