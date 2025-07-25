import AdvantageResult from "../../../../../../../../../components/advantageResult/AdvantageResult";
import TopNavigation from "../../../../../../../../../components/common/TopNavigation";
import CaseReviewResult from "../../../../../../../../../components/advantageResult/CaseReviewResult";
import CommentResult from "../../../../../../../../../components/advantageResult/CommentResult";

import { useCaseReviewStore } from "../../../../../../../../../store/useCaseReviewStore";
import { getCategoryTheme } from '../../../../../../../../../components/advantageResult/advantageResultTypes';
import { useNavigate } from "react-router-dom";

const FinalResult = () => {
    const caseReviewData = useCaseReviewStore((state) => state.data);
    const navigate = useNavigate();

    return (
        <div className="w-full h-full flex flex-col">
            <TopNavigation
                title="나의 코인"
                onBackClick={() => { navigate('/home'); }}
            />
            {caseReviewData && (
                <div className="w-full px-6">
                    <div className="w-full mb-8">
                        <AdvantageResult
                            theme={getCategoryTheme(caseReviewData.categoryName ?? '')}
                            category={caseReviewData.categoryName ?? ''}
                            title={caseReviewData.strengthName ?? ''}
                            titleDescription={caseReviewData.strengthDescription ?? ''}
                            description={caseReviewData.classify ?? ''}
                        />
                    </div>
                    <div className="w-full py-2 px-2 mb-2">
                        <span className="w-full line-14 text-[20px] font-semibold text-grey-15">
                            코인이 발견된 기억 속 순간
                        </span>
                    </div>
                    <div className="w-full mb-8">
                        <CaseReviewResult
                            data={{
                                uploadedImage: caseReviewData.uploadedImage,
                                caseName: caseReviewData.caseName ?? '',
                                writtenCase1: caseReviewData.writtenCase1 ?? '',
                                writtenCase2: caseReviewData.writtenCase2 ?? ''
                            }}
                        />
                    </div>
                    <div className="w-full py-2 px-2 mb-2">
                        <span className="w-full line-14 text-[20px] font-semibold text-grey-15">
                            코인이 발견된 기억 속 순간
                        </span>
                    </div>
                    <div className="w-full mb-18">
                        <CommentResult
                            comment={caseReviewData.comment}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};

export default FinalResult;
