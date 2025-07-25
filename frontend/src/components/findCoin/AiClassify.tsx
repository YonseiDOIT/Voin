import { useEffect } from 'react';
import { useActivityStore } from '@/store/useActivityStore';
import { useNavigate } from 'react-router-dom';
import { classifyText } from '@/services/gptService';
import { fetchAllStrengths } from '@/services/strengthService';

interface AiClassifyProps {
    nextPath: string;
}

const AiClassify = ({ nextPath }: AiClassifyProps) => {
    const writtenCase1 = useActivityStore((state) => state.data.writtenCase1);
    const setActivityData = useActivityStore((state) => state.setData);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchClassify = async () => {
            if (!writtenCase1) return;
            try {
                const classificationResult = await classifyText(writtenCase1);
                if (!classificationResult.strengthName || !classificationResult.categoryName) {
                    throw new Error("AI 분류 결과가 올바르지 않습니다.");
                }

                const allStrengths = await fetchAllStrengths();
                const categoryData = allStrengths.get(classificationResult.categoryName);
                const keywordData = categoryData?.keywords.find(k => k.name === classificationResult.strengthName);

                if (!categoryData || !keywordData) {
                    throw new Error("분류된 장점 정보를 찾을 수 없습니다.");
                }

                setActivityData({
                    ...classificationResult,
                    strengthDescription: keywordData.description,
                    fullDescription: `코인 설명: ${categoryData.description}`, // API 응답에 fullDescription이 없으므로 임시 처리
                    coinName: categoryData.name,
                    caseName: '오늘의 일기',
                });

                navigate(nextPath);
            } catch (e) {
                // 에러 처리: 사용자에게 알림을 보여주거나, 이전 페이지로 돌려보내는 등의 처리를 할 수 있습니다.
                console.error('Error fetching classification:', e);
                alert('장점을 분석하는 데 실패했습니다. 다시 시도해주세요.');
                navigate("/home"); // 이전 페이지로 돌아가기
            }
        };
        fetchClassify();
    }, [writtenCase1, nextPath, setActivityData, navigate]);

    return (
        <div className="w-full h-full flex flex-col items-center justify-center">
            <span className="text-[20px] font-semibold text-grey-15">
                {`그 순간에 빛났던\n장점을 살펴보는 중이에요`}
            </span>
        </div>
    );
};

export default AiClassify;
