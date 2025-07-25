import TopNavigation from '@/components/common/TopNavigation';
import ProfileUploader from '@/components/ProfileUploader';
import TextInput from '@/components/TextInput';
import ActionButton from '@/components/common/ActionButton';

import { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import type { Member } from '@/services/authService';

const SignUp = () => {
    const [nickname, setNickname] = useState<string>('');
    const [profileImage, setProfileImage] = useState<string | null>(null);
    const [finalProfileImage, setFinalProfileImage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const { setAuthData } = useAuthStore((state) => state.actions);

    useEffect(() => {
        const fetchKakaoProfile = async () => {
            try {
                // URL 파라미터에서 카카오 토큰 정보 확인
                const kakaoAccessToken = searchParams.get('kakao_access_token');
                const isNewMember = searchParams.get('is_new_member');
                
                console.log('회원가입 페이지 진입:', {
                    kakaoAccessToken: kakaoAccessToken ? kakaoAccessToken.substring(0, 20) + '...' : null,
                    isNewMember
                });

                // 새로운 회원이 아니면 로그인 페이지로 리다이렉트
                if (isNewMember !== 'true') {
                    console.log('신규 회원이 아닙니다. 로그인 페이지로 리다이렉트');
                    navigate('/login', { replace: true });
                    return;
                }

                if (kakaoAccessToken) {
                    // 카카오 프로필 정보 가져오기
                    const profileResponse = await fetch('https://kapi.kakao.com/v2/user/me', {
                        headers: {
                            'Authorization': `Bearer ${kakaoAccessToken}`
                        }
                    });

                    if (profileResponse.ok) {
                        const profileData = await profileResponse.json();
                        
                        // 카카오 닉네임 설정
                        if (profileData.kakao_account?.profile?.nickname) {
                            setNickname(profileData.kakao_account.profile.nickname);
                        }
                        
                        // 카카오 프로필 이미지 설정
                        if (profileData.kakao_account?.profile?.profile_image_url) {
                            setProfileImage(profileData.kakao_account.profile.profile_image_url);
                        }
                    }
                }
            } catch (error) {
                console.error('카카오 프로필 정보 가져오기 실패:', error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchKakaoProfile();
    }, [searchParams, navigate]);

    // 프로필 이미지 변경 핸들러
    const handleProfileImageChange = (imageUrl: string | null) => {
        setFinalProfileImage(imageUrl);
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value;
        const sanitizedValue = rawValue.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]/g, '');

        setNickname(sanitizedValue);

        // 유효성 검사
        if (rawValue !== sanitizedValue) {
            setError('공백없이 한글, 영문, 숫자만 입력해주세요.');
        }
        else if (sanitizedValue.length > 0 && sanitizedValue.length < 3) {
            setError('3글자 이상 입력해주세요.');
        }
        else {
            setError(null);
        }
    };

    const handleClear = () => {
        setNickname('');
        setError(null);
    };

    const handleButtonClick = async () => {
        try {
            const kakaoAccessToken = searchParams.get('kakao_access_token');
            if (!kakaoAccessToken) {
                alert('카카오 로그인 정보가 없습니다. 다시 로그인해주세요.');
                navigate('/login');
                return;
            }

            const response = await fetch('/signup/profile-image', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    accessToken: kakaoAccessToken,
                    nickname: nickname,
                    profileImage: finalProfileImage || profileImage,
                }),
                credentials: 'include',
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`회원가입 요청 실패: ${response.status} ${errorText}`);
            }

            const apiResponse = await response.json();

            if (apiResponse.success && apiResponse.data) {
                // 백엔드에서 받은 JWT와 사용자 정보로 로그인 상태 설정
                const { jwtToken, member }: { jwtToken: string; member: Member } = apiResponse.data;
                setAuthData(jwtToken, member);
                navigate('/home');
            } else {
                throw new Error(apiResponse.message || '회원가입에 실패했습니다.');
            }
        } catch (error) {
            console.error('회원가입 오류:', error);
            const errorMessage = error instanceof Error ? error.message : '회원가입 중 알 수 없는 오류가 발생했습니다.';
            alert(errorMessage);
        }
    };

    return (
        <div className="h-full w-full flex flex-col pt-4">
            <TopNavigation
                title="만나서 반가워요!"
                caption="프로필을 완성하고 Voin을 시작해볼까요?"
            />
            {isLoading ? (
                <div className="flex items-center justify-center py-8">
                    <div className="text-gray-500">프로필 정보를 불러오는 중...</div>
                </div>
            ) : (
                <ProfileUploader 
                    defaultImage={profileImage} 
                    onChange={handleProfileImageChange}
                />
            )}
            <div className='w-full px-6'>
                <TextInput
                    label="닉네임"
                    placeholder="닉네임을 입력해주세요"
                    maxLength={10}
                    onChange={handleChange}
                    onClear={handleClear}
                    value={nickname}
                    error={error}
                />
            </div>
            <div className='mt-auto pb-4 px-6'>
                <ActionButton
                    onClick={handleButtonClick}
                    disabled={!nickname || nickname.length < 3 || !!error}
                    buttonText='완료'
                />
            </div>
        </div>
    );
}

export default SignUp;