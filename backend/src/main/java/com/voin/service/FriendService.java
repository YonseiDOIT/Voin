package com.voin.service;

import com.voin.constant.FriendStatus;
import com.voin.dto.response.FriendRequestResponse;
import com.voin.dto.response.FriendCardResponse;
import com.voin.entity.Friend;
import com.voin.entity.Member;
import com.voin.entity.Card;
import com.voin.repository.FriendRepository;
import com.voin.repository.MemberRepository;
import com.voin.repository.CardRepository;
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
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;

    /**
     * 현재 로그인한 사용자 정보 가져오기
     */
    private Member getCurrentMember() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findById(UUID.fromString(memberId))
                .orElseThrow(() -> new RuntimeException("로그인한 사용자를 찾을 수 없습니다."));
    }

    /**
     * 친구 요청 보내기
     */
    public FriendRequestResponse sendFriendRequest(String friendCode) {
        Member currentMember = getCurrentMember();
        Member targetMember = memberRepository.findByFriendCode(friendCode)
                .orElseThrow(() -> new RuntimeException("해당 친구 코드를 가진 사용자를 찾을 수 없습니다."));

        // 자기 자신에게 요청하는 경우 체크
        if (currentMember.getId().equals(targetMember.getId())) {
            throw new RuntimeException("자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 친구인 경우 체크
        if (friendRepository.existsByFromMemberAndToMemberAndStatus(
                currentMember, targetMember, FriendStatus.ACCEPTED) ||
            friendRepository.existsByFromMemberAndToMemberAndStatus(
                targetMember, currentMember, FriendStatus.ACCEPTED)) {
            throw new RuntimeException("이미 친구 관계입니다.");
        }

        // 이미 요청을 보낸 경우 체크
        if (friendRepository.existsByFromMemberAndToMemberAndStatus(
                currentMember, targetMember, FriendStatus.PENDING)) {
            throw new RuntimeException("이미 친구 요청을 보냈습니다.");
        }

        Friend friendRequest = Friend.builder()
                .fromMember(currentMember)
                .toMember(targetMember)
                .status(FriendStatus.PENDING)
                .build();

        Friend savedRequest = friendRepository.save(friendRequest);
        return convertToFriendRequestResponse(savedRequest);
    }

    /**
     * 받은 친구 요청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<FriendRequestResponse> getReceivedFriendRequests() {
        Member currentMember = getCurrentMember();
        return friendRepository.findByToMemberAndStatus(currentMember, FriendStatus.PENDING)
                .stream()
                .map(this::convertToFriendRequestResponse)
                .collect(Collectors.toList());
    }

    /**
     * 친구 요청 수락
     */
    public FriendRequestResponse acceptFriendRequest(Long requestId) {
        Member currentMember = getCurrentMember();
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("친구 요청을 찾을 수 없습니다."));

        // 요청 대상자가 현재 사용자인지 확인
        if (!friendRequest.getToMember().getId().equals(currentMember.getId())) {
            throw new RuntimeException("해당 친구 요청에 대한 권한이 없습니다.");
        }

        // 이미 처리된 요청인지 확인
        if (friendRequest.getStatus() != FriendStatus.PENDING) {
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        friendRequest.setStatus(FriendStatus.ACCEPTED);
        return convertToFriendRequestResponse(friendRepository.save(friendRequest));
    }

    /**
     * 친구 요청 거절
     */
    public void rejectFriendRequest(Long requestId) {
        Member currentMember = getCurrentMember();
        Friend friendRequest = friendRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("친구 요청을 찾을 수 없습니다."));

        // 요청 대상자가 현재 사용자인지 확인
        if (!friendRequest.getToMember().getId().equals(currentMember.getId())) {
            throw new RuntimeException("해당 친구 요청에 대한 권한이 없습니다.");
        }

        // 이미 처리된 요청인지 확인
        if (friendRequest.getStatus() != FriendStatus.PENDING) {
            throw new RuntimeException("이미 처리된 친구 요청입니다.");
        }

        friendRepository.delete(friendRequest);
    }

    /**
     * 친구 피드 조회
     */
    @Transactional(readOnly = true)
    public List<FriendCardResponse> getFriendsFeed() {
        Member currentMember = getCurrentMember();
        
        // 현재 사용자의 친구 목록 조회 (양방향 친구 관계 고려)
        List<Member> friends = friendRepository.findAcceptedFriends(currentMember);
        
        // 친구들의 카드 정보 조회 (공개된 카드만)
        return cardRepository.findByOwnerIdInAndIsPublicTrueOrderByCreatedAtDesc(
                friends.stream().map(Member::getId).collect(Collectors.toList()))
                .stream()
                .map(this::convertToFriendCardResponse)
                .collect(Collectors.toList());
    }

    /**
     * Friend 엔티티를 FriendRequestResponse DTO로 변환
     */
    private FriendRequestResponse convertToFriendRequestResponse(Friend friend) {
        return FriendRequestResponse.builder()
                .requestId(friend.getId())
                .fromMemberId(friend.getFromMember().getId().toString())
                .fromMemberNickname(friend.getFromMember().getNickname())
                .toMemberId(friend.getToMember().getId().toString())
                .toMemberNickname(friend.getToMember().getNickname())
                .status(friend.getStatus().name())
                .createdAt(friend.getCreatedAt())
                .build();
    }

    /**
     * Card 엔티티를 FriendCardResponse DTO로 변환
     */
    private FriendCardResponse convertToFriendCardResponse(Card card) {
        return FriendCardResponse.builder()
                .cardId(card.getId())
                .memberId(card.getOwnerId().toString())
                .memberNickname(card.getTargetMember().getNickname())
                .content(card.getContent())
                .coinType(card.getCoinName())
                .createdAt(card.getCreatedAt())
                .build();
    }
} 