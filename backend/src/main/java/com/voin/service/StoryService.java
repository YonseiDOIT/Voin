package com.voin.service;

import com.voin.dto.request.StoryCreateRequest;
import com.voin.dto.request.StoryUpdateRequest;
import com.voin.dto.response.StoryResponse;
import com.voin.entity.Member;
import com.voin.entity.Story;
import com.voin.repository.MemberRepository;
import com.voin.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StoryService {

    private final StoryRepository storyRepository;
    private final MemberRepository memberRepository;

    /**
     * 현재 로그인한 사용자 정보 가져오기
     */
    private Member getCurrentMember() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findById(UUID.fromString(memberId))
                .orElseThrow(() -> new RuntimeException("로그인한 사용자를 찾을 수 없습니다."));
    }

    /**
     * 새로운 스토리 생성
     */
    public StoryResponse createStory(StoryCreateRequest request) {
        Member currentMember = getCurrentMember();
        
        Story story = Story.builder()
                .memberId(currentMember.getId())
                .title(request.getType() == com.voin.constant.StoryType.DAILY_DIARY ? "오늘의 일기" : "사례 돌아보기")
                .content(request.getContent())
                .storyType(request.getType())
                .answer1(request.getContent())
                .situationContext(request.getSituationContextId() != null ? 
                    "상황맥락" + request.getSituationContextId() : null)
                .build();

        Story savedStory = storyRepository.save(story);
        log.info("Story created: id={}, type={}", savedStory.getId(), savedStory.getStoryType());
        
        return convertToStoryResponse(savedStory);
    }

    /**
     * 스토리 업데이트 (두 번째 답변 추가)
     */
    public StoryResponse updateStory(Long storyId, StoryUpdateRequest request) {
        Member currentMember = getCurrentMember();
        
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다."));

        // 소유자 확인
        if (!story.getMemberId().equals(currentMember.getId())) {
            throw new RuntimeException("해당 스토리에 대한 권한이 없습니다.");
        }

        story.updateAnswer2(request.getAdditionalContent());
        Story updatedStory = storyRepository.save(story);
        
        log.info("Story updated: id={}", updatedStory.getId());
        return convertToStoryResponse(updatedStory);
    }

    /**
     * 특정 스토리 조회
     */
    @Transactional(readOnly = true)
    public StoryResponse getStory(Long storyId) {
        Member currentMember = getCurrentMember();
        
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다."));

        // 소유자 확인
        if (!story.getMemberId().equals(currentMember.getId())) {
            throw new RuntimeException("해당 스토리에 대한 권한이 없습니다.");
        }

        return convertToStoryResponse(story);
    }

    /**
     * 내 스토리 목록 조회
     */
    @Transactional(readOnly = true)
    public List<StoryResponse> getMyStories() {
        Member currentMember = getCurrentMember();
        
        List<Story> stories = storyRepository.findByMemberIdOrderByCreatedAtDesc(currentMember.getId());
        return stories.stream()
                .map(this::convertToStoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 스토리 삭제
     */
    public void deleteStory(Long storyId) {
        Member currentMember = getCurrentMember();
        
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("스토리를 찾을 수 없습니다."));

        // 소유자 확인
        if (!story.getMemberId().equals(currentMember.getId())) {
            throw new RuntimeException("해당 스토리에 대한 권한이 없습니다.");
        }

        storyRepository.delete(story);
        log.info("Story deleted: id={}", storyId);
    }

    /**
     * Story 엔티티를 StoryResponse DTO로 변환
     */
    private StoryResponse convertToStoryResponse(Story story) {
        return StoryResponse.builder()
                .id(story.getId())
                .type(story.getStoryType())
                .answer1(story.getAnswer1())
                .answer2(story.getAnswer2())
                .situationContextId(story.getSituationContext() != null ? 
                    Integer.parseInt(story.getSituationContext().replace("상황맥락", "")) : null)
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .build();
    }
} 