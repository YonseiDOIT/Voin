
import { useEffect, useState } from 'react';
import { useCaseReviewStore } from '../../store/useCaseReviewStore';
import { useNavigate } from 'react-router-dom';

interface AiClassifyProps {
    nextPath: string;
}

const AiClassify = ({ nextPath }: AiClassifyProps) => {
    const writtenCase1 = useCaseReviewStore((state) => state.data.writtenCase1);
    const setCaseReviewData = useCaseReviewStore((state) => state.setData);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchClassify = async () => {
            if (!writtenCase1) return;
            try {
                const response = await fetch('/api/gpt/classify', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ text: writtenCase1 })
                });
                if (!response.ok) throw new Error('API error');
                const data = await response.json();
                setCaseReviewData({ classify: data });
                navigate(nextPath);
            } catch (e) {
                // 에러 처리 필요시 추가
            } finally {
                setLoading(false);
            }
        };
        fetchClassify();
    }, [writtenCase1, nextPath, setCaseReviewData, navigate]);

    return (
        <div className="w-full h-full">
            <div className="flex flex-col items-center justify-center w-full mb-[200px]">
                <span className="text-[20px] font-semibold text-grey-15">
                    {loading
                        ? `그 순간에 빛났던\n장점을 살펴보는 중이에요`
                        : '분석 결과를 가져오는 중...'}
                </span>
            </div>
        </div>
    );
};

export default AiClassify;