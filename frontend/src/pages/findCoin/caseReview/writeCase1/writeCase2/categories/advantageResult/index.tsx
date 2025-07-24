import AdvantageResult from '../../../../../../../components/advantageResult/AdvantageResult';
import CaseReviewResult from '../../../../../../../components/advantageResult/CaseReviewResult';
import { getCategoryTheme } from '../../../../../../../components/advantageResult/advantageResultTypes';
import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { useCaseReviewStore } from '../../../../../../../store/useCaseReviewStore';

// 케이스 리뷰 + 장점 선택 통합 데이터 타입 정의
interface CaseReviewAdvantageData {
    // 케이스 리뷰 작성 데이터
    categoryName: string; // 선택한 카테고리명
    caseName: string;          // 선택한 순간 케이스
    writtenCase1: string;     // "그때 나는 어떤 행동을 했었나요?" 답변
    writtenCase2: string;     // "내 행동에 대해 어떻게 생각하나요?" 답변
    // 선택된 장점 데이터
    strengthName: string;      // 선택된 강점명
    strengthDescription: string; // 강점 설명
    coinColor: string;         // 코인 색상
    fullDescription: string;   // 상세 설명
    coinName: string;         // 코인명
}

const AdvantageResultPage = () => {

    const navigate = useNavigate();
    const caseReviewData = useCaseReviewStore((state) => state.data);

    useEffect(() => {
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
            {/* 케이스 리뷰 작성 내용 표시 */}
            <CaseReviewResult 
                data={{
                    caseName: caseReviewData.caseName || '',
                    writtenCase1: caseReviewData.writtenCase1 || '',
                    writtenCase2: caseReviewData.writtenCase2 || ''
                }}
            />
            
            {/* 장점 결과 표시 */}
            <div className="px-4">
                <AdvantageResult
                    theme={getCategoryTheme(caseReviewData.categoryName)}
                    category={caseReviewData.categoryName}
                    title={caseReviewData.strengthName}
                    titleDescription={caseReviewData.strengthDescription}
                    description={caseReviewData.fullDescription}
                />
            </div>
        </div>
    );
};

export default AdvantageResultPage;
