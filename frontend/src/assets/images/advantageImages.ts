// 장점별 이미지들을 동적으로 처리
// 블로그의 require().default 개념을 Vite에 맞게 적용

// 기본 원형 SVG 이미지를 생성하는 함수 (placeholder 대신)
export const getCirclePlaceholderUrl = (title: string): string => {
    // SVG 원형 이미지를 data URL로 생성
    const svg = `
        <svg width="240" height="240" xmlns="http://www.w3.org/2000/svg">
            <circle cx="120" cy="120" r="100" fill="#e5e7eb" stroke="#9ca3af" stroke-width="2"/>
            <text x="120" y="130" text-anchor="middle" font-family="Arial, sans-serif" font-size="16" fill="#374151">
                ${title}
            </text>
        </svg>
    `;
    
    // SVG를 data URL로 인코딩
    const encodedSvg = encodeURIComponent(svg);
    return `data:image/svg+xml,${encodedSvg}`;
};

// 블로그에서 제시한 방법을 Vite에 맞게 적용한 동적 이미지 로딩
// 이미지 이름을 받아서 경로를 동적으로 생성
// 장점 키워드 이름 -> ID 매핑 (API 데이터 기반)
const advantageNameToId: Record<string, number> = {
    // 관리와 성장 카테고리
    "끈기": 1,
    "성실함": 3,
    "성찰력": 7,
    "수용성": 9,
    "인내심": 2,
    "적응력": 8,
    "절제력": 4,
    "침착함": 5,
    "학습력": 6,
    
    // 감정과 태도 카테고리
    "감수성": 11,
    "긍정성": 14,
    "밝은 에너지": 13,
    "열정": 15,
    "유머 감각": 10,
    "표현력": 12,
    
    // 창의와 몰입 카테고리
    "기획력": 21,
    "몰입력": 20,
    "집중력": 19,
    "창의력": 18,
    "탐구력": 17,
    "호기심": 16,
    
    // 사고와 해결 카테고리
    "논리력": 23,
    "문제해결력": 27,
    "분석력": 24,
    "신중성": 26,
    "융통성": 28,
    "통찰력": 25,
    "판단력": 22,
    
    // 관계와 공감 카테고리
    "겸손함": 38,
    "경청 태도": 32,
    "공감력": 29,
    "배려심": 30,
    "예의 바름": 39,
    "온화함": 35,
    "조율력": 37,
    "중재력": 36,
    "지지력": 34,
    "친화력": 33,
    "포용력": 31,
    
    // 신념과 실행 카테고리
    "결단력": 46,
    "계획성": 52,
    "공정성": 50,
    "도덕심": 44,
    "도전력": 53,
    "리더십": 49,
    "신념": 40,
    "실행력": 48,
    "용기": 45,
    "정의감": 43,
    "정직함": 42,
    "주도성": 47,
    "주체성": 41,
    "책임감": 51
};

// 동적 이미지 URL 생성 (ID 기반)
const getDynamicImageUrl = (itemName: string): string => {
    let id = advantageNameToId[itemName];
    
    // 공백이 있는 키워드에 대한 폴백 처리
    if (!id) {
        // 공백 제거한 버전으로 시도
        const nameWithoutSpace = itemName.replace(/\s+/g, '');
        const fallbackMapping: Record<string, string> = {
            "유머감각": "유머 감각",
            "밝은에너지": "밝은 에너지", 
            "경청태도": "경청 태도",
            "예의바름": "예의 바름"
        };
        id = advantageNameToId[fallbackMapping[nameWithoutSpace] || itemName];
    }
    
    if (id) {
        // Vite에서 assets 폴더 이미지에 접근하는 방식
        const imageUrl = new URL(`./advantages/${id}.png`, import.meta.url).href;
        return imageUrl;
    } else {
        // ID가 없으면 기존 한글 이름 방식으로 폴백
        const imageUrl = new URL(`./advantages/${itemName}.png`, import.meta.url).href;
        return imageUrl;
    }
};

// 이미지 존재 여부를 확인하는 함수 (블로그에서 언급한 '엑박' 방지)
// 이미지 존재 여부 확인 함수 (블로그에서 언급한 엑박 방지)
const checkImageExists = async (url: string): Promise<boolean> => {
    try {
        const response = await fetch(url, { method: 'HEAD' });
        return response.ok;
    } catch (error) {
        console.error(`이미지 확인 에러 (${url}):`, error); // 디버깅용
        return false;
    }
};

// 블로그 방법을 기반으로 한 이미지 가져오기 함수
export const getAdvantageImage = async (itemName: string): Promise<string> => {
    try {
        const imageUrl = getDynamicImageUrl(itemName);
        
        const exists = await checkImageExists(imageUrl);
        
        if (exists) {
            return imageUrl;
        } else {
            return getCirclePlaceholderUrl(itemName);
        }
    } catch {
        return getCirclePlaceholderUrl(itemName);
    }
};

// 동기 버전 (즉시 경로 반환, 존재 여부 확인 없음)
export const getAdvantageImageSync = (itemName: string): string => {
    return getDynamicImageUrl(itemName);
};
