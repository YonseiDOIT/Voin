import { useState } from 'react';
import PencilIcon from '../../assets/svgs/TodaysDiary/Pencil.svg?react';

const WritingTip = () => {
    // 컴포넌트의 확장/축소 상태를 관리하는 state
    const [isExpanded, setIsExpanded] = useState(false);

    // 상태를 반전시키는 토글 함수
    const toggleExpansion = () => {
        setIsExpanded(!isExpanded);
    };

    return (
        <div className="w-full max-w-lg mx-auto">
            <div className="bg-[#86E1F3]/20 p-4 rounded-2xl">
                <div
                    className="flex justify-between items-center cursor-pointer"
                    onClick={toggleExpansion}
                    role="button"
                    aria-expanded={isExpanded}
                >
                    <div className="flex items-center space-x-2">
                        <PencilIcon/>
                        <span className="body-n text-VB-20 font-semibold">작성 팁</span>
                    </div>
                    {isExpanded ? (
                        <ChevronUpIcon className="w-5 h-5 text-gray-600" />
                    ) : (
                        <ChevronDownIcon className="w-5 h-5 text-gray-600" />
                    )}
                </div>

                {/* 확장되는 컨텐츠 영역 */}
                <div
                    className={`overflow-hidden transition-all duration-300 ease-in-out ${isExpanded ? 'max-h-40 mt-3' : 'max-h-0'
                        }`}
                >
                    <p className="body-n text-VB-20 leading-relaxed">
                        거창하지 않아도 괜찮아요. 오늘의 생각, 선택, 감정을 편하게 적어보세요. 일상을 적다 보면, 그 안에서 나만의 장점을 발견할 수 있어요.
                    </p>
                </div>
            </div>
        </div>
    );
};

// 화살표 아이콘 컴포넌트 (Heroicons SVG)
const ChevronDownIcon = (props: React.SVGProps<SVGSVGElement>) => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" {...props}>
        <path strokeLinecap="round" strokeLinejoin="round" d="m19.5 8.25-7.5 7.5-7.5-7.5" />
    </svg>
);

const ChevronUpIcon = (props: React.SVGProps<SVGSVGElement>) => (
    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5} stroke="currentColor" {...props}>
        <path strokeLinecap="round" strokeLinejoin="round" d="m4.5 15.75 7.5-7.5 7.5 7.5" />
    </svg>
);

export default WritingTip;