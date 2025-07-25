// src/services/gptService.ts

import type { ActivityData } from "@/store/useActivityStore";

type ClassifyResponse = {
    summary: string;
    category: string;
    keyword: string;
};

/**
 * 주어진 텍스트를 AI를 통해 분류하고, 관련된 장점 키워드를 반환합니다.
 * @param text - 분류할 텍스트 (사용자의 경험담 등)
 * @returns 분류 결과 (요약, 카테고리, 키워드)
 */
export const classifyText = async (text: string): Promise<Partial<ActivityData>> => {
    try {
        const response = await fetch('/api/gpt/classify', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ input: text }),
        });

        if (!response.ok) {
            throw new Error(`API error: ${response.statusText}`);
        }

        const result = await response.json();

        if (result.success && result.data && !result.data.error) {
            const data: ClassifyResponse = result.data;
            return {
                classify: data.summary,
                categoryName: data.category,
                strengthName: data.keyword,
            };
        } else if (result.data.error) {
            throw new Error(result.data.error);
        } else {
            throw new Error(result.message || 'Failed to get classification from API.');
        }
    } catch (error) {
        console.error('Error fetching classification:', error);
        // 실제 서비스에서는 사용자에게 보여줄 에러 메시지를 반환하거나,
        // 에러 로깅 서비스를 사용하는 것이 좋습니다.
        throw error;
    }
};
