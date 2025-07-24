export type AdvantageResultTheme = 
    | 'GROWTH'
    | 'EMOTION' 
    | 'CREATIVITY'
    | 'PROBLEM_SOLVING'
    | 'RELATIONSHIP'
    | 'BELIEFS';

// 테마 매핑 헬퍼 함수
export const getCategoryTheme = (categoryName: string): AdvantageResultTheme => {
    const themeMap: Record<string, AdvantageResultTheme> = {
        // 카테고리 이름으로 매핑
        '관리와 성장': 'GROWTH',
        '감정과 태도': 'EMOTION',
        '창의와 몰입': 'CREATIVITY',
        '사고와 해결': 'PROBLEM_SOLVING',
        '관계와 공감': 'RELATIONSHIP',
        '신념과 실행': 'BELIEFS',
        // 기존 ID 방식 호환성 유지
        'growth': 'GROWTH',
        'emotion': 'EMOTION',
        'creativity': 'CREATIVITY',
        'problemSolving': 'PROBLEM_SOLVING',
        'relationship': 'RELATIONSHIP',
        'beliefs': 'BELIEFS',
        // 코인 이름으로 매핑 (소문자)
        'blue': 'GROWTH',
        'orange': 'EMOTION',
        'purple': 'CREATIVITY',
        'green': 'PROBLEM_SOLVING',
        'yellow': 'RELATIONSHIP',
        'red': 'BELIEFS',
    };
    
    return themeMap[categoryName] || 'GROWTH';
};
