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
    active: boolean;
}

export interface LoginResponse {
    type: "Login";
    member: Member;
    jwtToken: string;
}

export interface KakaoVerifyRequest {
    accessToken: string;
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
                    success?: (authObj: { access_token: string }) => void;
                    fail?: (err: unknown) => void;
                }) => void;
                login: (options: {
                    success: (authObj: { access_token: string }) => void;
                    fail: (err: unknown) => void;
                }) => void;
                loginForm: (options: {
                    success: (authObj: { access_token: string }) => void;
                    fail: (err: unknown) => void;
                }) => void;
                getAccessToken: () => string | null;
            };
        };
    }
}

class AuthService {
    private baseURL = '/api';

    // 더미 유저 데이터로 로그인 (개발용)
    async loginWithDummyUser(): Promise<LoginResponse> {
        try {
            console.log('더미 유저 로그인 시작...');
            
            // 더미 유저 데이터 생성
            const dummyUser: Member = {
                createdAt: new Date().toISOString(),
                updatedAt: new Date().toISOString(),
                id: 'dummy-user-001',
                kakaoId: 'dummy-kakao-123456789',
                nickname: '테스트 사용자',
                profileImage: 'https://via.placeholder.com/100x100?text=User',
                friendCode: 'DUMMY001',
                isActive: true,
                active: true
            };

            // 더미 JWT 토큰 (실제로는 백엔드에서 발급되지만, 개발용으로 생성)
            const dummyJwtToken = 'dummy.jwt.token.' + Date.now();

            const loginResponse: LoginResponse = {
                type: "Login",
                member: dummyUser,
                jwtToken: dummyJwtToken
            };

            console.log('더미 유저 로그인 성공:', loginResponse);
            
            // 로컬 스토리지에 저장
            this.storeToken(dummyJwtToken);
            this.storeUserInfo(dummyUser);

            return loginResponse;
        } catch (error) {
            console.error('더미 유저 로그인 실패:', error);
            throw error;
        }
    }

    // Kakao JavaScript SDK를 사용한 로그인
    async loginWithKakaoSDK(): Promise<LoginResponse> {
        try {
            console.log('Kakao SDK 로그인 시작...');
            
            // Kakao SDK가 로드되었는지 확인
            if (typeof window === 'undefined') {
                throw new Error('브라우저 환경이 아닙니다.');
            }
            
            if (!window.Kakao) {
                throw new Error('Kakao SDK가 로드되지 않았습니다.');
            }
            
            console.log('Kakao SDK 발견:', window.Kakao);
            console.log('Kakao SDK 구조:', Object.keys(window.Kakao));
            console.log('Kakao.Auth 구조:', window.Kakao.Auth ? Object.keys(window.Kakao.Auth) : 'Auth 없음');
            
            // SDK 초기화
            if (!window.Kakao.isInitialized()) {
                console.log('Kakao SDK 초기화 중...');
                window.Kakao.init('2ac62bc43a0718bcd65126b5dbae3843');
                console.log('Kakao SDK 초기화 완료');
            } else {
                console.log('Kakao SDK 이미 초기화됨');
            }

            return new Promise((resolve, reject) => {
                // 여러 가지 방법을 시도해봅시다
                
                // 방법 1: loginForm 시도
                if (window.Kakao.Auth.loginForm) {
                    console.log('Kakao.Auth.loginForm 호출...');
                    
                    window.Kakao.Auth.loginForm({
                        success: async (authObj: { access_token: string }) => {
                            try {
                                console.log('카카오 로그인 성공! 액세스 토큰 받음:', authObj.access_token.substring(0, 20) + '...');
                                
                                // 액세스 토큰을 백엔드로 전달하여 JWT 발급
                                console.log('백엔드 API 호출 중...');
                                const response = await this.verifyKakaoToken(authObj.access_token);
                                console.log('JWT 발급 성공:', response);
                                
                                resolve(response);
                            } catch (error) {
                                console.error('백엔드 API 호출 실패:', error);
                                reject(error);
                            }
                        },
                        fail: (err: unknown) => {
                            console.error('카카오 로그인 실패:', err);
                            reject(new Error('사용자가 로그인을 취소했거나 카카오 로그인에 실패했습니다.'));
                        }
                    });
                    return;
                }
                
                // 방법 2: authorize 시도
                if (window.Kakao.Auth.authorize) {
                    console.log('Kakao.Auth.authorize 호출...');
                    
                    window.Kakao.Auth.authorize({
                        redirectUri: 'http://localhost:5173/auth/kakao/callback'
                    });
                    return;
                }
                
                // 방법 3: 기존 login 시도 (혹시 존재할 경우)
                if (window.Kakao.Auth.login) {
                    console.log('Kakao.Auth.login 호출...');
                    
                    window.Kakao.Auth.login({
                        success: async (authObj: { access_token: string }) => {
                            try {
                                console.log('카카오 로그인 성공! 액세스 토큰 받음:', authObj.access_token.substring(0, 20) + '...');
                                
                                // 액세스 토큰을 백엔드로 전달하여 JWT 발급
                                console.log('백엔드 API 호출 중...');
                                const response = await this.verifyKakaoToken(authObj.access_token);
                                console.log('JWT 발급 성공:', response);
                                
                                resolve(response);
                            } catch (error) {
                                console.error('백엔드 API 호출 실패:', error);
                                reject(error);
                            }
                        },
                        fail: (err: unknown) => {
                            console.error('카카오 로그인 실패:', err);
                            reject(new Error('사용자가 로그인을 취소했거나 카카오 로그인에 실패했습니다.'));
                        }
                    });
                    return;
                }
                
                // 모든 방법이 실패한 경우
                reject(new Error('Kakao SDK에서 지원하는 로그인 메소드를 찾을 수 없습니다.'));
            });
        } catch (error) {
            console.error('Kakao SDK 로그인 실패:', error);
            throw error;
        }
    }

