import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "@/store/useAuthStore";
import useEmblaCarousel from 'embla-carousel-react';
import type { EmblaCarouselType } from 'embla-carousel';

import KakaoImage from "@/assets/svgs/login/kakao.svg?react";

// onBoard 이미지
import onBoardImage1 from '@/assets/images/onBoard/onboarding_01.png';
import onBoardImage2 from '@/assets/images/onBoard/onboarding_02.png';
import onBoardImage3 from '@/assets/images/onBoard/onboarding_03.png';
import onBoardImage4 from '@/assets/images/onBoard/onboarding_04.png';

const onBoardImages = [onBoardImage1, onBoardImage2, onBoardImage3, onBoardImage4];

const onBoardTexts = [
    <div><span>일상 속 순간이 장점이 되는</span><br /><span className="text-VB-40">Voin에 오신 걸 환영해요</span></div>,
    <div><span>일상 속 순간을 기록하고</span><br /><span className="text-VB-40">나와 친구의 장점을 발견해요</span></div>,
    <div><span>일상 속 순간이 장점이 되는</span><br /><span className="text-VB-40">Voin에 오신 걸 환영해요</span></div>,
    <div><span>일상 속 순간이 장점이 되는</span><br /><span className="text-VB-40">Voin에 오신 걸 환영해요</span></div>,
];

const Login = () => {
    const navigate = useNavigate();
    const { isAuthenticated, isLoading, actions, hasInitialized } = useAuthStore();
    const [isLoginLoading, setIsLoginLoading] = useState(false);

    const [emblaRef, emblaApi] = useEmblaCarousel({ align: 'center', loop: true });
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [scrollSnaps, setScrollSnaps] = useState<number[]>([]);

    // 캐러셀 초기화
    useEffect(() => {
        if (!emblaApi) return;
        const onUpdate = (api: EmblaCarouselType) => {
            setScrollSnaps(api.scrollSnapList());
            setSelectedIndex(api.selectedScrollSnap());
        };
        emblaApi.on('select', onUpdate);
        emblaApi.on('reInit', onUpdate);
        onUpdate(emblaApi);
        return () => {
            emblaApi.off('select', onUpdate);
            emblaApi.off('reInit', onUpdate);
        };
    }, [emblaApi]);

    // 스토어 초기화(앱이 App.tsx에서 initialize 호출 중이면 hasInitialized로 대기)
    useEffect(() => {
        if (!hasInitialized) {
            actions.initialize();
        }
    }, [hasInitialized, actions]);

    // 이미 로그인 상태면 홈으로
    useEffect(() => {
        if (!hasInitialized || isLoading) return;
        if (isAuthenticated) {
            navigate('/home', { replace: true });
        }
    }, [hasInitialized, isLoading, isAuthenticated, navigate]);

    const imageCarouselItems = onBoardImages.map((imageSrc, index) => (
        <img key={index} src={imageSrc} alt={`onboarding image ${index + 1}`} className="max-h-full w-auto object-contain" />
    ));

    const handleKakaoLogin = () => {
        if (isLoginLoading) return;
        setIsLoginLoading(true);
        try {
            // 스토어 액션을 사용 (리다이렉트 트리거만 함)
            actions.loginWithKakao();
            // 이후 카카오 → 백엔드 → 프론트 콜백으로 이동하므로 버튼 로딩은 유지해도 무방
        } catch (error) {
            console.error('카카오 로그인 시작 오류:', error);
            alert('카카오 로그인 과정에 문제가 발생했습니다.');
            setIsLoginLoading(false);
        }
    };

    // 초기화/검증 중에는 로딩 UI
    if (!hasInitialized || isLoading) {
        return (
            <div className="w-full h-screen flex items-center justify-center">
                <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-gray-800" />
            </div>
        );
    }

    return (
        <div className="fixed inset-0 flex flex-col justify-end bg-transparent">
            {/* 상단 이미지 캐러셀 영역 */}
            <div className="w-full " ref={emblaRef}>
                <div className="flex">
                    {imageCarouselItems.map((slide, index) => (
                        <div className="flex-shrink-0 w-full flex items-center justify-center" key={index}>
                            {slide}
                        </div>
                    ))}
                </div>
            </div>

            {/* 하단 컨텐츠 영역 */}
            <div className="bg-white w-full pt-6 flex flex-col rounded-t-2xl"
                 style={{ paddingBottom: 'calc(1rem + env(safe-area-inset-bottom))' }}>

                {/* 텍스트 표시 영역 */}
                <div className="w-full px-5 h-24 flex items-center justify-center text-center text-2xl font-semibold">
                    {onBoardTexts[selectedIndex]}
                </div>

                {/* Dots */}
                <div className="flex flex-wrap justify-center items-center mt-2">
                    {scrollSnaps.map((_, index) => (
                        <button
                            key={index}
                            onClick={() => emblaApi?.scrollTo(index)}
                            className={`w-1.5 h-1.5 rounded-full bg-gray-200 mx-1 transition-all duration-200 ${
                                index === selectedIndex ? 'w-5 !bg-cyan-300' : ''
                            }`}
                        />
                    ))}
                </div>

                {/* 카카오 로그인 버튼 */}
                <div className="w-full px-6 mt-25 pb-2">
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
                                <div className="body-n text-[#191919] font-semibold">로그인 중...</div>
                            </>
                        ) : (
                            <>
                                <KakaoImage />
                                <div className="body-n text-[#191919] font-semibold">카카오로 계속하기</div>
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default Login;
