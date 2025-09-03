import { create } from 'zustand';
import { authService } from '@/services/authService';
import type { Member } from '@/services/authService';

interface AuthActions {
    // ✅ 서버 리다이렉트 플로우: 반환값 없음
    loginWithKakao: () => void;
    logout: () => void;
    checkAuthStatus: () => Promise<void>;
    initialize: () => Promise<void>;
    setAuthData: (token: string, member?: Member | null) => void;
    clearAuth: () => void;
}

interface AuthState {
    isAuthenticated: boolean;
    member: Member | null;
    token: string | null;
    isLoading: boolean;
    hasInitialized: boolean;
    actions: AuthActions;
}

export const useAuthStore = create<AuthState>((set, get) => ({
    isAuthenticated: false,
    member: null,
    token: null,
    isLoading: true,
    hasInitialized: false,
    actions: {
        // ✅ 리다이렉트만 트리거
        loginWithKakao: () => {
            authService.loginWithKakao(); // window.Kakao.Auth.authorize(...) or /api/auth/kakao/url 로 이동
        },

        setAuthData: (token, member = null) => {
            authService.storeAuthData(token, member ?? authService.getStoredUserInfo() ?? null);
            set({
                isAuthenticated: true,
                token,
                member: member ?? authService.getStoredUserInfo() ?? null,
                isLoading: false,
            });
        },

        clearAuth: () => {
            authService.logout();
            set({ isAuthenticated: false, member: null, token: null, isLoading: false });
        },

        logout: () => {
            authService.logout();
            set({ isAuthenticated: false, member: null, token: null, isLoading: false });
        },

        checkAuthStatus: async () => {
            const token = authService.getStoredToken();
            const storedMember = authService.getStoredUserInfo();
            if (!token) {
                set({ isAuthenticated: false, member: null, token: null, isLoading: false });
                return;
            }
            set({ isLoading: true });
            try {
                const ok = await authService.validateToken(token);
                if (ok) {
                    set({
                        isAuthenticated: true,
                        member: storedMember ?? null,
                        token,
                        isLoading: false,
                    });
                } else {
                    get().actions.clearAuth();
                }
            } catch {
                get().actions.clearAuth();
            }
        },

        initialize: async () => {
            if (get().hasInitialized) return;
            set({ isLoading: true });
            await get().actions.checkAuthStatus();
            set({ hasInitialized: true });
        },
    },
}));

// App.tsx에서 useEffect로 initialize() 호출하면 아래 줄은 제거해도 됩니다.
useAuthStore.getState().actions.initialize();
