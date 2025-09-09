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
    type: 'Login' | 'Signup';
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

interface KakaoTokenResponse {
    access_token: string;
    token_type: string;
    refresh_token: string;
    expires_in: number;
    scope: string;
    refresh_token_expires_in: number;
}


class AuthService {
    private apiBase = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

    /**
     * Kakao SDK를 초기화합니다.
     */
    private initializeKakao(): void {
        if (window.Kakao && !window.Kakao.isInitialized()) {
            const kakaoJsKey = import.meta.env.VITE_KAKAO_JS_KEY;
            if (!kakaoJsKey) {
                throw new Error('VITE_KAKAO_JS_KEY가 .env 파일에 설정되지 않았습니다.');
            }
            window.Kakao.init(kakaoJsKey);
        }
    }

    /**
     * 카카오 로그인 페이지로 리다이렉트합니다.
     */
    loginWithKakao(): void {
        this.initializeKakao();
        if (!window.Kakao) {
            throw new Error('Kakao SDK를 불러오는데 실패했습니다.');
        }

        const redirectUri = import.meta.env.VITE_KAKAO_REDIRECT_URI;
        if (!redirectUri) {
            throw new Error('VITE_KAKAO_REDIRECT_URI가 .env 파일에 설정되지 않았습니다.');
        }

        window.Kakao.Auth.authorize({
            redirectUri: redirectUri,
        });
    }

    /**
     * 카카오 로그인 콜백을 처리합니다.
     * 인증 코드로 카카오에서 토큰을 받고, 그 토큰을 백엔드로 보냅니다.
     * @param code 카카오에서 받은 인증 코드
     */
    async handleKakaoCallback(code: string): Promise<KakaoLoginResponse> {
        try {
            // 1. 인증 코드로 카카오에 액세스 토큰 요청
            const accessToken = await this.getKakaoToken(code);
            console.log('Kakao access token:', accessToken);

            // 2. 받은 액세스 토큰으로 백엔드에 검증 요청
            const result = await this.verifyKakaoToken(accessToken);
            
            return result;
        } catch (error) {
            console.error("카카오 콜백 처리 중 오류:", error);
            throw new Error("카카오 로그인 처리에 실패했습니다.");
        }
    }

    /**
     * 인증 코드를 사용하여 카카오로부터 액세스 토큰을 받아옵니다.
     * @param code 카카오 인증 코드
     */
    private async getKakaoToken(code: string): Promise<string> {
        const redirectUri = import.meta.env.VITE_KAKAO_REDIRECT_URI;
        const kakaoJsKey = import.meta.env.VITE_KAKAO_JS_KEY;

        if (!redirectUri || !kakaoJsKey) {
            throw new Error('.env 파일에 카카오 설정이 누락되었습니다.');
        }

        const tokenUrl = `https://kauth.kakao.com/oauth/token`;
        const params = new URLSearchParams();
        params.append('grant_type', 'authorization_code');
        params.append('client_id', kakaoJsKey);
        params.append('redirect_uri', redirectUri);
        params.append('code', code);

        const response = await fetch(tokenUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8',
            },
            body: params,
        });

        const tokenData: KakaoTokenResponse = await response.json();

        if (!response.ok) {
            throw new Error(tokenData.error_description || '카카오 토큰 발급에 실패했습니다.');
        }

        return tokenData.access_token;
    }


    /**
     * 백엔드에 카카오 액세스 토큰 검증을 요청합니다.
     * @param accessToken 카카오에서 받은 액세스 토큰
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

        this.storeAuthData(result.data.jwtToken, result.data.member);
        return result.data;
    }

    /**
     * JWT 토큰의 유효성을 백엔드에 검증 요청합니다.
     */
    async validateToken(token: string): Promise<boolean> {
        try {
            const res = await fetch(`${this.apiBase}/api/auth/validate`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const result: ApiResponse<boolean> = await response.json();
            return response.ok && result.success && result.data;
        } catch (error) {
            console.error('토큰 검증 실패:', error);
            return false;
        }
    }

    /**
     * 인증 정보(토큰, 사용자 정보)를 localStorage에 저장합니다.
     * @param token JWT 토큰
     * @param member 사용자 정보
     */
    storeAuthData(token: string, member: Member): void {
        localStorage.setItem('jwtToken', token);
        localStorage.setItem('userInfo', JSON.stringify(member));
    }

    /**
     * 저장된 JWT 토큰을 가져옵니다.
     */
    getStoredToken(): string | null {
        return localStorage.getItem('jwtToken');
    }

    /**
     * 저장된 사용자 정보를 가져옵니다.
     */
    getStoredUserInfo(): Member | null {
        const userInfo = localStorage.getItem('userInfo');
        return userInfo ? JSON.parse(userInfo) : null;
    }

    /**
     * 로그아웃을 처리합니다. 저장된 인증 정보를 삭제합니다.
     */
    logout(): void {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userInfo');
    }
}

export const authService = new AuthService();
