package com.voin.util;

import com.voin.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 닉네임 유효성 검증 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class NicknameValidator {
    
    private final MemberRepository memberRepository;
    
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 10;
    private static final String NICKNAME_PATTERN = "^[가-힣a-zA-Z0-9]+$";
    
    /**
     * 닉네임 유효성 검사
     * @param nickname 검사할 닉네임
     * @return 오류 메시지 (유효한 경우 null)
     */
    public String validateNickname(String nickname) {
        // 길이 검사
        if (nickname == null || nickname.trim().isEmpty()) {
            return "닉네임을 입력해주세요.";
        }

        if (nickname.length() < MIN_LENGTH) {
            return "닉네임은 " + MIN_LENGTH + "자 이상이어야 합니다.";
        }

        if (nickname.length() > MAX_LENGTH) {
            return "닉네임은 " + MAX_LENGTH + "자 이하여야 합니다.";
        }

        // 문자 패턴 검사
        if (!nickname.matches(NICKNAME_PATTERN)) {
            return "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.";
        }

        // 중복 검사
        if (memberRepository.existsByNickname(nickname)) {
            return "이미 사용 중인 닉네임입니다.";
        }

        return null;
    }
    
    /**
     * 닉네임이 유효한지 확인
     * @param nickname 검증할 닉네임
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean isValid(String nickname) {
        return validateNickname(nickname) == null;
    }
} 