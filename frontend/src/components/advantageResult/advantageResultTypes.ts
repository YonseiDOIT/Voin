export type AdvantageResultTheme = 
    | 'GROWTH'
    | 'EMOTION' 
    | 'CREATIVITY'
    | 'PROBLEM_SOLVING'
    | 'RELATIONSHIP'
    | 'BELIEFS';

// 테마 매핑 헬퍼 함수
export const getCategoryTheme = (categoryId: string): AdvantageResultTheme => {
    const themeMap: Record<string, AdvantageResultTheme> = {
        'growth': 'GROWTH',
        'emotion': 'EMOTION',
        'creativity': 'CREATIVITY',
        'problemSolving': 'PROBLEM_SOLVING',
        'relationship': 'RELATIONSHIP',
        'Beliefs': 'BELIEFS',
    };
    
    return themeMap[categoryId] || 'GROWTH';
};
