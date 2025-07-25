import AdvantageResult from '../../../../../../../components/advantageResult/AdvantageResult';
import CaseReviewResult from '../../../../../../../components/advantageResult/CaseReviewResult';
import ActionButton from '../../../../../../../components/common/ActionButton';

import { getCategoryTheme } from '../../../../../../../components/advantageResult/advantageResultTypes';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useCaseReviewStore } from '../../../../../../../store/useCaseReviewStore';

const AdvantageResultPage = () => {

    const navigate = useNavigate();
    const caseReviewData = useCaseReviewStore((state) => state.data);


    useEffect(() => {
        // zustand 데이터 콘솔 출력
        console.log('[CaseReviewData zustand]', caseReviewData);
        // 데이터가 없으면 이전 페이지로 리다이렉트
        if (!caseReviewData || !caseReviewData.caseName) {
            console.warn('케이스 리뷰 데이터가 없습니다. 강점 선택 페이지로 돌아갑니다.');
            navigate('/case-review/categories', { replace: true });
        }
    }, [caseReviewData, navigate]);

    // 데이터가 없으면 로딩 상태 표시
    if (!caseReviewData || !caseReviewData.caseName) {
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
                    theme={getCategoryTheme(caseReviewData.categoryName ?? '')}
                    category={caseReviewData.categoryName ?? ''}
                    title={caseReviewData.strengthName ?? ''}
                    titleDescription={caseReviewData.strengthDescription ?? ''}
                    description={caseReviewData.classify ?? ''}
                />
            </div>
            <div className="w-full px-6">
                <div className="w-full py-2 px-2 mb-2">
                    <span className="w-full line-14 text-[20px] font-semibold text-grey-15">
                        코인이 발견된 기억 속 순간
                    </span>
                </div>
                <CaseReviewResult
                    data={{
                        caseName: caseReviewData.caseName ?? '',
                        writtenCase1: caseReviewData.writtenCase1 ?? '',
                        writtenCase2: caseReviewData.writtenCase2 ?? ''
                    }}
                />
                <div className="w-full pb-4 pt-8 mt-24">
                    {/* TODO: 다음 단계로 버튼 추가 */}
                    <ActionButton
                        buttonText='다음'
                        onClick={() => navigate('/case-review/comment-and-image')}
                        disabled={!caseReviewData.caseName}
                    />
                </div>
            </div>
        </div>
    );
};

export default AdvantageResultPage;
