
interface CaseReviewResultProps {
    data: {
        caseName: string;
        writtenCase1: string;
        writtenCase2: string;
        uploadedImage?: string;
    };
}

const CaseReviewResult: React.FC<CaseReviewResultProps> = ({ data }) => {
    return (
        <div className={`w-full flex flex-col bg-white rounded-[32px] pb-6 px-[10px] shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]
            ${data.uploadedImage ? 'pt-[10px]' : 'pt-2'}`}>
            {data.uploadedImage && (
                <div className="mb-4">
                    <img src={data.uploadedImage} alt="Uploaded" className="w-full h-[190px] rounded-[22px] object-cover" />
                </div>
            )}
            <div className="my-4 px-2">
                <p className="line-15 text-[14px] font-semibold text-grey-80 mb-2">떠올린 상황</p>
                <p className="line-15 text-[15px] font-semibold text-grey-40">
                    {data.caseName ? data.caseName : '선택된 순간이 없습니다.'}
                </p>
            </div>
            <div className="self-stretch h-0 mx-2 outline-1 outline-offset-[-0.50px] outline-gray-200"></div>
            <div className="my-4 px-2">
                <p className="line-15 text-[14px] font-semibold text-grey-80 mb-2">순간 속 행동</p>
                <p className="line-15 text-[15px] font-medium text-grey-40 break-words whitespace-pre-line">
                    {data.writtenCase1 ? data.writtenCase1 : '작성된 내용이 없습니다.'}
                </p>
            </div>
            <div className="self-stretch h-0 mx-2 outline-1 outline-offset-[-0.50px] outline-gray-200"></div>
            <div className="mt-4 px-2">
                <p className="line-15 text-[14px] font-semibold text-grey-80 mb-2">행동에 대한 생각</p>
                <p className="line-15 text-[15px] font-medium text-grey-40 break-words whitespace-pre-line">
                    {data.writtenCase2 ? data.writtenCase2 : '작성된 내용이 없습니다.'}
                </p>
            </div>
        </div>
    );
};

export default CaseReviewResult;
