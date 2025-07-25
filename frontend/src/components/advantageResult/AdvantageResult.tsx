import type { AdvantageResultTheme } from '@/components/advantageResult/advantageResultTypes';
import { useState, useEffect } from 'react';
import { getAdvantageImage, getCirclePlaceholderUrl } from '@/assets/images/advantageImages';
import { useActivityStore } from '@/store/useActivityStore';

type AdvantageResultColorPalette = {
    titleStyle: string;
    categoryStyle: string;
    backgroundClass: string;
}

const colorPalettes: Record<AdvantageResultTheme, AdvantageResultColorPalette> = {
    'GROWTH': {
        titleStyle: "text-[#305DAB]",
        categoryStyle: "bg-[#D0E1FB] text-[#305DAB]",
        backgroundClass: "bg-gradient-to-b from-[#E0E9F7] to-white",
    },
    'EMOTION': {
        titleStyle: "text-[#A95800]",
        categoryStyle: "bg-[#FFC382] text-[#BA4614]",
        backgroundClass: "bg-gradient-to-b from-[#FAEEE3] to-white",
    },
    'CREATIVITY': {
        titleStyle: "text-[#7A3BC2]",
        categoryStyle: "bg-[#DACBF7] text-[#7A3BC2]",
        backgroundClass: "bg-gradient-to-b from-[#EAE4F7] to-white",
    },
    'PROBLEM_SOLVING': {
        titleStyle: "text-[#007832]",
        categoryStyle: "bg-[#C7E9D0] text-[#007832]",
        backgroundClass: "bg-gradient-to-b from-[#EAF3EC] to-white",
    },
    'RELATIONSHIP': {
        titleStyle: "text-[#896A00]",
        categoryStyle: "bg-[#FFEB9C] text-[#896A00]",
        backgroundClass: "bg-gradient-to-b from-[#F9F2DC] to-white",
    },
    'BELIEFS': {
        titleStyle: "text-[#B63043]",
        categoryStyle: "bg-[#FFB9BB] text-[#B63043]",
        backgroundClass: "bg-gradient-to-b from-[#F9EDED] to-white",
    },
};


// props는 선택적으로 유지하되, 없으면 zustand에서 읽어오도록 처리
export interface AdvantageResultProps {
    theme?: AdvantageResultTheme;
    category?: string;
    title?: string;
    titleDescription?: string;
    description?: string;
}


const AdvantageResult = (props: AdvantageResultProps) => {
    // zustand에서 데이터 읽기
    const activityData = useActivityStore((state) => state.data);
    // props 우선, 없으면 zustand 값 사용
    const theme = props.theme ?? ('GROWTH');
    const category = props.category ?? activityData.categoryName ?? '';
    const title = props.title ?? activityData.strengthName ?? '';
    const titleDescription = props.titleDescription ?? activityData.strengthDescription ?? '';
    const description = props.description ?? activityData.fullDescription ?? '';

    const palette = colorPalettes[theme];
    const [imageUrl, setImageUrl] = useState<string>('');
    const [imageLoading, setImageLoading] = useState<boolean>(true);
    const [imageError, setImageError] = useState<boolean>(false);

    useEffect(() => {
        const loadImage = async () => {
            setImageLoading(true);
            setImageError(false);
            try {
                const imageUrl = await getAdvantageImage(title);
                setImageUrl(imageUrl);
            } catch (error) {
                console.error(`이미지 로딩 실패 (${title}):`, error);
                setImageError(true);
                setImageUrl(getCirclePlaceholderUrl(title));
            }
            setImageLoading(false);
        };
        if (title) {
            loadImage();
        } else {
            setImageUrl('');
            setImageLoading(false);
        }
    }, [title]);

    return (
        <div className={`
            w-full px-6 py-8
            ${palette.backgroundClass}
            rounded-[24px]
            shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]
            outline-2 outline-offset-[-2px] outline-white
            inline-flex flex-col justify-start items-center gap-8
            overflow-hidden
        `}>
            <div className={`
                self-stretch
                flex
                flex-col
                justify-start
                items-start
                gap-4
            `}>
                <div data-active="True" data-color="Blue" data-count="False" data-type="Category" className={`p-3 ${palette.categoryStyle} rounded-3xl inline-flex justify-center items-center gap-2`}>
                    <span className={`${palette.titleStyle} line-16px text-[15px] font-semibold`}>
                        {category}
                    </span>
                </div>
                <div className={`
                    self-stretch
                    px-1
                    flex
                    flex-col
                    justify-start
                    items-start
                    gap-1
                `}>
                    <span className={`${palette.titleStyle} text-[28px] font-bold line-14`}>
                        {title}
                    </span>
                    <span className={`
                        self-stretch
                        text-grey-50
                        text-[18px]
                        line-14
                    `}>{titleDescription}</span>
                </div>
            </div>
            <div className={`
                inline-flex
                justify-center
                items-end
                gap-2
            `}>
                {imageLoading ? (
                    // 로딩 중일 때 스켈레톤 또는 스피너
                    <div className="w-60 h-60 bg-gray-200 rounded-lg animate-pulse flex items-center justify-center">
                        <span className="text-gray-500 text-sm">이미지 로딩중...</span>
                    </div>
                ) : (
                    <div className="relative w-60 h-60">
                        <img
                            className="w-full h-full object-cover rounded-lg"
                            src={imageUrl}
                            alt={`${title} 장점 이미지`}
                            onLoad={() => {
                                // 로드 성공 시 에러 상태 해제
                                if (imageError) {
                                    setImageError(false);
                                }
                            }}
                            onError={(e) => {
                                // 이미지 로드 실패 시 원형 placeholder로 변경 (조용한 처리)
                                if (!imageError && !e.currentTarget.src.includes('data:image/svg+xml')) {
                                    e.currentTarget.src = getCirclePlaceholderUrl(title);
                                    setImageError(true);
                                }
                            }}
                        />
                        {imageError && (
                            <div className="absolute bottom-2 right-2 bg-black bg-opacity-50 text-white text-xs px-2 py-1 rounded">
                                임시 이미지
                            </div>
                        )}
                    </div>
                )}
            </div>
            <div className={`
                inline-flex
                justify-start
                items-center
                gap-2
                h-auto
            `}>
                <span className={`
                    flex-1
                    w-full
                    justify-center
                    text-grey-40
                    text-[16px]
                    line-15
                `}>{description}</span>
            </div>
        </div>
    )
}

export default AdvantageResult;