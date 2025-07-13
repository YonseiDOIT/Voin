import TopNavigation from '../../../components/TopNavigation';
import WritingTip from '../../../components/findCoin/WritingTip';
import LargeTextField from '../../../components/findCoin/LargeTextField';
import ActionButton from '../../../components/ActionButton';
import BottomSheet from '../../../components/BottomSheet';
import { Link } from 'react-router-dom';

import { useState } from 'react';

const TodaysDiary = () => {
    const [isSheetOpen, setIsSheetOpen] = useState(false);

    const openSheet = () => setIsSheetOpen(true);
    const closeSheet = () => setIsSheetOpen(false);

    const [diaryContent, setDiaryContent] = useState<string>('');
    return (
        <div className="h-full w-full flex flex-col pt-4">
            <TopNavigation title='오늘 하루, 어떤 일이 있었나요?' />
            <div className="w-full h-full flex flex-col px-6 gap-y-4">
                <WritingTip />
                <LargeTextField
                    value={diaryContent}
                    onChange={(e) => setDiaryContent(e.target.value)}
                    placeholder="오늘 하루 있었던 일을 자유롭게 적어보세요."
                />
                <div className='w-full mt-2 mb-4'>
                    <ActionButton
                        buttonText="다음"
                        onClick={() => {openSheet()}}
                        disabled={!diaryContent.trim()} // 내용이 없으면 버튼 비활성화
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
                    <Link to="/todays-diary/categories" className="pt-6 w-full bg-gradient-to-t from-white/0 to-gray-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
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

export default TodaysDiary;