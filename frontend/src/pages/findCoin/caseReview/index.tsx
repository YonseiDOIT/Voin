import SelectCaseCategory from "../../../components/caseReview/SelectCaseCategory"
import TopNavigation from "../../../components/common/TopNavigation";
import ActionButton from "../../../components/common/ActionButton";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import HomeIcon from '../../../assets/svgs/caseReview/homeIcon.svg?react';
import HandShakeIcon from '../../../assets/svgs/caseReview/handshakeIcon.svg?react';
import BagIcon from '../../../assets/svgs/caseReview/bagIcon.svg?react';
import FireIcon from '../../../assets/svgs/caseReview/fireIcon.svg?react';
import ParachuteIcon from '../../../assets/svgs/caseReview/parachuteIcon.svg?react';
import RainbowIcon from '../../../assets/svgs/caseReview/rainbowIcon.svg?react';

const categories = [
    {
        title: '개인적 일상,\n습관, 선택',
        subtitle: '개인적인 순간',
        SvgComponent: () => <HomeIcon />
    },
    {
        title: '인간관계 속\n대화, 행동',
        subtitle: '관계 속의 순간',
        SvgComponent: () => <HandShakeIcon />
    },
    {
        title: '협력,\n함께한 성취',
        subtitle: '힘을 합친 순간',
        SvgComponent: () => <BagIcon />
    },
    {
        title: '집중하고\n몰두한 일',
        subtitle: '몰입하는 순간',
        SvgComponent: () => <FireIcon />
    },
    {
        title: '낮설고\n새로운 경험',
        subtitle: '도전하는 순간',
        SvgComponent: () => <ParachuteIcon />
    },
    {
        title: '다른 특별한 순간',
        subtitle: '그 외의 순간',
        SvgComponent: () => <RainbowIcon />
    }
];

const CaseReviewPage = () => {
    const navigate = useNavigate();

    const [selectedCategoryIndex, setSelectedCategoryIndex] = useState<number | null>(null);

    const handleCategorySelect = (index: number | null) => {
        setSelectedCategoryIndex(index);
    };

    return (
        <div className="w-full h-full flex flex-col">
            <TopNavigation
                title="기억에 남는 순간을 떠올려볼까요?"
                caption="상대의 장점이 드러났던 순간의 상황을 선택해주세요."
            />

            <div className="px-6 mt-4 overflow-y-auto pb-32">
                <SelectCaseCategory
                    categories={categories}
                    onCategorySelect={handleCategorySelect}
                />
            </div>

            <div className='fixed bottom-0 left-0 right-0 flex flex-col w-full max-w-md mx-auto'>
                <div className="h-8 w-full bg-gradient-to-b from-[#F7F7F8]/0 to-grey-99"></div>
                <div className='relative bg-grey-99 px-6' style={{ paddingBottom: 'calc(env(safe-area-inset-bottom) + 1rem)' }}>
                    <ActionButton
                        buttonText="다음"
                        onClick={() => {navigate('/case-review/write')}}
                        disabled={selectedCategoryIndex === null} // 선택된 카테고리가 없으면 버튼 비활성화
                    />
                </div>
            </div>
        </div>
    );
};

export default CaseReviewPage;