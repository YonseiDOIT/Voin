// src/services/strengthService.ts

import type { StrengthCategory } from "@/store/useStrengthStore";

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

/**
 * 서버로부터 모든 강점 카테고리와 키워드 데이터를 가져옵니다.
 * @returns {Promise<Map<string, StrengthCategory>>} 카테고리 이름을 키로 하는 강점 데이터 맵
 */
export const fetchAllStrengths = async (): Promise<Map<string, StrengthCategory>> => {
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
        return processedCategories;
    } catch (error) {
        console.error("Error fetching strengths:", error);
        throw error; // 오류를 다시 던져 호출부에서 처리하도록 함
    }
};
