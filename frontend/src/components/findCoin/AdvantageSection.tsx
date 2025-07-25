import { useEffect, useRef } from 'react';

import EnabledButtonImage from "@/assets/svgs/TodaysDiary/EnabledButton.svg?react";
import { DynamicIcon } from '@/components/common/DynamicIcon';

interface Props {
    id: string;
    categoryTitle: string;
    items: string[];
    onMount: (id: string, element: HTMLElement) => void;
    onUnmount: (id: string) => void;
    onSelectItem: (category: string, item: string) => void;
    selectedItems: string[];
    isFirst?: boolean;
}

function AdvantageSection({ id, categoryTitle, items, onMount, onUnmount, onSelectItem, selectedItems, isFirst = false }: Props) {
    const sectionRef = useRef<HTMLElement>(null);

    // 카테고리별 색상 매핑 (비선택 상태에서만 사용)
    const getCategoryColor = () => {
        const colorMap: Record<string, string> = {
            '관리와 성장': '#E0E9F7',
            '감정과 태도': '#FAEEE3',
            '창의와 몰입': '#EAE4F7',
            '사고와 해결': '#EAF3EC',
            '관계와 공감': '#F9F2DC',
            '신념과 실행': '#F9EDED',
        };

        return colorMap[categoryTitle] || colorMap['관리와 성장'];
    };

    useEffect(() => {
        if (sectionRef.current) {
            onMount(id, sectionRef.current);
        }
        // 컴포넌트가 사라질 때 unmount 함수 호출
        return () => {
            onUnmount(id);
        };
    }, [id, onMount, onUnmount]);

    return (
        <section ref={sectionRef} id={id}>
            {!isFirst && <hr className='h-px bg-grey-97 border-0 my-6'/>}
            <div className="grid grid-cols-3 gap-2">
                {items.map((item) => {
                    const isSelected = selectedItems.includes(item);
                    
                    return (
                        <div
                            key={item}
                            onClick={() => onSelectItem(id, item)}
                            className={`relative flex flex-col items-center aspect-[1/1.15] justify-center px-6 pt-5 pb-4 gap-y-3 transition-colors duration-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]`}
                        >
                            {/* 비선택 상태 - 카테고리별 색상 배경 */}
                            {!isSelected && (
                                <svg 
                                    viewBox="10 5 100 115" 
                                    fill="none" 
                                    xmlns="http://www.w3.org/2000/svg"
                                    className="absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200"
                                    preserveAspectRatio="none"
                                >
                                    <defs>
                                        <linearGradient id={`gradient_${id}_${item.replace(/\s+/g, '')}`} x1="60" y1="120" x2="60" y2="5" gradientUnits="userSpaceOnUse">
                                            <stop offset="0.1" stopColor="white" stopOpacity="0"/>
                                            <stop offset="1" stopColor={getCategoryColor()}/>
                                        </linearGradient>
                                        <linearGradient id={`gradient_stroke_${id}_${item.replace(/\s+/g, '')}`} x1="60" y1="5" x2="60" y2="120" gradientUnits="userSpaceOnUse">
                                            <stop stopColor="white"/>
                                            <stop offset="1" stopColor="white" stopOpacity="0.2"/>
                                        </linearGradient>
                                    </defs>
                                    <rect x="10" y="5" width="100" height="115" rx="24" fill={`url(#gradient_${id}_${item.replace(/\s+/g, '')})`} shapeRendering="crispEdges"/>
                                    <rect x="11" y="6" width="98" height="113" rx="23" stroke={`url(#gradient_stroke_${id}_${item.replace(/\s+/g, '')})`} strokeWidth="2" shapeRendering="crispEdges"/>
                                </svg>
                            )}
                            
                            {/* 선택 상태 - 기본 EnabledButtonImage */}
                            {isSelected && (
                                <EnabledButtonImage
                                    preserveAspectRatio="none"
                                    className="absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200"
                                />
                            )}
                            
                            {/* 동적 SVG 아이콘 */}
                            <DynamicIcon
                                name={item}
                                isSelected={isSelected}
                            />
                            
                            <div className={`body-n font-semibold whitespace-nowrap duration-200
                                ${isSelected ? 'text-white' : 'text-grey-50'}`}>
                                {item}
                            </div>
                        </div>
                    );
                })}
            </div>
        </section>
    );
}

export default AdvantageSection;
