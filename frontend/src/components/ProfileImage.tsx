// src/components/ProfileImage.tsx
import React, { useState, useEffect } from 'react';
import { useAuthStore } from '@/store/useAuthStore';
import 아미새image from '@/assets/images/9e476176d040dbba92dedbb1e7f3d47eee4b0f12.png';

interface ProfileImageProps {
    /** 우선 사용될 이미지 URL (없으면 전역 userInfo.profileImage 사용) */
    src?: string | null;
    /** 대체 텍스트 (없으면 userInfo.nickname → '프로필') */
    alt?: string | null;
    /** 정사각 픽셀 크기 (기본 32px) */
    size?: number;
    /** 외부에서 클래스 추가하고 싶을 때 */
    className?: string;
}

const ProfileImage: React.FC<ProfileImageProps> = ({
                                                       src,
                                                       alt,
                                                       size = 32,
                                                       className = '',
                                                   }) => {
    const userInfo = useAuthStore((state) => state.userInfo);
    const [imageError, setImageError] = useState(false);

    // 실제 사용할 이미지 URL/ALT 결정
    const imageUrl = (src ?? undefined) || userInfo?.profileImage || undefined;
    const altText = alt ?? userInfo?.nickname ?? '프로필';

    // 이미지 URL이 바뀌면 에러 상태 초기화
    useEffect(() => {
        setImageError(false);
    }, [imageUrl]);

    const handleError = () => setImageError(true);
    const showImage = !!imageUrl && !imageError;

    const px = `${size}px`;

    return (
        <div
            className={`rounded-full bg-gray-200 flex items-center justify-center text-gray-500 overflow-hidden ${className}`}
            style={{ width: px, height: px }}
            aria-label={altText}
        >
            {showImage ? (
                <img
                    src={imageUrl}
                    alt={altText}
                    onError={handleError}
                    loading="lazy"
                    decoding="async"
                    className="h-full w-full object-cover rounded-full"
                    // cross-origin 이미지라면 필요 시 주석 해제
                    // crossOrigin="anonymous"
                    // referrerPolicy="no-referrer"
                />
            ) : (
                <img
                    src={아미새image}
                    alt="대체 이미지"
                    className="h-full w-full object-cover rounded-full"
                    loading="lazy"
                    decoding="async"
                />
            )}
        </div>
    );
};

export default ProfileImage;
