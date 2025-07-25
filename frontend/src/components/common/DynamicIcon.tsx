import React from 'react';
import { getAdvantageIcon } from '@/icons/advantageIcons';

interface DynamicIconProps {
  name: string;
  isSelected: boolean;
}

export const DynamicIcon: React.FC<DynamicIconProps> = ({
  name,
  isSelected,
}) => {
  const IconComponent = getAdvantageIcon(name);

  // isSelected 상태에 따라 아이콘 색상을 props로 직접 전달
  // SVG 내부의 fill과 stroke를 제어하기 위해 'currentColor'를 사용하도록 SVG 파일을 수정해야 할 수 있음
  // 여기서는 React 컴포넌트로 가져온 SVG에 직접 props를 전달하여 색상을 제어
  // const iconProps = {
  //   width: 48,
  //   height: 48,
  //   className: `transition-all duration-200 ${isSelected ? 'text-white' : ''}`,
  //   fill: isSelected ? 'white' : undefined, // 선택 시 흰색, 아닐 시 원래 색상
  //   stroke: isSelected ? 'white' : undefined,
  // };

  const uniqueId = `icon-${name.replace(/\s+/g, '')}-${Date.now()}`;

  if (isSelected) {
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
  }

  return <IconComponent width={48} height={48} className="transition-all duration-200" />;
};
