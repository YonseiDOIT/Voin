import TopNavigation from '../../components/TopNavigation';
import ProfileUploader from '../../components/ProfileUploader';
import TextInput from '../../components/TextInput';
import ActionButton from '../../components/ActionButton';

import { useState } from 'react';

const RegisterProfile = () => {
    const [nickname, setNickname] = useState<string>('');
    const [error, setError] = useState<string | null>(null);

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

    const handleButtonClick = () => {
        alert('프로필 등록 완료!');
    };

    return (
        <div className="h-full w-full flex flex-col pt-4">
            <TopNavigation
                title="만나서 반가워요!"
                caption="프로필을 완성하고 Voin을 시작해볼까요?"
            />
            <ProfileUploader />
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
                    disabled={!nickname || !!error}
                    buttonText='완료'
                />
            </div>
        </div>
    );
}

export default RegisterProfile;