import React, { useState } from 'react';

interface TextInputProps {
    label: string;
    value: string;
    // 입력 필드의 값이 변경될 때 호출될 함수
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    // 입력 필드의 값을 초기화할 때 호출될 함수
    onClear: () => void;
    error?: string | null;
    placeholder?: string;
    // 하단 에 표시될 도움말 텍스트
    helperText?: string;
    maxLength?: number;
}

const TextInput: React.FC<TextInputProps> = ({
    label,
    value,
    onChange,
    onClear,
    error = null,
    placeholder = '텍스트를 입력해주세요.',
    helperText,
    maxLength = 10,
}) => {
    const [isFocused, setIsFocused] = useState(false);

    const handleFocus = () => setIsFocused(true);
    const handleBlur = () => setIsFocused(false);
    const decoratedOnChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (maxLength !== undefined && e.target.value.length > maxLength) {
            e.target.value = e.target.value.slice(0, maxLength);
        }
        onChange(e);
    }

    const borderColorClass = error
        ? 'border-[#FF4242]' // 에러 상태
        : isFocused
            ? 'border-VB-50' // 활성 상태
            : 'border-gray-200'; // 비활성 상태

    return (
        <div className="w-full font-sans">
            <label htmlFor={label} className="block button-n font-medium text-grey-70 mb-2">
                {label}
            </label>
            <div className="relative">
                <input
                    id={label}
                    type="text"
                    value={value}
                    onChange={decoratedOnChange}
                    onFocus={handleFocus}
                    onBlur={handleBlur}
                    placeholder={placeholder}
                    maxLength={maxLength}
                    className={`w-full box-border rounded-2xl body-n transition-colors duration-200 py-3 pl-4 pr-10 placeholder:text-gray-400 focus:outline-none bg-grey-98 focus:bg-white border ${borderColorClass}`}
                />
                {/* 포커스 상태이고 입력 값이 있을 때만 초기화 버튼 표시 */}
                {isFocused && value && (
                    <button
                        type="button"
                        onMouseDown={(e) => e.preventDefault()}
                        onClick={onClear}
                        aria-label="입력 내용 지우기"
                        className="absolute right-3 top-1/2 -translate-y-1/2 w-5 h-5 bg-gray-400 text-white rounded-full cursor-pointer flex items-center justify-center body-n leading-none">
                        ×
                    </button>
                )}
            </div>
            <div className="flex justify-between mt-1.5">
                <span className={`button-n ${error ? 'text-red-500' : 'text-grey-70'}`}>
                    {error || helperText}
                </span>
                <span className="button-n text-grey-70">
                    {value.length}/{maxLength}
                </span>
            </div>
        </div>
    );
};

export default TextInput;