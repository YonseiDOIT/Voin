import TopNavigation from '../common/TopNavigation';
import WritingTip from './WritingTip';
import LargeTextField from './LargeTextField';
import ActionButton from '../common/ActionButton';
import BottomSheet from '../common/BottomSheet';
import { Link } from 'react-router-dom';
import { useState } from 'react';

interface WritingPromptProps {
    title: string;
    tip: string;
    placeholder: string;
    directLinkTo: string;
}

export default function WritingPrompt({ title, tip, placeholder, directLinkTo }: WritingPromptProps) {
    const [isSheetOpen, setIsSheetOpen] = useState(false);
    const [diaryContent, setDiaryContent] = useState<string>('');
    
    const openSheet = () => setIsSheetOpen(true);
    const closeSheet = () => setIsSheetOpen(false);
    
    const isContentTooShort = diaryContent.trim().length > 0 && diaryContent.trim().length < 40;
    const errorMessage = isContentTooShort ? "최소 40자 이상 입력해주세요." : null;
    
    return (
        <div className="h-full w-full flex flex-col pt-4">
            <TopNavigation title={title} />
            <div className="w-full h-full flex flex-col px-6 gap-y-4">
                <WritingTip tip={tip} />
                <LargeTextField
                    value={diaryContent}
                    onChange={(e) => setDiaryContent(e.target.value)}
                    placeholder={placeholder}
                    maxLength={500}
                    error={errorMessage}
                />
                <div className='w-full mt-2 mb-4'>
                    <ActionButton
                        buttonText="다음"
                        onClick={openSheet}
                        disabled={!diaryContent.trim() || diaryContent.trim().length < 40}
                    />
                </div>
            </div>

            <BottomSheet title="어떻게 장점을 찾아볼까요?" isOpen={isSheetOpen} onClose={closeSheet}>
                <div className="w-full grid grid-cols-2 gap-2">
                    <div className="pt-6 w-full bg-gradient-to-t from-white/0 to-gray-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
                        <div className="w-full flex flex-col items-center px-3 gap-y-1">
                            <div className="title-n font-semibold text-[#00BDDE]">AI로 찾기</div>
                            <div className="text-center button-n text-grey-60">AI가 일상 내용을 토대로<br />장점을 자동으로 찾아요</div>
                        </div>
                        <div className="self-stretch h-28 py-4 inline-flex justify-center items-center">
                            <img className="w-28 h-28" src="https://placehold.co/110x110" />
                        </div>
                    </div>
                    <Link to={directLinkTo} className="pt-6 w-full bg-gradient-to-t from-white/0 to-gray-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
                        <div className="w-full flex flex-col items-center px-3 gap-y-1">
                            <div className="title-n font-semibold text-[#00BDDE]">직접 찾기</div>
                            <div className="text-center button-n text-grey-60">일상을 돌아보며,<br />직접 내 장점을 골라봐요</div>
                        </div>
                        <div className="self-stretch h-28 py-4 inline-flex justify-center items-center">
                            <img className="w-28 h-28" src="https://placehold.co/110x110" />
                        </div>
                    </Link>
                </div>
            </BottomSheet>
        </div>
    );
}
