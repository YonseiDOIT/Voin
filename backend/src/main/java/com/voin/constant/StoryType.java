package com.voin.constant;

/**
 * 스토리(Story) 유형을 정의하는 열거형
 * 사용자의 경험이나 일기 등 다양한 형태의 스토리를 구분합니다.
 */
public enum StoryType {
    
    /**
     * 오늘의 일기
     * 사용자가 하루 동안의 경험과 감정을 기록한 일기
     */
    DAILY_DIARY("오늘의 일기", "하루 동안의 경험과 감정을 기록한 일기"),
    
    /**
     * 경험 돌아보기
     * 과거의 특정 경험이나 사건을 회고하며 장점을 발견하는 스토리
     */
    EXPERIENCE_REFLECTION("경험 돌아보기", "과거의 경험을 통해 장점을 발견하는 회고 스토리");
    
    private final String displayName;
    private final String description;
    
    StoryType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * 스토리 타입의 표시 이름을 반환합니다.
     * @return 표시 이름
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 스토리 타입의 설명을 반환합니다.
     * @return 설명
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 일기 타입인지 확인합니다.
     * @return 일기 타입 여부
     */
    public boolean isDiary() {
        return this == DAILY_DIARY;
    }
    
    /**
     * 경험 회고 타입인지 확인합니다.
     * @return 경험 회고 타입 여부
     */
    public boolean isExperienceReflection() {
        return this == EXPERIENCE_REFLECTION;
    }
} 