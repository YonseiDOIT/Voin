import AdvantageResult from '../../../components/advantageResult/AdvantageResult';
import { getCategoryTheme } from '../../../components/advantageResult/advantageResultTypes';
import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';

// StrengthSelector에서 넘어올 데이터 타입 정의
interface SelectedStrengthData {
    categoryName: string;      // 코인 카테고리명 (예: "관리와 성장")
    strengthName: string;      // 선택된 강점명 (예: "리더십")
    strengthDescription: string; // 강점 설명
    coinColor: string;         // 코인 색상
    fullDescription: string;   // 상세 설명
    coinName: string;         // 코인명
}

const AdvantageResultPage = () => {
    const location = useLocation();
    const navigate = useNavigate();
    
    // StrengthSelector에서 넘어온 데이터 가져오기
    const selectedStrengthData = location.state as SelectedStrengthData | null;

    // 디버깅용 로그
    useEffect(() => {
        if (selectedStrengthData) {
            console.log('AdvantageResult 페이지로 전달된 데이터:', selectedStrengthData);
        }
    }, [selectedStrengthData]);

    useEffect(() => {
        // 데이터가 없으면 이전 페이지로 리다이렉트
        if (!selectedStrengthData) {
            console.warn('선택된 강점 데이터가 없습니다. 강점 선택 페이지로 돌아갑니다.');
            // 개발 중에는 리다이렉트 대신 경고만 표시
            // navigate('/findCoin/caseReview/categories', { replace: true });
        }
    }, [selectedStrengthData, navigate]);

    // 데이터가 없으면 더미 데이터로 대체 (개발용)
    const displayData = selectedStrengthData || {
        categoryName: "관리와 성장",
        strengthName: "끈기",
        strengthDescription: "포기하지 않고 계속 나아가는 힘",
        coinColor: "blue",
        fullDescription: "어려운 상황에서도 포기하지 않고 목표를 향해 꾸준히 노력하는 당신의 모습이 인상적입니다.",
        coinName: "Blue"
    };

    return (
        <div className="w-full h-full overflow-y-auto flex flex-col px-4">
            <AdvantageResult
                theme={getCategoryTheme(displayData.categoryName)}
                category={displayData.categoryName}
                title={displayData.strengthName}
                titleDescription={displayData.strengthDescription}
                description={displayData.fullDescription}
            />
        </div>
    );
};

export default AdvantageResultPage;
