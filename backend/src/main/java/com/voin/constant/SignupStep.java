package com.voin.constant;

/**
 * 회원가입 단계를 나타내는 enum
 */
public enum SignupStep {
    NICKNAME_SETTING("닉네임 설정"),
    PROFILE_IMAGE_SETTING("프로필 이미지 설정"),
    COMPLETED("회원가입 완료");

    private final String description;

    SignupStep(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 