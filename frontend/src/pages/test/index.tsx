import { Link } from 'react-router-dom';
import { useState } from 'react';

import TextInput from '@/components/TextInput';
import ActionButton from '@/components/common/ActionButton';
import TopNavigation from '@/components/common/TopNavigation';

const IndexPage = () => {
    const [text, setText] = useState('');
    const [error, setError] = useState<string | null>(null);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const rawValue = e.target.value;
        const sanitizedValue = rawValue.replace(/[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]/g, '');

        setText(sanitizedValue);

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

    const handleDefaultClick = () => {
        alert('기본 버튼 클릭!');
    };

    const handleClear = () => {
        setText('');
        setError(null);
    };

    return (
        <div className="w-full h-full flex flex-col">
            <Link to="/">Back To Router Page</Link>
            <div className='w-full px-4'>
                {/* 기본 상태 */}
                <TextInput
                    label="주제"
                    value={text}
                    onChange={handleChange}
                    onClear={handleClear}
                    error={error}
                    maxLength={10}
                    helperText="시나리오1 텍스트 박스입니다"
                    placeholder="텍스트를 입력해주세요."
                />
                <br />
                {/* 비활성 및 값만 있는 상태 */}
                <TextInput
                    label="주제 (입력 후)"
                    value="입력 완료된 텍스트"
                    onChange={() => { }}
                    onClear={() => { }}
                    maxLength={20}
                />
                <br />
                {/* 에러 상태 */}
                <TextInput
                    label="주제 (에러 예시)"
                    value="두글자"
                    onChange={() => { }}
                    onClear={() => { }}
                    error="에러 메시지가 출력됩니다."
                    maxLength={10}
                />
            </div>
            <div>
                <br />
                <ActionButton buttonText='버튼' onClick={handleDefaultClick} />
                <div className='py-1'></div>
                <ActionButton buttonText='버튼 (비활성화)' onClick={handleDefaultClick} disabled={true}/>
            </div>
            <div>
                <br />
                <TopNavigation title="타이틀 영역" caption='캡션이 들어가는 영역'/>
            </div>
        </div>
    )
}

export default IndexPage;
