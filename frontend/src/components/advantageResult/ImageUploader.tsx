import { useCaseReviewStore } from '../../store/useCaseReviewStore';
import React, { useRef, useState } from 'react';

import CameraIcon from '../../assets/svgs/ProfileUploader/camera.svg?react';

const ImageUploader = () => {
    const [previewUrl, setPreviewUrl] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);
    const setCaseReviewData = useCaseReviewStore((state) => state.setData);

    const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                const result = reader.result as string;
                setPreviewUrl(result);
                setCaseReviewData({ uploadedImage: result });
            };
            reader.readAsDataURL(file);
        }
    };

    const handleClick = () => {
        fileInputRef.current?.click();
    };

    const handleRemoveImage = (e: React.MouseEvent<HTMLButtonElement>) => {
        e.stopPropagation();
        setPreviewUrl(null);
        setCaseReviewData({ uploadedImage: undefined });
        if (fileInputRef.current) fileInputRef.current.value = "";
    };

    return (
        <div
            className="w-full h-[190px] group relative flex items-center justify-center overflow-hidden rounded-2xl bg-grey-97 outline-2 outline-grey-96"
            onClick={handleClick}
        >
            <input
                type="file"
                accept="image/*"
                ref={fileInputRef}
                className="hidden"
                onChange={handleImageChange}
            />
            {previewUrl ? (
                <div className='w-full h-full relative'>
                    <img
                        src={previewUrl}
                        alt="미리보기"
                        className="w-full h-full object-cover rounded-2xl"
                    />
                    <button
                        type="button"
                        className="absolute top-2 right-2 bg-black bg-opacity-50 rounded-full px-2 shadow-2xl transition"
                        onClick={handleRemoveImage}
                        aria-label="이미지 제거"
                    >
                        <span className="text-lg font-bold text-white">×</span>
                    </button>
                </div>
            ) : (
                <div className="w-full h-full flex flex-col items-center justify-center py-8 px-4">
                    <div className="p-4 rounded-full bg-white outline-1 outline-grey-97 flex items-center justify-center mb-2">
                        <CameraIcon className="w-8 h-8" />
                    </div>
                    <div className="text-center line-15 text-[15px] text-grey-70">
                        순간을 더 생생하게 기억할 수 있도록<br />
                        관련 사진을 추가할 수 있어요.
                    </div>
                </div>
            )}
        </div>
    );
};

export default ImageUploader;
