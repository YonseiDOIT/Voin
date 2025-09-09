import TopNavigation from '@/components/common/TopNavigation';
import ProfileUploader from '@/components/ProfileUploader';
import TextInput from '@/components/TextInput';
import ActionButton from '@/components/common/ActionButton';

import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
// import type { Member } from '@/services/authService';

const SignUp = () => {
    const [nickname, setNickname] = useState<string>('');
    const [profileImage, setProfileImage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const navigate = useNavigate();
    const { kakaoNickname, kakaoProfileImage } = useLocation().state || {};
    
    useEffect(() => {
        // 카카오 로그인 정보가 있다면 초기화
        setNickname(kakaoNickname);
        setProfileImage(kakaoProfileImage);

        setIsLoading(false);
    }, [kakaoNickname, kakaoProfileImage]);


    // 프로필 이미지 변경 핸들러
    const handleProfileImageChange = (imageUrl: string | null) => {
        setProfileImage(imageUrl);
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
            const JWTtoken = localStorage.getItem('jwtToken');
            if (!JWTtoken) {
                throw new Error('로그인이 필요합니다. 다시 로그인해주세요.');
            }

            const response = await fetch('/api/members/me', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${JWTtoken}`,
                },
                body: JSON.stringify({
                    nickname: nickname,
                    // profileImage: profileImage,
                    profileImage: "abcd",
                }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`회원가입 요청 실패: ${response.status} ${errorText}`);
            }

            const apiResponse = await response.json();
            console.log('회원가입 응답:', apiResponse);
            if (apiResponse.success && apiResponse.data) {
                console.log('회원가입 성공:', apiResponse.data);
                // 백엔드에서 받은 JWT와 사용자 정보로 로그인 상태 설정
                localStorage.setItem('userInfo', JSON.stringify(apiResponse.data));
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