import { create } from 'zustand';
import { authService } from '@/services/authService';
import type { Member } from '@/services/authService';

interface AuthState {
    isAuthenticated: boolean;
    member: Member | null;
    token: string | null;
    isLoading: boolean;
    actions: {
        loginWithKakao: () => void;
        logout: () => void;
        checkAuthStatus: () => Promise<void>;
        initialize: () => void;
        setAuthData: (token: string, member: Member) => void;
    };
}

export const useAuthStore = create<AuthState>((set, get) => ({
    isAuthenticated: false,
    member: null,
    token: null,
    isLoading: true,
    hasInitialized: false,
    actions: {
        loginWithKakao: () => {
            authService.loginWithKakao();
        },
        setAuthData: (token: string, member: Member) => {
            authService.storeAuthData(token, member); // localStorage에 저장
            set({ isAuthenticated: true, token, member, isLoading: false });
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
                    get().actions.logout();
                }
            } else {
                set({ isLoading: false });
            }
        },
        initialize: () => {
            get().actions.checkAuthStatus();
        }
    },
}));

useAuthStore.getState().actions.initialize();