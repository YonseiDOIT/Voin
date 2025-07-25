import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import type { ReactNode } from 'react';
import type { Member, LoginResponse } from '../services/authService';
import { authService } from '../services/authService';

interface AuthContextType {
    isAuthenticated: boolean;
    userInfo: Member | null;
    isLoading: boolean;
    login: (accessToken: string) => Promise<void>;
    loginWithCode: (code: string) => Promise<void>;
    loginWithBackendCallback: (code: string) => Promise<void>;
    loginWithKakaoPopup: () => Promise<LoginResponse>;
    loginWithKakaoSDK: () => Promise<LoginResponse>;
    loginWithDummy: () => Promise<void>; // 더미 로그인 기능 추가
    setAuthData: (jwtToken: string, member: Member) => void;
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
    const [userInfo, setUserInfo] = useState<Member | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    const login = async (accessToken: string): Promise<void> => {
        try {
            setIsLoading(true);
            console.log('Login 시작:', { accessToken: accessToken.substring(0, 20) + '...' });
            
            // 카카오 액세스 토큰으로 JWT 발급
            const loginData = await authService.verifyKakaoToken(accessToken);
            
            // JWT 토큰과 사용자 정보 저장
            authService.storeToken(loginData.jwtToken);
            authService.storeUserInfo(loginData.member);
            
            // 상태 업데이트
            setIsAuthenticated(true);
            setUserInfo(loginData.member);
            
            console.log('Login 성공:', { member: loginData.member });
        } catch (error) {
            console.error('Login 실패:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const loginWithCode = async (code: string): Promise<void> => {
        try {
            setIsLoading(true);
            console.log('LoginWithCode 시작:', { code: code.substring(0, 20) + '...' });
            
            // 카카오 인증 코드로 JWT 발급
            const loginData = await authService.verifyKakaoCode(code);
            
            // JWT 토큰과 사용자 정보 저장
            authService.storeToken(loginData.jwtToken);
            authService.storeUserInfo(loginData.member);
            
            // 상태 업데이트
            setIsAuthenticated(true);
            setUserInfo(loginData.member);
            
            console.log('LoginWithCode 성공:', { member: loginData.member });
        } catch (error) {
            console.error('LoginWithCode 실패:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const loginWithBackendCallback = async (code: string): Promise<void> => {
        try {
            setIsLoading(true);
            console.log('LoginWithBackendCallback 시작:', { code: code.substring(0, 20) + '...' });
            
            // 백엔드 콜백으로 JWT 발급
            const loginData = await authService.handleBackendCallback(code);
            
            // JWT 토큰과 사용자 정보 저장
            authService.storeToken(loginData.jwtToken);
            authService.storeUserInfo(loginData.member);
            
            // 상태 업데이트
            setIsAuthenticated(true);
            setUserInfo(loginData.member);
            
            console.log('LoginWithBackendCallback 성공:', { member: loginData.member });
        } catch (error) {
            console.error('LoginWithBackendCallback 실패:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    // 팝업으로 카카오 로그인 (백엔드 수정 없이 사용)
    const loginWithKakaoPopup = async (): Promise<LoginResponse> => {
        setIsLoading(true);
        try {
            console.log('팝업 카카오 로그인 시작');
            
            const response = await authService.handleKakaoLoginWithPopup();
            
            // JWT 토큰과 사용자 정보 저장
            authService.storeToken(response.jwtToken);
            authService.storeUserInfo(response.member);
            
            // 상태 업데이트
            setIsAuthenticated(true);
            setUserInfo(response.member);
            
            console.log('팝업 카카오 로그인 성공:', { member: response.member });
            return response;
        } catch (error) {
            console.error('팝업 카카오 로그인 실패:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    // Kakao SDK로 로그인 (가장 간단한 방법)
    const loginWithKakaoSDK = async (): Promise<LoginResponse> => {
        setIsLoading(true);
        try {
            console.log('Kakao SDK 로그인 시작');
            
            const response = await authService.loginWithKakaoSDK();
            
            // JWT 토큰과 사용자 정보 저장
            authService.storeToken(response.jwtToken);
            authService.storeUserInfo(response.member);
            
            // 상태 업데이트
            setIsAuthenticated(true);
            setUserInfo(response.member);
            
            console.log('Kakao SDK 로그인 성공:', { member: response.member });
            return response;
        } catch (error) {
            console.error('Kakao SDK 로그인 실패:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    // 더미 유저로 로그인 (개발용)
    const loginWithDummy = async (): Promise<void> => {
        try {
            setIsLoading(true);
            console.log('더미 유저 로그인 시작...');
            
            const response = await authService.loginWithDummyUser();
            
            // 상태 업데이트
            setIsAuthenticated(true);
            setUserInfo(response.member);
            
            console.log('더미 유저 로그인 성공:', { member: response.member });
        } catch (error) {
            console.error('더미 유저 로그인 실패:', error);
            throw error;
        } finally {
            setIsLoading(false);
        }
    };

    const setAuthData = (jwtToken: string, member: Member) => {
        console.log('SetAuthData 호출:', { jwtToken: jwtToken.substring(0, 20) + '...', member });
        
        // JWT 토큰과 사용자 정보 저장
        authService.storeToken(jwtToken);
        authService.storeUserInfo(member);
        
        // 상태 업데이트
        setIsAuthenticated(true);
        setUserInfo(member);
        
        console.log('SetAuthData 완료');
    };

    const logout = () => {
        console.log('Logout 호출');
        setIsAuthenticated(false);
        setUserInfo(null);
        
        // 저장된 인증 데이터 삭제
        authService.clearAuthData();
        
        console.log('Logout 완료');
    };

    const checkAuthStatus = useCallback(async (): Promise<boolean> => {
        try {
            setIsLoading(true);
            
            const jwtToken = authService.getStoredToken();
            const storedUserInfo = authService.getStoredUserInfo();

            if (!jwtToken || !storedUserInfo) {
                console.log('저장된 토큰 또는 사용자 정보 없음');
                return false;
            }

            // JWT 토큰 유효성 검증
            const isValid = await authService.validateToken(jwtToken);
            
            if (isValid) {
                setIsAuthenticated(true);
                setUserInfo(storedUserInfo);
                console.log('인증 상태 유효:', { userInfo: storedUserInfo });
                return true;
            } else {
                console.log('토큰 유효하지 않음, 로그아웃 처리');
                logout();
                return false;
            }
        } catch (error) {
            console.error('인증 상태 확인 실패:', error);
            logout();
            return false;
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        // 초기 로드 시 인증 상태 확인
        const initAuth = async () => {
            try {
                console.log('AuthContext 초기화 시작');
                await checkAuthStatus();
            } catch (error) {
                console.error('Auth 초기화 실패:', error);
                setIsAuthenticated(false);
                setUserInfo(null);
            } finally {
                setIsLoading(false);
            }
        };

        initAuth();
    }, [checkAuthStatus]);

    return (
        <AuthContext.Provider value={{
            isAuthenticated,
            userInfo,
            isLoading,
            login,
            loginWithCode,
            loginWithBackendCallback,
            loginWithKakaoPopup,
            loginWithKakaoSDK,
            loginWithDummy,
            setAuthData,
            logout,
            checkAuthStatus
        }}>
            {children}
        </AuthContext.Provider>
    );
};
