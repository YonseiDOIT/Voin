import StrengthSelector from '../../../../../../components/findCoin/StrengthSelector';
import { useNavigate, useLocation } from 'react-router-dom';
import { useCallback, useEffect, useState } from 'react';

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
    const location = useLocation();
    const [keywordData, setKeywordData] = useState<{ [key: string]: KeywordData[] }>({});
    const [isLoading, setIsLoading] = useState(true);

    // 키워드 데이터 fetch
    const fetchKeywordData = useCallback(async () => {
        try {
            setIsLoading(true);
            const response = await fetch('/api/master/keywords');
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const apiResponse: ApiResponse = await response.json();
            
            if (apiResponse.success) {
                setKeywordData(apiResponse.data);
            } else {
                console.error('Failed to fetch keyword data:', apiResponse.message);
            }
        } catch (error) {
            console.error('Error fetching keyword data:', error);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchKeywordData();
    }, [fetchKeywordData]);

    const handleNext = (selectedItems: { category: string; item: string }[]) => {
        if (selectedItems.length > 0 && !isLoading) {
            const selectedItem = selectedItems[0]; // 하나만 선택되므로 첫 번째 항목
            
            // 선택된 카테고리에서 해당 키워드 데이터 찾기
            const categoryData = Object.entries(keywordData).find(([categoryName]) => 
                categoryName === selectedItem.category.replace('category_', '')
            );
            
            if (categoryData) {
                const [categoryName, keywords] = categoryData;
                const selectedKeyword = keywords.find(keyword => keyword.name === selectedItem.item);
                
                if (selectedKeyword) {
                    // 기존 state에서 케이스 리뷰 데이터 가져오기
                    const existingData = location.state || {};
                    
                    // advantage-result 페이지로 모든 데이터 전달
                    navigate('/case-review/advantage-result', {
                        state: {
                            // 케이스 리뷰 작성 데이터 (CaseReviewResult용)
                            categoryName: existingData.categoryName || categoryName,
                            writtenCase1: existingData.writtenCase1 || '',
                            writtenCase2: existingData.writtenCase2 || '',
                            strengthName: selectedKeyword.name,
                            strengthDescription: selectedKeyword.description,
                            fullDescription: selectedKeyword.fullInfo,
                            coinName: selectedKeyword.coinName
                        }
                    });
                } else {
                    console.error('Selected keyword not found in data');
                }
            } else {
                console.error('Selected category not found in data');
            }
        } else {
            console.warn('No items selected or data is still loading');
        }
    };

    return (
        <StrengthSelector
            title="그 순간, 어떤 장점이 드러났나요?"
            caption="가장 돋보였다고 생각하는 장점 하나만 골라주세요."
            onNext={handleNext}
        />
    );
}
