import { create } from 'zustand';

// API 응답 타입 정의
type ApiCoinInfo = {
    id: number;
    name: string;
    description: string;
    color: string;
};

type ApiKeywordInfo = {
    id: number;
    name: string;
    description: string;
    coin: ApiCoinInfo;
};

type ApiResponse = {
    success: boolean;
    message: string;
    data: {
        [categoryName: string]: ApiKeywordInfo[];
    };
};

// 스토어에서 사용할 데이터 타입 정의
export type Keyword = {
    id: number;
    name: string;
    description: string;
};

export type StrengthCategory = {
    id: number;
    name: string;
    description: string;
    color: string;
    keywords: Keyword[];
};

// 스토어의 상태(State) 타입
interface StrengthState {
    categories: Map<string, StrengthCategory>;
    isLoading: boolean;
    error: string | null;
    fetchStrengths: () => Promise<void>;
}

// Zustand 스토어 생성
export const useStrengthStore = create<StrengthState>((set) => ({
    categories: new Map(),
    isLoading: false,
    error: null,

    /**
     * API를 통해 모든 강점 카테고리와 키워드 데이터를 가져와 스토어 상태를 업데이트합니다.
     * 데이터 로딩 중에는 isLoading 상태가 true로 설정되며,
     * 성공 시에는 categories 맵에 데이터가 채워지고, 실패 시에는 error 상태가 설정됩니다.
     */
    fetchStrengths: async () => {
        set({ isLoading: true, error: null });
        try {
            const response = await fetch('/api/master/keywords');
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const apiResponse: ApiResponse = await response.json();

            if (!apiResponse.success) {
                throw new Error(apiResponse.message || 'Failed to fetch strength data.');
            }

            const processedCategories = new Map<string, StrengthCategory>();
            for (const [categoryName, keywordsList] of Object.entries(apiResponse.data)) {
                if (keywordsList.length > 0) {
                    const coinInfo = keywordsList[0].coin;
                    processedCategories.set(categoryName, {
                        id: coinInfo.id,
                        name: coinInfo.name,
                        description: coinInfo.description,
                        color: coinInfo.color,
                        keywords: keywordsList.map(k => ({ id: k.id, name: k.name, description: k.description })),
                    });
                }
            }
            
            set({ categories: processedCategories, isLoading: false });
        } catch (error) {
            const errorMessage = error instanceof Error ? error.message : 'An unknown error occurred.';
            set({ error: errorMessage, isLoading: false });
            console.error("Error fetching strengths:", errorMessage);
        }
    },
}));

// 애플리케이션 시작 시 강점 데이터를 미리 로드합니다.
useStrengthStore.getState().fetchStrengths();
