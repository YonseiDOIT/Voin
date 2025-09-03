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
        const type = params.get('type') as 'Login' | 'Signup' | null; // 선택적

        if (!token) {
            const error = params.get('error') ?? 'unknown';
            console.error('카카오 로그인 실패: token missing', error);
            alert('카카오 로그인에 실패했습니다. 다시 시도해주세요.');
            navigate('/login', { replace: true });
            return;
        }

        (async () => {
            try {
                // 1) 토큰 저장 (member는 이후 /api/me 등으로 조회 권장)
                authService.storeAuthData(token, null as any);

                // 2) 스토어가 자체 로직으로 인증상태 재계산하도록 호출
                //    (스토어 타입에 setAuthData가 없으므로 checkAuthStatus 또는 initialize 사용)
                if (typeof actions.checkAuthStatus === 'function') {
                    await actions.checkAuthStatus();
                } else if (typeof actions.initialize === 'function') {
                    actions.initialize();
                }

                // 3) URL 정리(쿼리 제거) — 선택
                window.history.replaceState({}, '', '/auth/callback');

                // 4) 필요시 간단 검증 후 라우팅
                // const ok = await authService.validateToken(token);
                // if (!ok) { authService.logout(); navigate('/login', { replace: true }); return; }

                navigate(type === 'Signup' ? '/signup' : '/home', { replace: true });
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
            <p className="mt-4 text-lg font-semibold text-gray-700">
                카카오 로그인 처리 중입니다...
            </p>
        </div>
    );
};

export default KakaoCallback;
