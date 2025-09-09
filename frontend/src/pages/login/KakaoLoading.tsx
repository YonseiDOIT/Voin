import { useEffect, useState, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '@/services/authService';
import ToastMessage from '@/components/ToastMessage';

const KakaoLoading = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const [error, setError] = useState<string | null>(null);
    const isProcessing = useRef(false); // 중복 실행 방지 플래그

    useEffect(() => {
        if (isProcessing.current) return;

        const code = searchParams.get('code');
        if (!code) {
            setError('카카오 인증 코드를 받지 못했습니다.');
            setTimeout(() => navigate('/login'), 2000);
            return;
        }

        isProcessing.current = true; // 처리 시작

        (async () => {
            try {
                const result = await authService.handleKakaoCallback(code);
                console.log('Kakao callback result:', result);
                if (result.type === 'Signup') {
                    console.log(result.member.nickname, result.member.profileImage);
                    navigate('/signup', { state: { kakaoNickname: result.member.nickname, kakaoProfileImage: result.member.profileImage } });
                } else {
                    localStorage.setItem('jwtToken', result.jwtToken);
                    localStorage.setItem('userInfo', JSON.stringify(result.member));
                    navigate('/home');
                }
            } catch (err) {
                if (err instanceof Error) {
                    setError(err.message);
                } else {
                    setError('카카오 로그인 처리에 실패했습니다.');
                }
                setTimeout(() => navigate('/login'), 2000);
            }
        })();
    }, [searchParams, navigate]);

    if (error) {
        return <ToastMessage message={error} />;
    }

    return (
        <div className="h-full w-full flex items-center justify-center">
            <div className="text-center">
                <div className="mb-4">로그인 처리 중...</div>
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto"></div>
            </div>
        </div>
    );
};

export default KakaoLoading;
