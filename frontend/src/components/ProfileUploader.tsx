import { useState, useRef } from 'react';
import ReactCrop, { type Crop, centerCrop, makeAspectCrop } from 'react-image-crop';
import 'react-image-crop/dist/ReactCrop.css';

import CameraIcon from '../assets/svgs/ProfileUploader/camera.svg?react';
import DefaultIcon from '../assets/svgs/ProfileUploader/default.svg?react';


// 캔버스에 크롭된 이미지를 그리는 헬퍼 함수
function canvasPreview(image: HTMLImageElement, canvas: HTMLCanvasElement, crop: Crop) {
    const ctx = canvas.getContext('2d');
    if (!ctx) {
        throw new Error('No 2d context');
    }


    const scaleX = image.naturalWidth / image.width;
    const scaleY = image.naturalHeight / image.height;
    const pixelRatio = window.devicePixelRatio;


    canvas.width = Math.floor(crop.width * scaleX * pixelRatio);
    canvas.height = Math.floor(crop.height * scaleY * pixelRatio);


    ctx.scale(pixelRatio, pixelRatio);
    ctx.imageSmoothingQuality = 'high';


    const cropX = crop.x * scaleX;
    const cropY = crop.y * scaleY;


    const centerX = image.naturalWidth / 2;
    const centerY = image.naturalHeight / 2;


    ctx.save();
    ctx.translate(-cropX, -cropY);
    ctx.translate(centerX, centerY);
    ctx.translate(-centerX, -centerY);
    ctx.drawImage(
        image,
        0,
        0,
        image.naturalWidth,
        image.naturalHeight,
        0,
        0,
        image.naturalWidth,
        image.naturalHeight
    );
    ctx.restore();
}


export default function ProfileUploader() {
    const [imgSrc, setImgSrc] = useState<string | null>(null);
    const [crop, setCrop] = useState<Crop>();
    const [completedCrop, setCompletedCrop] = useState<Crop>();
    const [croppedImageUrl, setCroppedImageUrl] = useState<string | null>(null);


    const fileInputRef = useRef<HTMLInputElement>(null);
    const imgRef = useRef<HTMLImageElement>(null);
    const previewCanvasRef = useRef<HTMLCanvasElement>(null);

    // 이미지 파일 선택 시 호출
    const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            const reader = new FileReader();
            reader.addEventListener('load', () => setImgSrc(String(reader.result)));
            reader.readAsDataURL(e.target.files?.[0] as Blob);
        }
    };

    // 이미지 로드 시 크롭 영역 초기화
    const onImageLoad = (e: React.SyntheticEvent<HTMLImageElement>) => {
        const { width, height } = e.currentTarget;

        // 1. 초기 크롭 영역을 퍼센트(%) 단위로 생성
        const initialCrop = centerCrop(
            makeAspectCrop({ unit: '%', width: 90 }, 1, width, height),
            width,
            height
        );
        setCrop(initialCrop); // UI에 표시될 크롭 영역은 % 단위로 설정

        // 2. 최종 결과물에 사용할 크롭 정보는 픽셀(px) 단위로 직접 변환하여 저장
        setCompletedCrop({
            unit: 'px',
            x: (initialCrop.x * width) / 100,
            y: (initialCrop.y * height) / 100,
            width: (initialCrop.width * width) / 100,
            height: (initialCrop.height * height) / 100,
        });
    };

    // '등록' 버튼 클릭 시
    const handleUpload = async () => {
        const image = imgRef.current;
        const previewCanvas = previewCanvasRef.current;
        if (!image || !previewCanvas || !completedCrop) {
            throw new Error('Crop canvas does not exist');
        }


        canvasPreview(image, previewCanvas, completedCrop);


        previewCanvas.toBlob((blob) => {
            if (!blob) {
                throw new Error('Failed to create blob');
            }
            const url = URL.createObjectURL(blob);
            setCroppedImageUrl(url);
            setImgSrc(null); // 모달 닫기
        }, 'image/png');
    };


    // '취소' 버튼 클릭 시
    const handleCancel = () => {
        setImgSrc(null);
    };


    return (
        <div className="flex flex-col items-center justify-center">
            {/* 프로필 이미지 표시 영역 */}
            <div className="relative mb-6">
                <div className="w-40 h-40 rounded-full overflow-hidden bg-gray-200 flex items-center justify-center">
                    {croppedImageUrl ? (
                        <img
                            src={croppedImageUrl}
                            alt="Cropped Profile"
                            className="w-full h-full object-cover"
                        />
                    ) : (
                        <div className='w-32 h-32 bg-gray-200 rounded-full p-8'>
                            <DefaultIcon className="w-full h-full object-cover" />
                        </div>
                    )}
                    <button
                        onClick={() => fileInputRef.current?.click()}
                        className="absolute right-0 bottom-0 w-11 h-11 p-2.5 bg-white rounded-3xl outline-1 outline-offset-[-1px] outline-gray-200 inline-flex justify-start items-center gap-2">
                        <CameraIcon className="w-full h-full object-cover" />
                    </button>
                </div>
                <input
                    type="file"
                    ref={fileInputRef}
                    onChange={onFileChange}
                    className="hidden"
                    accept="image/*"
                />
            </div>


            {/* 이미지 크롭 모달 */}
            {imgSrc && (
                <div className="fixed inset-0 bg-black flex flex-col z-[9999]">
                    {/* 헤더 */}
                    <header className="relative flex items-center justify-center p-4 bg-white"
                        style={{ paddingTop: 'env(safe-area-inset-top)' }}
                    >
                        <span className="text-lg">사진 선택</span>
                    </header>

                    {/* 크롭 영역 */}
                    <div className="flex-1 flex items-center justify-center p-4">
                        <ReactCrop
                            crop={crop}
                            onChange={(_, percentCrop) => setCrop(percentCrop)}
                            onComplete={(c) => setCompletedCrop(c)}
                            aspect={1}
                            circularCrop
                        >
                            <img
                                ref={imgRef}
                                src={imgSrc}
                                onLoad={onImageLoad}
                                alt="Crop Target"
                                className="max-h-[70vh] object-contain"
                            />
                        </ReactCrop>
                    </div>

                    {/* 푸터 */}
                    <footer className="flex justify-between items-center p-4"
                        style={{ paddingBottom: 'env(safe-area-inset-bottom)' }}
                    >
                        <button onClick={handleCancel} className="text-white text-base px-4 py-2">
                            취소
                        </button>
                        <button onClick={handleUpload} className="text-white text-base px-6 py-2">
                            선택
                        </button>
                    </footer>
                </div>
            )}


            {/* 크롭된 이미지를 그릴 숨겨진 캔버스 */}
            {completedCrop && (
                <canvas
                    ref={previewCanvasRef}
                    className="hidden"
                    style={{
                        objectFit: 'contain',
                        width: completedCrop.width,
                        height: completedCrop.height,
                    }}
                />
            )}
        </div>
    );
}