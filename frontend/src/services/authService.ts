// API 응답 타입 정의
export interface ApiResponse<T> {
    success: boolean;
    message: string;
    data: T;
    errorCode: string | null;
}

export interface Member {
    createdAt: string;
    updatedAt: string;
    id: string;
    kakaoId: string;
    nickname: string;
    profileImage: string;
    friendCode: string;
    isActive: boolean;
}

export interface KakaoLoginResponse {
    type: "Login" | "Signup";
    member: Member;
    jwtToken: string;
}

// Kakao SDK 타입 정의
declare global {
    interface Window {
        Kakao: {
            init: (key: string) => void;
            isInitialized: () => boolean;
            Auth: {
                authorize: (options: {
                    redirectUri: string;
                }) => void;
            };
        };
    }
}

class AuthService {
    private baseURL = '/api';

    /**
     * 카카오 로그인 페이지로 리다이렉트합니다.
     */
    loginWithKakao(): void {
        if (!window.Kakao) {
            throw new Error('Kakao SDK가 로드되지 않았습니다.');
        }

        if (!window.Kakao.isInitialized()) {
            const kakaoJsKey = import.meta.env.VITE_KAKAO_JS_KEY;
            if (!kakaoJsKey) {
                throw new Error('카카오 JS KEY가 설정되지 않았습니다. (.env 파일 확인)');
            }
            window.Kakao.init(kakaoJsKey);
        }

        window.Kakao.Auth.authorize({
            redirectUri: 'https://localhost:5173/auth/kakao/callback',
        });
    }

    /**
     * 카카오 로그인 성공 후 리다이렉트된 페이지에서 호출됩니다.
     * URL의 인증 코드를 사용하여 백엔드에 토큰 발급을 요청합니다.
     * @param code URL에서 받은 인증 코드
     */
    async handleKakaoCallback(code: string): Promise<KakaoLoginResponse> {
        // 카카오 SDK를 통해 직접 토큰을 받고, 그 토큰을 우리 백엔드로 보냅니다.
        const tokenResponse = await this.getKakaoToken(code);
        const backendResponse = await this.verifyKakaoToken(tokenResponse.access_token);
        
        this.storeAuthData(backendResponse.jwtToken, backendResponse.member);
        return backendResponse;
    }

    /**
     * 카카오 서버에 인증 코드를 보내 액세스 토큰을 받습니다.
     * @param code 인증 코드
     */
    private async getKakaoToken(code: string): Promise<{ access_token: string }> {
        const params = new URLSearchParams();
        params.append('grant_type', 'authorization_code');
        params.append('client_id', import.meta.env.VITE_KAKAO_JS_KEY);
        params.append('redirect_uri', 'https://localhost:5173/auth/kakao/callback');
        params.append('code', code);

        const response = await fetch('https://kauth.kakao.com/oauth/token', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
            },
            body: params,
        });

        const result = await response.json();
        if (!response.ok) {
            throw new Error(result.error_description || '카카오 토큰 발급에 실패했습니다.');
        }
        return result;
    }

    /**
     * 백엔드에 카카오 액세스 토큰을 보내 검증하고 JWT를 받습니다.
     * @param accessToken 카카오가 발급한 액세스 토큰
     */
    private async verifyKakaoToken(accessToken: string): Promise<KakaoLoginResponse> {
        const response = await fetch(`${this.baseURL}/auth/kakao/verify`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ accessToken }),
        });

        const result: ApiResponse<KakaoLoginResponse> = await response.json();

        if (!response.ok || !result.success) {
            throw new Error(result.message || '카카오 토큰 검증에 실패했습니다.');
        }
        
        return result.data;
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     */
    async validateToken(token: string): Promise<boolean> {
        try {
            const response = await fetch(`${this.baseURL}/auth/validate`, {
                headers: { 'Authorization': `Bearer ${token}` },
            });
            const result: ApiResponse<boolean> = await response.json();
            return response.ok && result.success && result.data;
        } catch (error) {
            console.error('Token validation failed:', error);
            return false;
        }
    }

    // 인증 정보(토큰, 유저정보)를 localStorage에 저장
    storeAuthData(token: string, member: Member): void {
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('userInfo', JSON.stringify(member));
    }

    // 저장된 토큰 가져오기
    getStoredToken(): string | null {
        return localStorage.getItem('jwtToken');
    }

    // 저장된 유저 정보 가져오기
    getStoredUserInfo(): Member | null {
        const userInfo = localStorage.getItem('userInfo');
        return userInfo ? JSON.parse(userInfo) : null;
    }

    // 로그아웃
    logout(): void {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userInfo');
    }
}

export const authService = new AuthService();