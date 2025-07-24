import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const KakaoCallback = () => {
    const navigate = useNavigate();

    useEffect(() => {
        // Kakao SDK 사용 시에는 이 페이지가 필요하지 않음
        // 홈으로 리다이렉트
        navigate('/', { replace: true });
    }, [navigate]);

    return (
        <div className="flex items-center justify-center min-h-screen">
            <div className="text-center">
                <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                <p>로그인 처리 중...</p>
            </div>
        </div>
    );
};

export default KakaoCallback;
