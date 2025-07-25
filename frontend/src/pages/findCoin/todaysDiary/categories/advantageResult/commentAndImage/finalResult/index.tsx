import AdvantageResult from "@/components/advantageResult/AdvantageResult";
import TopNavigation from "@/components/common/TopNavigation";
import TodaysResult from "@/components/advantageResult/ToDaysResult";
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
                title="나의 코인"
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
                            코인이 발견된 기억 속 순간
                        </span>
                    </div>
                    <div className="w-full mb-8">
                        <TodaysResult
                            data={{
                                uploadedImage: activityData.uploadedImage,
                                writtenCase1: activityData.writtenCase1 ?? '',
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
                            comment={activityData.comment}
                        />
                    </div>
                </div>
            )}
            <ToastMessage message="코인 찾기가 완료되었어요!" />
        </div>
    );
};

export default FinalResult;