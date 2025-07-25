import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { authService } from "../../services/authService";

import Carousel from "../../components/home/Carousel";

import KakaoImage from "../../assets/svgs/login/kakao.svg?react";

const Login = () => {
    const navigate = useNavigate();
    const { isAuthenticated, isLoading, loginWithKakaoSDK, loginWithDummy } = useAuth();
    const [isLoginLoading, setIsLoginLoading] = useState(false);

    useEffect(() => {
        console.log('Login page useEffect - isAuthenticated:', isAuthenticated);
        
        // 로딩 중이면 대기
        if (isLoading) {
            return;
        }
        
        // 이미 로그인된 사용자는 홈으로 리다이렉트
        if (isAuthenticated) {
            console.log('User is authenticated, redirecting to home');
            navigate('/home', { replace: true });
            return;
        }

        console.log('User is not authenticated, showing login page');
        
        // 혹시 남아있는 구버전 인증 정보 정리
        authService.clearAuthData();
        
        // 로그인 페이지에서는 배경 설정
        document.body.style.background = "#D9D9D9";

        return () => {
            document.body.style.background = '#F7F7F8';
        };
    }, [isAuthenticated, isLoading, navigate]);

    const carouselItems = [
        <div className="w-full px-5 pb-4 text-center text-2xl font-semibold">온보딩에 들어갈<br />내용 입니다 1</div>,
        <div className="w-full px-5 pb-4 text-center text-2xl font-semibold">온보딩에 들어갈<br />내용 입니다 2</div>,
        <div className="w-full px-5 pb-4 text-center text-2xl font-semibold">온보딩에 들어갈<br />내용 입니다 3</div>
    ];

    const handleKakaoLogin = async () => {
        if (isLoginLoading) return; // 중복 클릭 방지
        
        try {
            setIsLoginLoading(true);
            console.log('카카오 로그인 시작...');
            
            // Kakao SDK 로드 확인
            if (typeof window.Kakao === 'undefined') {
                throw new Error('Kakao SDK가 로드되지 않았습니다. 페이지를 새로고침해보세요.');
            }
            
            console.log('Kakao SDK 상태:', {
                loaded: typeof window.Kakao !== 'undefined',
                initialized: window.Kakao?.isInitialized?.()
            });
            
            await loginWithKakaoSDK();
            
            console.log('카카오 로그인 성공, 홈으로 이동');
            navigate('/home', { replace: true });
        } catch (error) {
            console.error('카카오 로그인 오류 상세:', error);
            
            // 에러 타입에 따라 다른 메시지 표시
            let errorMessage = '카카오 로그인에 실패했습니다.';
            if (error instanceof Error) {
                if (error.message.includes('Kakao SDK')) {
                    errorMessage = 'Kakao SDK 로딩 문제입니다. 페이지를 새로고침해보세요.';
                } else if (error.message.includes('사용자가 취소')) {
                    errorMessage = '로그인이 취소되었습니다.';
                } else {
                    errorMessage = `로그인 실패: ${error.message}`;
                }
            }
            
            alert(errorMessage);
        } finally {
            setIsLoginLoading(false);
        }
    };

    // 더미 로그인 함수 (개발용)
    const handleDummyLogin = async () => {
        if (isLoginLoading) return; // 중복 클릭 방지
        
        try {
            setIsLoginLoading(true);
            console.log('더미 로그인 시작...');
            
            await loginWithDummy();
            
            console.log('더미 로그인 성공, 홈으로 이동');
            navigate('/home', { replace: true });
        } catch (error) {
            console.error('더미 로그인 오류:', error);
            alert('더미 로그인에 실패했습니다.');
        } finally {
            setIsLoginLoading(false);
        }
    };

    return (
        <div className="h-full w-full flex flex-col">
            <div className="fixed bottom-0 bg-white w-full pt-8 pb-4 flex flex-col"
                style={{ paddingBottom: 'env(safe-area-inset-bottom)' }}>
                <Carousel slides={carouselItems} />

                <div className="w-full px-6 mt-28 pb-4 space-y-4">
                    {/* 카카오 로그인 버튼 */}
                    <button
                        className={`w-full py-4 gap-x-4 flex flex-row items-center justify-center bg-[#FEE500] rounded-full transition-opacity ${
                            isLoginLoading ? 'opacity-70 cursor-not-allowed' : 'hover:opacity-90'
                        }`}
                        onClick={handleKakaoLogin}
                        disabled={isLoginLoading}
                    >
                        {isLoginLoading ? (
                            <>
                                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-[#191919]"></div>
                                <div className="body-n text-[#191919] font-semibold">
                                    로그인 중...
                                </div>
                            </>
                        ) : (
                            <>
                                <KakaoImage className="" />
                                <div className="body-n text-[#191919] font-semibold">
                                    카카오로 계속하기
                                </div>
                            </>
                        )}
                    </button>

                    {/* 더미 로그인 버튼 (개발용) */}
                    <div className="space-y-2">
                        <div className="text-center text-sm text-gray-500">
                            개발 및 테스트용 (실제 서비스에서는 제거됩니다)
                        </div>
                        <button
                            className={`w-full py-4 gap-x-4 flex flex-row items-center justify-center bg-gray-300 rounded-full transition-opacity border-2 border-dashed border-gray-400 ${
                                isLoginLoading ? 'opacity-70 cursor-not-allowed' : 'hover:opacity-90'
                            }`}
                            onClick={handleDummyLogin}
                            disabled={isLoginLoading}
                        >
                            <div className="body-n text-[#191919] font-semibold">
                                🚀 더미 유저로 로그인 (개발용)
                            </div>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Login;