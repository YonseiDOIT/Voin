import TopNavigation from "@/components/common/TopNavigation";
import ImageUploader from "@/components/advantageResult/ImageUploader";
import LargeTextField from "@/components/findCoin/LargeTextField";
import ActionButton from "@/components/common/ActionButton";

import { useState } from "react";
import { useActivityStore } from "@/store/useActivityStore";
import { useNavigate } from "react-router-dom";

const CommentAndImage = () => {
    const [comment, setComment] = useState("");
    const setActivityData = useActivityStore((state) => state.setData);
    const navigate = useNavigate();

    return (
        <div className="w-full h-full flex flex-col">
            <TopNavigation title={`그 순간을 기록하며,\n사진이나 한마디를 남겨보세요.`} />

            <div className="w-full h-full flex flex-col px-6 gap-y-4">
                <div className="min-h-[190px]">
                <ImageUploader />
                </div>
                
                <LargeTextField
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    placeholder="다짐, 또는 기억해두고 싶은 한마디를 남겨보세요."
                    maxLength={300}
                />
                <div className="w-full mt-2 mb-4">
                    <ActionButton
                        buttonText="완료"
                        onClick={() => {
                            setActivityData({
                                comment: comment.trim(),
                                uploadedImage: useActivityStore.getState().data.uploadedImage,
                            });
                            navigate('/todays-diary/final-result');
                        }}
                    />
                </div>
            </div>
        </div>
    );
};

export default CommentAndImage;