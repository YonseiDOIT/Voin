package com.voin.util;

import java.util.regex.Pattern;

/**
 * 닉네임 유효성 검증 유틸리티 클래스
 */
public class NicknameValidator {
    
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 10;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[가-힣a-zA-Z0-9]+$");
    
    /**
     * 닉네임 유효성 검증
     * @param nickname 검증할 닉네임
     * @return 검증 결과 메시지 (null이면 유효함)
     */
    public static String validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return "닉네임을 입력해주세요.";
        }
        
        String trimmedNickname = nickname.trim();
        
        // 글자 수 제한 검증
        if (trimmedNickname.length() > MAX_LENGTH) {
            return "10글자 내로 입력해주세요.";
        }
        
        if (trimmedNickname.length() < MIN_LENGTH) {
            return "닉네임은 2글자 이상 입력해주세요.";
        }
        
        // 특수문자 및 공백 검증
        if (!VALID_PATTERN.matcher(trimmedNickname).matches()) {
            return "공백없이 한글, 영문, 숫자만 입력해주세요.";
        }
        
        return null; // 유효함
    }
    
    /**
     * 닉네임이 유효한지 확인
     * @param nickname 검증할 닉네임
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public static boolean isValid(String nickname) {
        return validateNickname(nickname) == null;
    }
} 