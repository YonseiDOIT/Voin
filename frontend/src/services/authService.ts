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

class AuthService {
    private apiBase = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

    /** 카카오 로그인 시작: 백엔드 302 리다이렉트 사용 (fetch X) */
    loginWithKakao(): void {
        window.location.href = `${this.apiBase}/api/auth/kakao/url`;
    }

    /** 서버 콜백 방식에서는 token만 받음. member는 추후 /api/me로 채우는 걸 권장 */
    storeAuthData(token: string, member: Member | null): void {
        localStorage.setItem('jwtToken', token);
        if (member) localStorage.setItem('userInfo', JSON.stringify(member));
    }

    getStoredToken(): string | null {
        return localStorage.getItem('jwtToken');
    }

    getStoredUserInfo(): Member | null {
        const raw = localStorage.getItem('userInfo');
        return raw ? JSON.parse(raw) : null;
    }

    async validateToken(token: string): Promise<boolean> {
        try {
            const res = await fetch(`${this.apiBase}/api/auth/validate`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            const json: ApiResponse<boolean> = await res.json();
            return res.ok && json.success && json.data === true;
        } catch {
            return false;
        }
    }

    logout(): void {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userInfo');
    }
}

export const authService = new AuthService();
