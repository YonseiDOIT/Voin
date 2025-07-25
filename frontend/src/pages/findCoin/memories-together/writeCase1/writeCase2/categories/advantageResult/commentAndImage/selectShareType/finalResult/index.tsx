import AdvantageResult from "@/components/advantageResult/AdvantageResult";
import TopNavigation from "@/components/common/TopNavigation";
import CaseReviewResult from "@/components/advantageResult/CaseReviewResult";
import CommentResult from "@/components/advantageResult/CommentResult";
import ToastMessage from "@/components/ToastMessage";

import { useActivityStore } from "@/store/useActivityStore";
import { getCategoryTheme } from '@/components/advantageResult/advantageResultTypes';
import { useNavigate } from "react-router-dom";

const FinalResult = () => {
    const activityData = useActivityStore((state) => state.data);
    const navigate = useNavigate();

    return (
        <div className="w-full h-full flex flex-col">
            <TopNavigation
                title="친구의 코인"
                onBackClick={() => { navigate('/home'); }}
            />
            {activityData && (
                <div className="w-full px-6">
                    <div className="w-full mb-8">
                        <AdvantageResult
                            theme={getCategoryTheme(activityData.categoryName ?? '')}
                            category={activityData.categoryName ?? ''}
                            title={activityData.strengthName ?? ''}
                            titleDescription={activityData.strengthDescription ?? ''}
                            description={activityData.classify ?? ''}
                        />
                    </div>
                    <div className="w-full py-2 px-2 mb-2">
                        <span className="w-full line-14 text-[20px] font-semibold text-grey-15">
                            코인이 발견된 친구의 순간
                        </span>
                    </div>
                    <div className="w-full mb-8">
                        <CaseReviewResult
                            data={{
                                uploadedImage: activityData.uploadedImage,
                                caseName: activityData.caseName ?? '',
                                writtenCase1: activityData.writtenCase1 ?? '',
                                writtenCase2: activityData.writtenCase2 ?? ''
                            }}
                        />
                    </div>
                    <div className="w-full py-2 px-2 mb-2">
                        <span className="w-full line-14 text-[20px] font-semibold text-grey-15">
                            친구에게 한마디
                        </span>
                    </div>
                    <div className="w-full mb-18">
                        <CommentResult
                            comment={activityData.comment}
                        />
                    </div>
                </div>
            )}
            <ToastMessage message="코인 찾기 및 전송이 완료되었어요!" />
        </div>
    );
};

export default FinalResult;