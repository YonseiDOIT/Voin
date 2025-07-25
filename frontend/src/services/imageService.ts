// src/services/imageService.ts

/**
 * 주어진 URL의 이미지가 실제로 존재하는지 확인합니다.
 * HTTP HEAD 요청을 보내 리소스의 존재 여부만 빠르게 확인합니다.
 * @param url - 확인할 이미지의 URL
 * @returns 이미지가 존재하면 true, 그렇지 않으면 false를 반환합니다.
 */
export const checkImageExists = async (url: string): Promise<boolean> => {
    try {
        const response = await fetch(url, { method: 'HEAD' });
        return response.ok;
    } catch (error) {
        // 네트워크 오류 등 fetch 자체가 실패하는 경우
        console.error(`Failed to check image existence for ${url}:`, error);
        return false;
    }
};
