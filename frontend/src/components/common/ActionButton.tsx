import { type MouseEvent, useState } from 'react';

import DefaultBg from '../../assets/svgs/Button/default.svg?react'
import DisabledBg from '../../assets/svgs/Button/disabled.svg?react'
import HoverBg from '../../assets/svgs/Button/hover.svg?react'

interface ActionButtonProps {
    buttonText: string;
    onClick: (event: MouseEvent<HTMLButtonElement>) => void;
    disabled?: boolean;
    defaultBgClassName?: string;
    hoverBgClassName?: string;
    disabledBgClassName?: string;
}

const ActionButton = ({
    buttonText,
    onClick,
    disabled = false,
    defaultBgClassName = 'w-full h-full',
    hoverBgClassName = 'w-full h-full',
    disabledBgClassName = 'w-full h-full'
}: ActionButtonProps) => {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <div className='w-full h-fit'>
            <button onClick={onClick} disabled={disabled}
                onTouchStart={() => setIsHovered(true)}
                onTouchEnd={() => setIsHovered(false)}
                onMouseLeave={() => setIsHovered(false)}
                className='relative w-full h-fit bg-transparent isolate'>
                <DefaultBg preserveAspectRatio="none" className={`absolute top-0 left-0 z-[-1] transition-opacity duration-200 ${!disabled && !isHovered ? 'opacity-100' : 'opacity-0'} ${defaultBgClassName}`} />
                <HoverBg preserveAspectRatio="none" className={`absolute top-0 left-0 z-[-1] transition-opacity duration-200 ${!disabled && isHovered ? 'opacity-100' : 'opacity-0'} ${hoverBgClassName}`} />
                <DisabledBg preserveAspectRatio="none" className={`absolute top-0 left-0 z-[-1] transition-opacity duration-200 ${disabled ? 'opacity-100' : 'opacity-0'} ${disabledBgClassName}`} />
                <div className='flex items-center justify-center w-full h-full z-10'>
                    <span className={`title-n ${disabled ? "text-grey-90" : "text-white"} font-semibold py-4`}>
                        {buttonText}
                    </span>
                </div>
            </button>
        </div>
    );
};

export default ActionButton;