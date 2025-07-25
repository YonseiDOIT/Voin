import { useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { authService } from '@/services/authService';

const KakaoCallback = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { actions } = useAuthStore();
    const hasRun = useRef(false); // 실행 여부를 추적하는 ref

    useEffect(() => {
        // Strict Mode에서 두 번 실행되는 것을 방지
        if (hasRun.current) {
            return;
        }
        hasRun.current = true;

        const searchParams = new URLSearchParams(location.search);
        const code = searchParams.get('code');

        if (code) {
            authService.handleKakaoCallback(code)
                .then((response) => {
                    // 디버깅: 백엔드에서 받은 응답 전체를 확인합니다.
                    console.log('백엔드 응답:', response);

                    // Zustand store를 업데이트합니다.
                    actions.setAuthData(response.jwtToken, response.member);
                    // 신규 회원이면 프로필 등록, 기존 회원이면 홈으로 이동
                    if (response.type === 'Signup') {
                        navigate('/signup', { replace: true });
                    } else {
                        navigate('/home', { replace: true });
                    }
                })
                .catch((error) => {
                    console.error('카카오 로그인 콜백 처리 실패:', error);
                    alert('로그인에 실패했습니다. 로그인 페이지로 돌아갑니다.');
                    navigate('/login', { replace: true });
                });
        } else {
            // 코드가 없는 경우 (에러 등)
            const error = searchParams.get('error');
            console.error('카카오 로그인 실패:', error);
            alert('카카오 로그인에 동의가 필요합니다. 로그인 페이지로 돌아갑니다.');
            navigate('/login', { replace: true });
        }
    }, [location, navigate, actions]);

    return (
        <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="mt-4 text-lg font-semibold text-gray-700">
                카카오 로그인 처리 중입니다...
            </p>
        </div>
    );
};

export default KakaoCallback;