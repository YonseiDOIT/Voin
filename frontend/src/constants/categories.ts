// 카테고리 관련 상수들

export interface Category {
    id: string;
    title: string;
    color: string;
    items?: string[];
}

// 카테고리 순서 정의 (이 순서가 UI에서 표시되는 순서)
export const CATEGORY_ORDER = [
    '관리와 성장',
    '감정과 태도', 
    '창의와 몰입',
    '사고와 해결',
    '관계와 공감',
    '신념과 실행'
] as const;

// 카테고리 색상 매핑
export const CATEGORY_COLOR_MAP: { [key: string]: string } = {
    '관리와 성장': 'bg-blue-100 text-blue-800',
    '감정과 태도': 'bg-orange-300 text-orange-700',
    '창의와 몰입': 'bg-violet-200 text-purple-700',
    '사고와 해결': 'bg-green-200 text-green-700',
    '관계와 공감': 'bg-yellow-200 text-yellow-700',
    '신념과 실행': 'bg-red-200 text-rose-700',
};