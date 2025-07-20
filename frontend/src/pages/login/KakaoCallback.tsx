import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const KakaoCallback = () => {
    const navigate = useNavigate();
    const { login } = useAuth();

    useEffect(() => {
        const handleCallback = async () => {
            try {
                // URL에서 파라미터 추출
                const urlParams = new URLSearchParams(window.location.search);
                const loginSuccess = urlParams.get('login_success');
                const isNewMember = urlParams.get('is_new_member');
                const accessToken = urlParams.get('access_token');
                const kakaoAccessToken = urlParams.get('kakao_access_token');
                const nickname = urlParams.get('nickname');
                const error = urlParams.get('error');
                const errorMessage = urlParams.get('message');

                if (error) {
                    throw new Error(errorMessage || '로그인 중 오류가 발생했습니다');
                }

                if (loginSuccess === 'true') {
                    if (isNewMember === 'true') {
                        // 신규 회원 - 회원가입 페이지로 이동
                        navigate('/signup', { 
                            state: { 
                                kakaoAccessToken,
                                isNewMember: true 
                            }
                        });
                    } else {
                        // 기존 회원 - 홈으로 이동
                        console.log('KakaoCallback - existing member login:', { nickname, accessToken });
                        if (nickname) {
                            const decodedNickname = decodeURIComponent(nickname);
                            console.log('KakaoCallback - calling login with:', { decodedNickname, accessToken });
                            // AuthContext에 로그인 정보 저장 (accessToken 포함)
                            login(decodedNickname, undefined, accessToken || undefined);
                            
                            // 상태 업데이트 후 네비게이션
                            setTimeout(() => {
                                console.log('KakaoCallback - navigating to /home after timeout');
                                navigate('/home');
                            }, 100);
                        } else {
                            console.log('KakaoCallback - no nickname, navigating to /home');
                            navigate('/home');
                        }
                    }
                } else {
                    throw new Error('로그인 정보가 올바르지 않습니다');
                }
            } catch (error) {
                console.error('Kakao login error:', error);
                const errorMessage = error instanceof Error ? error.message : '로그인에 실패했습니다. 다시 시도해주세요.';
                alert(errorMessage);
                navigate('/login');
            }
        };

        handleCallback();
    }, [navigate, login]);

    return (
        <div className="h-full w-full flex items-center justify-center">
            <div className="text-center">
                <div className="mb-4">로그인 처리 중...</div>
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto"></div>
            </div>
        </div>
    );
};

export default KakaoCallback;
