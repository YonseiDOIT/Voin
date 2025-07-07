package com.voin.constant;

/**
 * 🔍 상황 맥락 (사례 돌아보기에서 사용)
 * 
 * 사용자가 [사례 돌아보기] 플로우에서 선택할 수 있는 6가지 상황 맥락을 정의합니다.
 * 각 상황 맥락은 ID, 소제목, 제목으로 구성됩니다.
 */
public enum SituationContext {
    DAILY_LIFE(1, "평소 내 모습", "일상적 행동, 습관"),
    INTERACTION(2, "누군가와 상호작용", "다른 사람과 대화, 행동"),
    TEAMWORK(3, "업무/과제/팀플", "무언가를 함께하며 발견"),
    CHALLENGE(4, "도전하는 과정", "새롭거나 어려운 상황"),
    CONSIDERATION(5, "배려하고 챙기는", "타인을 생각하고 배려"),
    ETC(6, "기타", "이 외 다른 행동");

    private final int id;
    private final String subtitle;
    private final String title;

    SituationContext(int id, String subtitle, String title) {
        this.id = id;
        this.subtitle = subtitle;
        this.title = title;
    }

    // Getters
    public int getId() { 
        return id; 
    }
    
    public String getSubtitle() { 
        return subtitle; 
    }
    
    public String getTitle() { 
        return title; 
    }

    /**
     * ID로 상황 맥락을 찾는 정적 메서드
     * @param id 찾고자 하는 상황 맥락의 ID
     * @return 해당하는 SituationContext
     * @throws IllegalArgumentException 유효하지 않은 ID인 경우
     */
    public static SituationContext findById(int id) {
        for (SituationContext context : values()) {
            if (context.getId() == id) {
                return context;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 상황 맥락 ID입니다: " + id);
    }

    /**
     * 전체 상황 맥락 목록을 JSON 형태로 반환하기 위한 메서드
     * @return 모든 상황 맥락의 정보
     */
    public static SituationContext[] getAll() {
        return values();
    }
} 