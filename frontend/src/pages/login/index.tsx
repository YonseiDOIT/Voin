import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

import Carousel from "../../components/home/Carousel";

import KakaoImage from "../../assets/svgs/login/kakao.svg?react";

const Login = () => {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    useEffect(() => {
        console.log('Login page useEffect - isAuthenticated:', isAuthenticated);
        
        // 이미 로그인된 사용자는 홈으로 리다이렉트
        if (isAuthenticated) {
            console.log('User is authenticated, redirecting to home');
            navigate('/home', { replace: true });
            return;
        }

        console.log('User is not authenticated, showing login page');
        
        // 혹시 남아있는 인증 정보 정리
        const hasOldToken = localStorage.getItem('accessToken');
        if (hasOldToken) {
            console.log('Found old token, clearing localStorage');
            localStorage.removeItem('accessToken');
            localStorage.removeItem('nickname');
            localStorage.removeItem('profileImage');
        }
        
        // 로그인 페이지에서는 배경 설정
        document.body.style.background = "#D9D9D9";

        return () => {
            document.body.style.background = '#F7F7F8';
        };
    }, [isAuthenticated, navigate]);

    const carouselItems = [
        <div className="w-full px-5 pb-4 text-center text-2xl font-semibold">온보딩에 들어갈<br />내용 입니다 1</div>,
        <div className="w-full px-5 pb-4 text-center text-2xl font-semibold">온보딩에 들어갈<br />내용 입니다 2</div>,
        <div className="w-full px-5 pb-4 text-center text-2xl font-semibold">온보딩에 들어갈<br />내용 입니다 3</div>
    ];

    const handleKakaoLogin = async () => {
        try {
            const response = await fetch('/auth/kakao/url', {
                method: 'GET',
                credentials: 'include',
            });

            if (!response.ok) {
                throw new Error('카카오 로그인 URL 요청 실패');
            }

            const data = await response.json();
            
            if (data.success && data.data) {
                // 받아온 카카오 인증 URL로 리다이렉트
                window.location.href = data.data;
            } else {
                throw new Error(data.message || '카카오 로그인 URL 생성 실패');
            }
        } catch (error) {
            console.error('카카오 로그인 오류:', error);
            alert('카카오 로그인에 실패했습니다. 다시 시도해주세요.');
        }
    };

    return (
        <div className="h-full w-full flex flex-col">
            <div className="fixed bottom-0 bg-white w-full pt-8 pb-4 flex flex-col"
                style={{ paddingBottom: 'env(safe-area-inset-bottom)' }}>
                <Carousel slides={carouselItems} />

                <div className="w-full px-6 mt-28 pb-4">
                    <button
                        className="w-full py-4 gap-x-4 flex flex-row items-center justify-center bg-[#FEE500] rounded-full disabled:opacity-50 disabled:cursor-not-allowed"
                        onClick={handleKakaoLogin}
                    >
                        <KakaoImage className="" />
                        <div className="body-n text-[#191919] font-semibold">
                            카카오로 계속하기
                        </div>
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Login;