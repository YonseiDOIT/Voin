import StrengthSelector from '../../../../../../components/findCoin/StrengthSelector';
import { useNavigate } from 'react-router-dom';
import { useCaseReviewStore } from '../../../../../../store/useCaseReviewStore';
import { useEffect, useState } from 'react';

// API 응답 타입 정의
interface KeywordData {
    name: string;
    description: string;
    coinName: string;
    fullInfo: string;
}

interface ApiResponse {
    success: boolean;
    message: string;
    data: {
        [categoryName: string]: KeywordData[];
    };
    errorCode: string | null;
}

export default function CaseReviewCategory() {
    const navigate = useNavigate();
    const [keywordData, setKeywordData] = useState<{ [key: string]: KeywordData[] }>({});
    const [isLoading, setIsLoading] = useState(true);
    const setCaseReviewData = useCaseReviewStore((state) => state.setData);

    useEffect(() => {
        (async () => {
            setIsLoading(true);
            try {
                const response = await fetch('/api/master/keywords');
                if (!response.ok) return;
                const apiResponse: ApiResponse = await response.json();
                if (apiResponse.success) setKeywordData(apiResponse.data);
            } finally {
                setIsLoading(false);
            }
        })();
    }, []);

    const handleNext = (selectedItems: { category: string; item: string }[]) => {
        if (selectedItems.length === 0 || isLoading) return;
        const selectedItem = selectedItems[0];
        const categoryData = keywordData[selectedItem.category.replace('category_', '')];
        if (!categoryData) return;
        const selectedKeyword = categoryData.find(keyword => keyword.name === selectedItem.item);
        if (!selectedKeyword) return;
        setCaseReviewData({
            categoryName: selectedItem.category.replace('category_', ''),
            strengthName: selectedKeyword.name,
            strengthDescription: selectedKeyword.description,
            fullDescription: selectedKeyword.fullInfo,
            coinName: selectedKeyword.coinName
        });
        navigate('/case-review/advantage-result');
    };

    return (
        <StrengthSelector
            title="그 순간, 어떤 장점이 드러났나요?"
            caption="가장 돋보였다고 생각하는 장점 하나만 골라주세요."
            onNext={handleNext}
        />
    );
}
