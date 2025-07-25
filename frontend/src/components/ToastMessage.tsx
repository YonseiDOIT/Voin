import { useEffect, useState, useLayoutEffect } from 'react';

import CheckIcon from '@/assets/svgs/ToastMessage/check.svg?react';

interface ToastMessageProps {
    message: string;
    duration?: number; // ms
    onClose?: () => void;
}

const ToastMessage = ({ message, duration = 3000, onClose }: ToastMessageProps) => {
    const [visible, setVisible] = useState(true);

    useEffect(() => {
        const timer = setTimeout(() => {
            setVisible(false);
            if (onClose) onClose();
        }, duration);
        return () => clearTimeout(timer);
    }, [duration, onClose]);

    // 애니메이션 상태 관리
    const [show, setShow] = useState(false);

    useLayoutEffect(() => {
        // 마운트 시 페이드 인 (DOM 렌더 후 바로 enter 상태로 변경)
        setShow(true);
    }, []);

    // 애니메이션 타이밍 관리
    const [animationState, setAnimationState] = useState<'enter' | 'leave' | 'idle'>('idle');

    useEffect(() => {
        if (show && visible) {
            // DOM 렌더 후 enter로 변경 (슬라이드+페이드 인)
            setTimeout(() => setAnimationState('enter'), 10);
        } else if (show && !visible) {
            setAnimationState('leave');
            setTimeout(() => setShow(false), 400);
        }
    }, [show, visible]);

    if (!show) return null;

    // 애니메이션 클래스
    let animationClass = '';
    if (animationState === 'enter') {
        animationClass = 'opacity-100 translate-y-0';
    } else if (animationState === 'leave') {
        animationClass = 'opacity-0 translate-y-8';
    } else {
        // idle(초기) 상태에서 opacity-0 translate-y-8로 시작
        animationClass = 'opacity-0 translate-y-8';
    }

    return (
        <div className="fixed left-0 bottom-4 w-full px-6 z-50">
            <div
                className={`flex flex-row items-center gap-x-1 p-4 bg-grey-30 rounded-full shadow-lg mx-auto transition-all duration-400 ease-in-out ${animationClass}`}
            >
                <div className="flex justify-center m-1 items-center bg-VB-40 rounded-full">
                    <CheckIcon />
                </div>
                <span className="text-white line-13 text-[14px]">
                    {message}
                </span>
            </div>
        </div>
    );
};

export default ToastMessage;