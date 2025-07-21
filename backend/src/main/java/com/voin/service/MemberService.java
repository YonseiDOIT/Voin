package com.voin.service;

import com.voin.dto.request.MemberUpdateRequest;
import com.voin.dto.response.MemberResponse;
import com.voin.entity.Member;
import com.voin.exception.ResourceNotFoundException;
import com.voin.repository.CardRepository;
import com.voin.repository.FriendRepository;
import com.voin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;
    private final FriendRepository friendRepository;

    /**
     * 현재 로그인한 사용자 정보 가져오기
     */
    private Member getCurrentMember() {
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();
        return memberRepository.findById(UUID.fromString(memberId))
                .orElseThrow(() -> new RuntimeException("로그인한 사용자를 찾을 수 없습니다."));
    }

    /**
     * 내 정보 조회
     */
    public MemberResponse getMyInfo() {
        Member member = getCurrentMember();
        return convertToMemberResponse(member);
    }

    /**
     * 내 정보 수정
     */
    @Transactional
    public MemberResponse updateMyInfo(MemberUpdateRequest request) {
        Member member = getCurrentMember();
        
        if (request.getNickname() != null && !request.getNickname().trim().isEmpty()) {
            member.updateNickname(request.getNickname());
        }
        
        if (request.getProfileImage() != null) {
            member.updateProfileImage(request.getProfileImage());
        }
        
        Member updatedMember = memberRepository.save(member);
        log.info("Member info updated: id={}", updatedMember.getId());
        
        return convertToMemberResponse(updatedMember);
    }

    /**
     * 닉네임으로 회원 검색
     */
    public List<MemberResponse> searchByNickname(String nickname) {
        List<Member> allMembers = memberRepository.findAll();
        List<Member> members = allMembers.stream()
                .filter(member -> member.getNickname() != null && 
                        member.getNickname().toLowerCase().contains(nickname.toLowerCase()))
                .collect(Collectors.toList());
        return members.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
    }

    /**
     * 친구 코드로 회원 찾기
     */
    public MemberResponse getMemberByFriendCode(String friendCode) {
        Member member = memberRepository.findByFriendCode(friendCode)
                .orElseThrow(() -> new RuntimeException("해당 친구 코드를 가진 회원을 찾을 수 없습니다."));
        return convertToMemberResponse(member);
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void deleteMyAccount() {
        Member member = getCurrentMember();
        
        // 관련 데이터 삭제는 DB의 CASCADE 설정이나 별도 로직으로 처리
        // 여기서는 회원만 삭제
        memberRepository.delete(member);
        log.info("Member account deleted: id={}", member.getId());
    }

    /**
     * 내 통계 정보 조회
     */
    public Map<String, Object> getMyStats() {
        Member member = getCurrentMember();
        
        Map<String, Object> stats = new HashMap<>();
        // 기본 통계 정보만 제공 (실제 카운트는 별도 구현 필요)
        stats.put("cardCount", 0);
        stats.put("friendCount", 0);
        stats.put("publicCardCount", 0);
        stats.put("memberId", member.getId());
        stats.put("nickname", member.getNickname());
        
        return stats;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member findById(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
    }

    public Member getMemberById(String memberId) {
        UUID uuid = UUID.fromString(memberId);
        return findById(uuid);
    }

    public Optional<Member> findByKakaoId(String kakaoId) {
        return memberRepository.findByKakaoId(kakaoId);
    }

    public Optional<Member> findByFriendCode(String friendCode) {
        return memberRepository.findByFriendCode(friendCode);
    }

    @Transactional
    public Member createMember(Member member) {
        Member savedMember = memberRepository.save(member);
        log.info("Created new member: {} with kakaoId: {}", savedMember.getId(), savedMember.getKakaoId());
        return savedMember;
    }

    @Transactional
    public Member updateMember(UUID memberId, Member memberDetails) {
        Member member = findById(memberId);
        
        if (memberDetails.getNickname() != null) {
            member.updateNickname(memberDetails.getNickname());
        }
        
        if (memberDetails.getProfileImage() != null) {
            member.updateProfileImage(memberDetails.getProfileImage());
        }
        
        return memberRepository.save(member);
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = findById(memberId);
        memberRepository.delete(member);
        log.info("Deleted member: {}", memberId);
    }

    public boolean existsByKakaoId(String kakaoId) {
        return memberRepository.existsByKakaoId(kakaoId);
    }

    public boolean existsByFriendCode(String friendCode) {
        return memberRepository.existsByFriendCode(friendCode);
    }

    /**
     * Member 엔티티를 MemberResponse DTO로 변환
     */
    private MemberResponse convertToMemberResponse(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .kakaoId(member.getKakaoId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .friendCode(member.getFriendCode())
                .isActive(member.getIsActive())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
} 