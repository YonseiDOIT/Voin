// src/pages/auth/KakaoCallback.tsx
import { useEffect, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { authService } from '@/services/authService';

const KakaoCallback = () => {
    const navigate = useNavigate();
    const { search } = useLocation();
    const { actions } = useAuthStore();
    const hasRun = useRef(false); // StrictMode 두 번 실행 방지

    useEffect(() => {
        if (hasRun.current) return;
        hasRun.current = true;

        const params = new URLSearchParams(search);
        const token = params.get('token'); // 서버 콜백 방식: ?token=...
        const type = (params.get('type') as 'Login' | 'Signup' | null) ?? null;

        if (!token) {
            const error = params.get('error') ?? 'unknown';
            console.error('카카오 로그인 실패: token missing', error);
            alert('카카오 로그인에 실패했습니다. 다시 시도해주세요.');
            navigate('/login', { replace: true });
            return;
        }

        (async () => {
            try {
                // 1) 토큰 저장 (member는 이후에 채우거나 /me로 동기화)
                authService.storeAuthData(token, null as any);

                // 2) 스토어 인증 상태 동기화
                if (typeof actions.checkAuthStatus === 'function') {
                    await actions.checkAuthStatus();
                } else if (typeof actions.initialize === 'function') {
                    await actions.initialize();
                }

                // 3) 최종 목적지 결정: sessionStorage > type 파라미터 > 기본값(/home)
                const saved = sessionStorage.getItem('postLoginRedirect');
                const fallback = type === 'Signup' ? '/signup' : '/home';
                const to = saved || fallback;
                sessionStorage.removeItem('postLoginRedirect');

                // 4) URL 정리 후 이동(선택)
                window.history.replaceState({}, '', '/api/auth/kakao/callback');
                // isNew 로 신규 가입 여부 확실하게 표시함
                const isNew = type === 'Signup';
                sessionStorage.setItem('isNewMember', isNew ? '1' : '0');

                navigate(to, { replace: true });
            } catch (e) {
                console.error('콜백 처리 중 오류:', e);
                alert('로그인 처리에 실패했습니다.');
                navigate('/login', { replace: true });
            }
        })();
    }, [search, navigate, actions]);

    return (
        <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
            <p className="mt-4 text-lg font-semibold text-gray-700">카카오 로그인 처리 중입니다...</p>
        </div>
    );
};

export default KakaoCallback;
