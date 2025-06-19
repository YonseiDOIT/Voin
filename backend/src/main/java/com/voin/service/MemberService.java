package com.voin.service;

import com.voin.entity.Member;
import com.voin.exception.ResourceNotFoundException;
import com.voin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findById(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
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
    public Member updateMember(UUID memberId, Member updatedMember) {
        Member existingMember = findById(memberId);
        
        Member updatedEntity = Member.builder()
                .id(existingMember.getId())
                .kakaoId(existingMember.getKakaoId())
                .nickname(updatedMember.getNickname() != null ? updatedMember.getNickname() : existingMember.getNickname())
                .profileImage(updatedMember.getProfileImage() != null ? updatedMember.getProfileImage() : existingMember.getProfileImage())
                .friendCode(existingMember.getFriendCode())
                .isActive(existingMember.getIsActive())
                .build();
        
        return memberRepository.save(updatedEntity);
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
} 