import React, { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';

interface AuthContextType {
    isAuthenticated: boolean;
    userInfo: {
        nickname: string;
        profileImage?: string;
    } | null;
    login: (nickname: string, profileImage?: string, accessToken?: string) => void;
    logout: () => void;
    checkAuthStatus: () => Promise<boolean>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [userInfo, setUserInfo] = useState<{ nickname: string; profileImage?: string } | null>(null);

    const login = (nickname: string, profileImage?: string, accessToken?: string) => {
        console.log('Login called with:', { nickname, profileImage, accessToken });
        setIsAuthenticated(true);
        setUserInfo({ nickname, profileImage });
        localStorage.setItem('nickname', nickname);
        if (profileImage) {
            localStorage.setItem('profileImage', profileImage);
        }
        if (accessToken) {
            localStorage.setItem('accessToken', accessToken);
        }
        console.log('Login completed, isAuthenticated set to true');
    };

    const logout = () => {
        console.log('Logout called');
        setIsAuthenticated(false);
        setUserInfo(null);
        
        // localStorage 완전 정리
        localStorage.removeItem('accessToken');
        localStorage.removeItem('nickname');
        localStorage.removeItem('profileImage');
        
        // 추가 정리 - 혹시 다른 키들도 정리
        Object.keys(localStorage).forEach(key => {
            if (key.startsWith('auth_') || key.startsWith('user_') || key.startsWith('kakao_')) {
                localStorage.removeItem(key);
            }
        });
        
        console.log('Logout completed, localStorage cleared');
        
        // 상태 완전 초기화 확인
        setTimeout(() => {
            setIsAuthenticated(false);
            setUserInfo(null);
            console.log('Logout timeout completed, states reset');
        }, 100);
    };

    const checkAuthStatus = async (): Promise<boolean> => {
        try {
            const accessToken = localStorage.getItem('accessToken');
            const nickname = localStorage.getItem('nickname');

            if (!accessToken) {
                return false;
            }

            // 백엔드에서 토큰 유효성 검사
            const response = await fetch('/api/auth/validate', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                },
                credentials: 'include'
            });

            if (response.ok) {
                const data = await response.json();
                if (data.success) {
                    setIsAuthenticated(true);
                    setUserInfo({
                        nickname: data.nickname || nickname || '',
                        profileImage: data.profileImage || localStorage.getItem('profileImage') || undefined
                    });
                    return true;
                }
            }

            // 토큰이 유효하지 않으면 로그아웃 처리
            logout();
            return false;
        } catch (error) {
            console.error('Auth check error:', error);
            logout();
            return false;
        }
    };

    useEffect(() => {
        // 초기 로드 시 인증 상태 확인
        const initAuth = async () => {
            try {
                const accessToken = localStorage.getItem('accessToken');
                const nickname = localStorage.getItem('nickname');
                console.log('AuthContext initAuth - checking localStorage:', { accessToken, nickname });

                if (accessToken && nickname) {
                    // 간단한 로컬 스토리지 기반 인증 (백엔드 검증 없이)
                    console.log('AuthContext initAuth - found tokens, setting authenticated');
                    setIsAuthenticated(true);
                    setUserInfo({
                        nickname,
                        profileImage: localStorage.getItem('profileImage') || undefined
                    });
                } else {
                    // 토큰이나 닉네임이 없으면 명시적으로 로그아웃 상태로 설정
                    console.log('AuthContext initAuth - no tokens found, setting unauthenticated');
                    setIsAuthenticated(false);
                    setUserInfo(null);
                }
            } catch (error) {
                console.error('Auth initialization error:', error);
                setIsAuthenticated(false);
                setUserInfo(null);
            }
        };

        initAuth();
    }, []);

    return (
        <AuthContext.Provider value={{
            isAuthenticated,
            userInfo,
            login,
            logout,
            checkAuthStatus
        }}>
            {children}
        </AuthContext.Provider>
    );
};
