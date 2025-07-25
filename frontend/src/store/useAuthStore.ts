import { create } from 'zustand';
import { authService } from '@/services/authService';
import type { Member, KakaoLoginResponse } from '@/services/authService';

interface AuthState {
    isAuthenticated: boolean;
    member: Member | null;
    token: string | null;
    isLoading: boolean;
    actions: {
        loginWithKakao: () => Promise<KakaoLoginResponse>;
        logout: () => void;
        checkAuthStatus: () => Promise<void>;
        initialize: () => void;
    };
}

export const useAuthStore = create<AuthState>((set, get) => ({
    isAuthenticated: false,
    member: null,
    token: null,
    isLoading: true,
    actions: {
        // 카카오로 로그인
        loginWithKakao: async () => {
            set({ isLoading: true });
            try {
                const response = await authService.loginWithKakao();
                set({
                    isAuthenticated: true,
                    member: response.member,
                    token: response.jwtToken,
                    isLoading: false,
                });
                return response;
            } catch (error) {
                console.error('카카오 로그인 실패:', error);
                get().actions.logout(); // 실패 시 로그아웃 처리
                throw error;
            }
        },

        setAuthData: (token: string, member: Member) => {
            set({ isAuthenticated: true, token, member, isLoading: false });
        },

        // 로그아웃
        logout: () => {
            authService.logout();
            set({ isAuthenticated: false, member: null, token: null, isLoading: false });
        },

        // 앱 로드 시 인증 상태 확인
        checkAuthStatus: async () => {
            const token = authService.getStoredToken();
            const member = authService.getStoredUserInfo();

            if (token && member) {
                const isValid = await authService.validateToken(token);
                if (isValid) {
                    set({ isAuthenticated: true, member, token, isLoading: false });
                } else {
                    get().actions.logout(); // 토큰이 유효하지 않으면 로그아웃
                }
            } else {
                set({ isLoading: false }); // 토큰이 없으면 로딩 종료
            }
        },
        
        // 스토어 초기화 (앱 시작 시 한 번만 호출)
        initialize: () => {
            get().actions.checkAuthStatus();
        }
    },
}));

// 앱이 시작될 때 인증 상태를 확인합니다.
useAuthStore.getState().actions.initialize();
