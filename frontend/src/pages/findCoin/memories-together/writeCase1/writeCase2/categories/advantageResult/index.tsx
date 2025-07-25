import AdvantageResult from '@/components/advantageResult/AdvantageResult';
import CaseReviewResult from '@/components/advantageResult/CaseReviewResult';
import ActionButton from '@/components/common/ActionButton';

import { getCategoryTheme } from '@/components/advantageResult/advantageResultTypes';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useActivityStore } from '@/store/useActivityStore';

const AdvantageResultPage = () => {

    const navigate = useNavigate();
    const activityData = useActivityStore((state) => state.data);


    useEffect(() => {
        // zustand 데이터 콘솔 출력
        console.log('[CaseReviewData zustand]', activityData);
        // 데이터가 없으면 이전 페이지로 리다이렉트
        if (!activityData || !activityData.caseName) {
            console.warn('케이스 리뷰 데이터가 없습니다. 강점 선택 페이지로 돌아갑니다.');
            navigate('/memories-together/categories', { replace: true });
        }
    }, [activityData, navigate]);

    // 데이터가 없으면 로딩 상태 표시
    if (!activityData || !activityData.caseName) {
        return (
            <div className="w-full h-full flex items-center justify-center">
                <div className="text-center">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500 mx-auto mb-4"></div>
                    <p className="text-gray-500">데이터를 불러오는 중...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="w-full h-full overflow-y-auto flex flex-col">
            <div className="w-full px-6 pb-10">
                <div className="w-full pt-12 pb-6 mb-2">
                    <span className="w-full pr-2 line-14 text-[24px] font-semibold text-grey-15">
                        그 순간, 이런 장점이 드러났어요
                    </span>
                </div>
                <AdvantageResult
                    theme={getCategoryTheme(activityData.categoryName ?? '')}
                    category={activityData.categoryName ?? ''}
                    title={activityData.strengthName ?? ''}
                    titleDescription={activityData.strengthDescription ?? ''}
                    description={activityData.classify ?? ''}
                />
            </div>
            <div className="w-full px-6 pb-24">
                <div className="w-full py-2 px-2 mb-2">
                    <span className="w-full line-14 text-[20px] font-semibold text-grey-15">
                        코인이 발견된 기억 속 순간
                    </span>
                </div>
                <CaseReviewResult
                    data={{
                        caseName: activityData.caseName ?? '',
                        writtenCase1: activityData.writtenCase1 ?? '',
                        writtenCase2: activityData.writtenCase2 ?? ''
                    }}
                />
                <div className="fixed bottom-4 left-0 w-full pb-4 px-6 pt-8">
                    {/* TODO: 다음 단계로 버튼 추가 */}
                    <ActionButton
                        buttonText='다음'
                        onClick={() => navigate('/todays-diary/comment-and-image')}
                        disabled={!activityData}
                    />
                </div>
            </div>
        </div>
    );
};

export default AdvantageResultPage;