    // 백엔드 API를 사용한 카카오 로그인 URL 조회
    async getKakaoLoginUrl(): Promise<string> {
        try {
            console.log('카카오 로그인 URL 조회 중...');
            
            const response = await fetch('/api/auth/kakao/url', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data: ApiResponse<string> = await response.json();
            console.log('카카오 로그인 URL 조회 응답:', data);

            if (data.success && data.data) {
                return data.data;
            } else {
                throw new Error(data.message || '카카오 로그인 URL 조회 실패');
            }
        } catch (error) {
            console.error('카카오 로그인 URL 조회 실패:', error);
            throw error;
        }
    }

    // 백엔드 카카오 콜백에서 JWT 토큰 추출
    parseBackendCallbackResponse(url: string): { 
        jwtToken?: string; 
        nickname?: string; 
        isNewMember?: boolean; 
        kakaoAccessToken?: string;
        error?: string;
        message?: string;
    } {
        try {
            const urlObj = new URL(url);
            const params = urlObj.searchParams;
            
            return {
                jwtToken: params.get('access_token') || undefined,
                nickname: params.get('nickname') ? decodeURIComponent(params.get('nickname')!) : undefined,
                isNewMember: params.get('is_new_member') === 'true',
                kakaoAccessToken: params.get('kakao_access_token') || undefined,
                error: params.get('error') || undefined,
                message: params.get('message') ? decodeURIComponent(params.get('message')!) : undefined
            };
        } catch (error) {
            console.error('URL 파싱 실패:', error);
            return { error: 'URL_PARSE_ERROR', message: 'URL 파싱에 실패했습니다.' };
        }
    }

    // JWT 토큰으로 사용자 정보 생성 (임시)
    createMemberFromJWT(_jwtToken: string, nickname?: string): Member {
        // JWT 토큰에서 실제로는 사용자 정보를 디코딩해야 하지만,
        // 현재는 임시로 기본 정보를 생성
        const now = new Date().toISOString();
        return {
            createdAt: now,
            updatedAt: now,
            id: 'temp-id-' + Date.now(),
            kakaoId: 'temp-kakao-id',
            nickname: nickname || '사용자',
            profileImage: '',
            friendCode: 'temp-friend-code',
            isActive: true,
            active: true
        };
    }

    // 백엔드 카카오 콜백 처리 (임시 해결책)
    // 백엔드 콜백을 직접 호출하여 처리
    async handleBackendCallback(code: string): Promise<LoginResponse> {
        try {
            console.log('백엔드 콜백 처리 시작:', code.substring(0, 20) + '...');
            
            // 백엔드 콜백 URL 구성
            const backendCallbackUrl = `http://localhost:8080/auth/kakao/callback?code=${code}`;
            
            // fetch로 백엔드 콜백 호출 (리다이렉트 수동 처리)
            const response = await fetch(backendCallbackUrl, {
                method: 'GET',
                credentials: 'include',
                redirect: 'manual' // 리다이렉트를 수동으로 처리
            });

            // 리다이렉트 응답인 경우
            if (response.status === 302 || response.status === 301) {
                const location = response.headers.get('Location');
                console.log('백엔드에서 리다이렉트 URL 받음:', location);
                
                if (location) {
                    // 리다이렉트 URL에서 파라미터 추출
                    const callbackData = this.parseBackendCallbackResponse(location);
                    
                    if (callbackData.error) {
                        throw new Error(callbackData.message || '로그인 실패');
                    }

                    if (callbackData.jwtToken) {
                        // JWT 토큰으로 LoginResponse 구성
                        const member = this.createMemberFromJWT(callbackData.jwtToken, callbackData.nickname);
                        
                        return {
                            type: "Login",
                            member,
                            jwtToken: callbackData.jwtToken
                        };
                    }
                    
                    if (callbackData.isNewMember && callbackData.kakaoAccessToken) {
                        // 신규 회원의 경우는 별도 처리 필요
                        throw new Error('신규 회원은 회원가입 페이지로 이동해야 합니다.');
                    }
                }
            }

            // 직접 응답인 경우 (JSON 형태로 응답하는 경우)
            if (response.ok) {
                const data = await response.json();
                if (data.success && data.data) {
                    return data.data;
                }
            }

            throw new Error('백엔드 콜백 처리 실패');
        } catch (error) {
            console.error('백엔드 콜백 처리 실패:', error);
            throw error;
        }
    }

    // 새 창에서 카카오 로그인 처리 (가장 안전한 방법)
    async handleKakaoLoginWithPopup(): Promise<LoginResponse> {
        try {
            // 카카오 로그인 URL 조회
            const kakaoUrl = await this.getKakaoLoginUrl();
            
            // 새 창에서 카카오 로그인 진행
            return new Promise((resolve, reject) => {
                const popup = window.open(
                    kakaoUrl,
                    'kakaoLogin',
                    'width=500,height=600,scrollbars=yes,resizable=yes'
                );

                if (!popup) {
                    reject(new Error('팝업이 차단되었습니다. 팝업 차단을 해제해주세요.'));
                    return;
                }

                // 팝업 URL 변화 감지
                const checkForCallback = setInterval(() => {
                    try {
                        const popupUrl = popup.location.href;
                        
                        // 백엔드 콜백 URL인지 확인
                        if (popupUrl.includes('/auth/kakao/callback')) {
                            clearInterval(checkForCallback);
                            
                            // URL에서 파라미터 추출
                            const url = new URL(popupUrl);
                            const code = url.searchParams.get('code');
                            const error = url.searchParams.get('error');
                            
                            popup.close();
                            
                            if (error) {
                                reject(new Error('카카오 로그인 오류: ' + error));
                                return;
                            }
                            
                            if (code) {
                                // 카카오 코드로 JWT 토큰 발급
                                this.verifyKakaoCode(code)
                                    .then(resolve)
                                    .catch(reject);
                            } else {
                                reject(new Error('인증 코드를 받지 못했습니다.'));
                            }
                        }
                        
                        // 프론트엔드 콜백 URL인지 확인 (이미 처리된 경우)
                        if (popupUrl.includes('localhost:5173/auth/kakao/callback')) {
                            clearInterval(checkForCallback);
                            
                            const url = new URL(popupUrl);
                            const jwtToken = url.searchParams.get('access_token');
                            const nickname = url.searchParams.get('nickname');
                            const error = url.searchParams.get('error');
                            
                            popup.close();
                            
                            if (error) {
                                reject(new Error('로그인 처리 오류: ' + url.searchParams.get('message')));
                                return;
                            }
                            
                            if (jwtToken) {
                                const member = this.createMemberFromJWT(jwtToken, nickname || undefined);
                                resolve({
                                    type: "Login",
                                    member,
                                    jwtToken
                                });
                            } else {
                                reject(new Error('JWT 토큰을 받지 못했습니다.'));
                            }
                        }
                        
                    } catch {
                        // 크로스 도메인 오류는 무시 (아직 카카오 페이지에 있음)
                        if (popup.closed) {
                            clearInterval(checkForCallback);
                            reject(new Error('로그인이 취소되었습니다.'));
                        }
                    }
                }, 1000);

                // 타임아웃 설정 (5분)
                setTimeout(() => {
                    clearInterval(checkForCallback);
                    if (!popup.closed) {
                        popup.close();
                    }
                    reject(new Error('로그인 시간이 초과되었습니다.'));
                }, 300000);
            });
        } catch (error) {
            console.error('팝업 카카오 로그인 실패:', error);
            throw error;
        }
    }
    
    async verifyKakaoCode(code: string): Promise<LoginResponse> {
        try {
            console.log('카카오 코드 검증 시작:', { code: code.substring(0, 20) + '...' });
            
            // 1. 먼저 인증 코드를 액세스 토큰으로 변환
            const accessToken = await this.getKakaoAccessToken(code);
            
            // 2. 액세스 토큰을 백엔드에 전달해서 JWT 발급
            const response = await fetch('/api/auth/kakao/verify', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ 
                    accessToken: accessToken
                }),
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('카카오 토큰 검증 응답:', data);

            if (data.success && data.data) {
                return data.data;
            } else {
                throw new Error(data.message || '카카오 토큰 검증 실패');
            }
        } catch (error) {
            console.error('카카오 코드 검증 실패:', error);
            throw error;
        }
    }

