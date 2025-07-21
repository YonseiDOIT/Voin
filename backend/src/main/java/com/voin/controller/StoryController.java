package com.voin.controller;

import com.voin.dto.request.StoryCreateRequest;
import com.voin.dto.request.StoryUpdateRequest;
import com.voin.dto.response.ApiResponse;
import com.voin.dto.response.StoryResponse;
import com.voin.service.StoryService;
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
@RequestMapping("/api/stories")
@RequiredArgsConstructor
@Tag(name = "📖 Story", description = "스토리 생성 및 관리 (일기, 사례 돌아보기)")
public class StoryController {

    private final StoryService storyService;

    /**
     * 새로운 스토리 생성 (일기 또는 사례 돌아보기)
     */
    @Operation(summary = "스토리 생성", description = "새로운 스토리를 생성합니다. (일기 또는 사례 돌아보기)")
    @PostMapping
    public ResponseEntity<ApiResponse<StoryResponse>> createStory(
            @Valid @RequestBody StoryCreateRequest request) {
        log.info("Creating new story: type={}", request.getType());
        StoryResponse story = storyService.createStory(request);
        return ResponseEntity.ok(ApiResponse.success("스토리가 생성되었습니다.", story));
    }

    /**
     * 스토리 내용 업데이트 (2단계 사례 돌아보기용)
     */
    @Operation(summary = "스토리 업데이트", description = "기존 스토리에 추가 내용을 업데이트합니다.")
    @PutMapping("/{storyId}")
    public ResponseEntity<ApiResponse<StoryResponse>> updateStory(
            @Parameter(description = "스토리 ID") @PathVariable Long storyId,
            @Valid @RequestBody StoryUpdateRequest request) {
        log.info("Updating story: id={}", storyId);
        StoryResponse story = storyService.updateStory(storyId, request);
        return ResponseEntity.ok(ApiResponse.success("스토리가 업데이트되었습니다.", story));
    }

    /**
     * 특정 스토리 조회
     */
    @Operation(summary = "스토리 조회", description = "스토리 ID로 특정 스토리를 조회합니다.")
    @GetMapping("/{storyId}")
    public ResponseEntity<ApiResponse<StoryResponse>> getStory(
            @Parameter(description = "스토리 ID") @PathVariable Long storyId) {
        log.info("Getting story: id={}", storyId);
        StoryResponse story = storyService.getStory(storyId);
        return ResponseEntity.ok(ApiResponse.success("스토리를 조회했습니다.", story));
    }

    /**
     * 내 스토리 목록 조회
     */
    @Operation(summary = "내 스토리 목록", description = "현재 사용자의 모든 스토리를 조회합니다.")
    @GetMapping("/my-stories")
    public ResponseEntity<ApiResponse<List<StoryResponse>>> getMyStories() {
        log.info("Getting my stories");
        List<StoryResponse> stories = storyService.getMyStories();
        return ResponseEntity.ok(ApiResponse.success("내 스토리 목록을 조회했습니다.", stories));
    }

    /**
     * 스토리 삭제
     */
    @Operation(summary = "스토리 삭제", description = "특정 스토리를 삭제합니다.")
    @DeleteMapping("/{storyId}")
    public ResponseEntity<ApiResponse<Void>> deleteStory(
            @Parameter(description = "스토리 ID") @PathVariable Long storyId) {
        log.info("Deleting story: id={}", storyId);
        storyService.deleteStory(storyId);
        return ResponseEntity.ok(ApiResponse.<Void>success("스토리가 삭제되었습니다.", null));
    }
} 