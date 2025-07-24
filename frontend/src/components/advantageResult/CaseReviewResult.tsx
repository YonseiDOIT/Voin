import { useLocation } from 'react-router-dom';

interface CaseReviewResultProps {
    caseName: string;
    writtenCase1: string;
    writtenCase2: string;
}

const CaseReviewResult: React.FC<CaseReviewResultProps> = ({ caseName, writtenCase1, writtenCase2 }) => {
    const location = useLocation();
    const locationData = location.state as CaseReviewResultProps;
    const displayData = locationData || { caseName, writtenCase1, writtenCase2 };
    
    return (
        <div className="bg-gray-50 p-4 m-4 rounded-lg">
            <h3 className="text-lg font-semibold mb-3 text-gray-800">
                케이스 리뷰 결과
            </h3>
            
            {displayData.caseName && (
                <div className="mb-4">
                    <p className="text-sm text-gray-600 mb-1">선택한 순간</p>
                    <p className="text-base font-medium text-gray-800">
                        {displayData.caseName}
                    </p>
                </div>
            )}
            
            {displayData.writtenCase1 && (
                <div className="mb-4">
                    <p className="text-sm text-gray-600 mb-1">케이스 1</p>
                    <p className="text-base text-gray-800">
                        {displayData.writtenCase1}
                    </p>
                </div>
            )}
            
            {displayData.writtenCase2 && (
                <div className="mb-4">
                    <p className="text-sm text-gray-600 mb-1">케이스 2</p>
                    <p className="text-base text-gray-800">
                        {displayData.writtenCase2}
                    </p>
                </div>
            )}
        </div>
    );
};

export default CaseReviewResult;
