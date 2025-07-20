import React from 'react';
import { getAdvantageIcon } from '../../icons/advantageIcons';

interface DynamicIconProps {
    name: string;
    isSelected: boolean;
}

export const DynamicIcon: React.FC<DynamicIconProps> = ({
    name,
    isSelected
}) => {
    const IconComponent = getAdvantageIcon(name);

    if (isSelected) {
        // 선택된 상태: 흰색으로 강제 변경
        const uniqueId = `icon-${name.replace(/\s+/g, '')}-${Date.now()}`;
        return (
            <>
                <style dangerouslySetInnerHTML={{
                    __html: `
                        .${uniqueId} svg path,
                        .${uniqueId} svg circle,
                        .${uniqueId} svg rect,
                        .${uniqueId} svg polygon,
                        .${uniqueId} svg ellipse,
                        .${uniqueId} svg line {
                            fill: white !important;
                            stroke: white !important;
                            transition: fill 0.2s ease, stroke 0.2s ease;
                        }
                    `
                }} />
                <div className={uniqueId}>
                    <IconComponent
                        width={48}
                        height={48}
                        className="transition-all duration-200"
                    />
                </div>
            </>
        );
    } else {
        // 비선택 상태: 원래 SVG 색상 사용
        return (
            <IconComponent
                width={48}
                height={48}
                className="transition-all duration-200"
            />
        );
    }
};
