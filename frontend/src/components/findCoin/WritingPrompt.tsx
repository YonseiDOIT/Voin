import TopNavigation from '@/components/common/TopNavigation';
import WritingTip from './WritingTip';
import LargeTextField from './LargeTextField';
import ActionButton from '@/components/common/ActionButton';
import BottomSheet from '@/components/common/BottomSheet';
import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { useActivityStore } from '@/store/useActivityStore';

import AiCoinIcon from '@/assets/svgs/TodaysDiary/AiCoin.svg?react';
import SearchCoinIcon from '@/assets/svgs/TodaysDiary/SearchCoin.svg?react';

interface WritingPromptProps {
    title: string;
    tip: string;
    placeholder: string;
    directLinkTo: string;
    directLinkToAi?: string;
    maxLength?: number;
    minLength?: number;
    showBottomSheet?: boolean; // BottomSheet 표시 여부를 제어하는 파라미터
    dataKey?: string; // 저장할 데이터의 키 (예: 'writtenCase1', 'writtenCase2')
    onSubmit?: (text: string) => void | Promise<void>;
}

import { useEffect } from 'react';

export default function WritingPrompt({ 
    title, 
    tip, 
    placeholder, 
    directLinkTo,
    directLinkToAi,
    maxLength = 500,
    minLength = 40,
    showBottomSheet = false,
    dataKey,
    onSubmit
}: WritingPromptProps) {
    const [isSheetOpen, setIsSheetOpen] = useState(false);
    const [diaryContent, setDiaryContent] = useState<string>('');
    const navigate = useNavigate();
    
    const openSheet = () => setIsSheetOpen(true);
    const closeSheet = () => setIsSheetOpen(false);
    const setActivityData = useActivityStore((state) => state.setData);
    const activityData = useActivityStore((state) => state.data);

    useEffect(() => {
        console.log('[ActivityData zustand]', activityData);
    }, [activityData]);

    // 버튼 클릭 핸들러: showBottomSheet가 true면 BottomSheet 열기, false면 zustand에 저장 후 다음 페이지로 이동
    const handleButtonClick = () => {
        if (showBottomSheet) {
            openSheet();
        } else {
            setActivityData({ [dataKey || 'content']: diaryContent.trim() });
            if (onSubmit) {
                onSubmit(diaryContent.trim());
            }
            navigate(directLinkTo);
        }
    };

    const BottomSheetButtonClick = () => {
        setActivityData({ [dataKey || 'content']: diaryContent.trim() });
        if (onSubmit) {
            onSubmit(diaryContent.trim());
        }
        navigate(directLinkTo);
    };

    const BottomSheetAiButtonClick = () => {
        setActivityData({ [dataKey || 'content']: diaryContent.trim() });
        if (onSubmit) {
            onSubmit(diaryContent.trim());
        }
        if (directLinkToAi) {
            navigate(directLinkToAi);
        } else {
            // 에러 처리 또는 fallback 경로로 이동
            console.error('Direct link to AI classification is not provided.');
        } 
    };

    const isContentTooShort = diaryContent.trim().length > 0 && diaryContent.trim().length < minLength;
    const errorMessage = isContentTooShort ? `최소 ${minLength}자 이상 입력해주세요.` : null;
    
    return (
        <div className="h-full w-full flex flex-col">
            <TopNavigation title={title} />
            <div className="w-full h-full flex flex-col px-6 gap-y-4">
                <WritingTip tip={tip} />
                <LargeTextField
                    value={diaryContent}
                    onChange={(e) => setDiaryContent(e.target.value)}
                    placeholder={placeholder}
                    maxLength={maxLength}
                    error={errorMessage}
                />
                <div className='w-full mt-2 mb-4'>
                    <ActionButton
                        buttonText="다음"
                        onClick={handleButtonClick}
                        disabled={!diaryContent.trim() || diaryContent.trim().length < minLength}
                    />
                </div>
            </div>

            {/* showBottomSheet가 true일 때만 BottomSheet 렌더링 */}
            {showBottomSheet && (
                <BottomSheet title="어떻게 장점을 찾아볼까요?" isOpen={isSheetOpen} onClose={closeSheet}>
                    <div className="w-full grid grid-cols-2 gap-2">
                        <button onClick={BottomSheetAiButtonClick} className="aspect-[1/1.33] pt-6 w-full bg-gradient-to-b from-zinc-100 from-0% via-white/0 via-40% to-white/0 to-100% rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
                            <div className="w-full flex flex-col items-center px-3 gap-y-1">
                                <div className="text-[16px] font-semibold line-14 text-grey-30">AI로 찾기</div>
                                <div className="text-center line-14 font-medium text-[13px] text-grey-60">AI가 일상 내용을 토대로<br />장점을 자동으로 찾아요</div>
                            </div>
                            <div className="self-stretch py-2 inline-flex justify-center items-center">
                                <AiCoinIcon className="w-35 h-35" />
                            </div>
                        </button>
                        <button onClick={BottomSheetButtonClick} className="aspect-[1/1.33] pt-6 w-full bg-gradient-to-b from-zinc-100 from-0% via-white/0 via-40% to-white/0 to-100% rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
                            <div className="w-full flex flex-col items-center px-3 gap-y-1">
                                <div className="text-[16px] font-semibold line-14 text-grey-30">직접 찾기</div>
                                <div className="text-center line-14 font-medium text-[13px] text-grey-60">일상을 돌아보며,<br />직접 내 장점을 골라봐요</div>
                            </div>
                            <div className="self-stretch py-2 inline-flex justify-center items-center">
                                <SearchCoinIcon className="w-35 h-35" />
                            </div>
                        </button>
                    </div>
                </BottomSheet>
            )}
        </div>
    );
}