    // 카카오 인증 코드를 액세스 토큰으로 변환
    private async getKakaoAccessToken(code: string): Promise<string> {
        try {
            console.log('카카오 액세스 토큰 요청 중...');
            
            const tokenResponse = await fetch('https://kauth.kakao.com/oauth/token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    grant_type: 'authorization_code',
                    client_id: '2ac62bc43a0718bcd65126b5dbae3843', // JavaScript 키
                    redirect_uri: 'http://localhost:5173/auth/kakao/callback',
                    code: code,
                }).toString(),
            });

            if (!tokenResponse.ok) {
                throw new Error(`카카오 토큰 요청 실패: ${tokenResponse.status}`);
            }

            const tokenData = await tokenResponse.json();
            console.log('카카오 액세스 토큰 획득 성공');

            if (tokenData.access_token) {
                return tokenData.access_token;
            } else {
                throw new Error('액세스 토큰을 받지 못했습니다.');
            }
        } catch (error) {
            console.error('카카오 액세스 토큰 요청 실패:', error);
            throw error;
        }
    }

    // 카카오 액세스 토큰으로 JWT 발급
    async verifyKakaoToken(accessToken: string): Promise<LoginResponse> {
        try {
            console.log('백엔드 카카오 토큰 검증 시작...');
            console.log('API 호출 URL:', `${this.baseURL}/auth/kakao/verify`);
            console.log('액세스 토큰:', accessToken.substring(0, 20) + '...');
            
            const response = await fetch(`${this.baseURL}/auth/kakao/verify`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({ accessToken }),
            });

            console.log('API 응답 상태:', response.status, response.statusText);

            if (!response.ok) {
                const errorText = await response.text();
                console.error('API 응답 에러:', errorText);
                throw new Error(`백엔드 API 호출 실패: ${response.status} ${errorText}`);
            }

            const data: ApiResponse<LoginResponse> = await response.json();
            console.log('API 응답 데이터:', data);

            if (data.success && data.data) {
                console.log('JWT 토큰 검증 성공');
                return data.data;
            } else {
                console.error('API 응답 실패:', data.message);
                throw new Error(data.message || '로그인 처리 실패');
            }
        } catch (error) {
            console.error('카카오 토큰 검증 실패:', error);
            if (error instanceof Error && error.message.includes('fetch')) {
                throw new Error('네트워크 연결 문제입니다. 인터넷 연결을 확인해주세요.');
            }
            throw error;
        }
    }

    // JWT 토큰 유효성 검증
    async validateToken(jwtToken: string): Promise<boolean> {
        try {
            // 더미 토큰인 경우 항상 유효로 처리
            if (jwtToken.startsWith('dummy.jwt.token.')) {
                console.log('더미 토큰 검증: 유효');
                return true;
            }

            const response = await fetch(`${this.baseURL}/auth/validate`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${jwtToken}`,
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
            });

            if (!response.ok) {
                return false;
            }

            const data: ApiResponse<boolean> = await response.json();
            return data.success && data.data === true;
        } catch (error) {
            console.error('토큰 검증 실패:', error);
            return false;
        }
    }

    // localStorage에서 JWT 토큰 조회
    getStoredToken(): string | null {
        return localStorage.getItem('jwtToken');
    }

    // JWT 토큰 저장
    storeToken(jwtToken: string): void {
        localStorage.setItem('jwtToken', jwtToken);
    }

    // 사용자 정보 저장
    storeUserInfo(member: Member): void {
        localStorage.setItem('userInfo', JSON.stringify(member));
    }

    // 저장된 사용자 정보 조회
    getStoredUserInfo(): Member | null {
        try {
            const userInfo = localStorage.getItem('userInfo');
            return userInfo ? JSON.parse(userInfo) : null;
        } catch {
            return null;
        }
    }

    // 로그아웃 - 저장된 정보 모두 삭제
    clearAuthData(): void {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userInfo');

        // 기존 구버전 데이터도 정리
        localStorage.removeItem('accessToken');
        localStorage.removeItem('nickname');
        localStorage.removeItem('profileImage');

        // 추가 정리 - 혹시 다른 키들도 정리
        Object.keys(localStorage).forEach(key => {
            if (key.startsWith('auth_') || key.startsWith('user_') || key.startsWith('kakao_')) {
                localStorage.removeItem(key);
            }
        });
    }
}

export const authService = new AuthService();
