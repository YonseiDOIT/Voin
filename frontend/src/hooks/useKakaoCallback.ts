import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { authService } from '@/services/authService';

interface CallbackData {
    jwtToken?: string;
    nickname?: string;
    isNewMember?: boolean;
    kakaoAccessToken?: string;
    error?: string;
    message?: string;
}

export const useKakaoCallback = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { loginWithCode, setAuthData } = useAuthStore((state) => state.actions);
    const [isProcessing, setIsProcessing] = useState(false);

    useEffect(() => {
        const processCallback = async () => {
            // 이미 처리 중이면 중복 실행 방지
            if (isProcessing) return;

            // URL에서 카카오 콜백 데이터 확인
            const callbackData: CallbackData = {
                jwtToken: searchParams.get('access_token') || undefined,
                nickname: searchParams.get('nickname') ? decodeURIComponent(searchParams.get('nickname')!) : undefined,
                isNewMember: searchParams.get('is_new_member') === 'true',
                kakaoAccessToken: searchParams.get('kakao_access_token') || undefined,
                error: searchParams.get('error') || undefined,
                message: searchParams.get('message') ? decodeURIComponent(searchParams.get('message')!) : undefined
            };

            // 카카오 인증 코드도 확인
            const code = searchParams.get('code');

            console.log('카카오 콜백 데이터:', { callbackData, code });

            // 어떤 파라미터도 없으면 처리하지 않음
            if (!callbackData.jwtToken && !callbackData.error && !callbackData.isNewMember && !code) {
                return;
            }

            // 백엔드에서 처리된 결과가 있는 경우
            if (callbackData.jwtToken || callbackData.error || callbackData.isNewMember) {
                setIsProcessing(true);

                try {
                    if (callbackData.error) {
                        throw new Error(callbackData.message || '로그인 중 오류가 발생했습니다.');
                    }

                    if (callbackData.jwtToken) {
                        // JWT 토큰이 있으면 직접 저장하고 로그인 처리
                        const member = authService.createMemberFromJWT(callbackData.jwtToken, callbackData.nickname);
                        
                        setAuthData(callbackData.jwtToken, member);

                        console.log('백엔드 콜백으로 로그인 성공:', { member });
                        
                        // URL 파라미터 제거하고 홈으로 이동
                        navigate('/home', { replace: true });
                        return;
                    }

                    if (callbackData.isNewMember && callbackData.kakaoAccessToken) {
                        // 신규 회원 - 회원가입 페이지로 이동
                        navigate('/signup', { 
                            state: { 
                                kakaoAccessToken: callbackData.kakaoAccessToken,
                                isNewMember: true 
                            },
                            replace: true
                        });
                        return;
                    }
                } catch (error) {
                    console.error('백엔드 콜백 처리 실패:', error);
                    alert(error instanceof Error ? error.message : '로그인 처리 중 오류가 발생했습니다.');
                    navigate('/login', { replace: true });
                } finally {
                    setIsProcessing(false);
                }
            }
            // 카카오 인증 코드만 있는 경우
            else if (code) {
                setIsProcessing(true);

                try {
                    console.log('카카오 인증 코드로 로그인 처리:', code.substring(0, 20) + '...');
                    await loginWithCode(code);
                    navigate('/home', { replace: true });
                } catch (error) {
                    console.error('카카오 코드 로그인 실패:', error);
                    alert('로그인 처리 중 오류가 발생했습니다.');
                    navigate('/login', { replace: true });
                } finally {
                    setIsProcessing(false);
                }
            }
        };

        processCallback();
    }, [searchParams, navigate, loginWithCode, setAuthData, isProcessing]);

    return { isProcessing };
};
