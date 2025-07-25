interface TodaysResultProps {
    data: {
        writtenCase1: string;
        uploadedImage?: string;
    };
}


// 오늘 날짜를 "7월 9일 수요일" 형태로 반환하는 함수
function formatToday(date: Date = new Date()): string {
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const daysKor = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
    const dayOfWeek = daysKor[date.getDay()];
    return `${month}월 ${day}일 ${dayOfWeek}`;
}

const TodaysResult: React.FC<TodaysResultProps> = ({ data }) => {
    const todayString = formatToday();
    return (
        <div className="w-full flex flex-col bg-white rounded-[32px] py-6 px-[10px] shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]">
            <div className="px-2 text-[14px] text-grey-80 font-medium mb-1">{todayString}</div>
            <div className="self-stretch h-0 mx-2 my-4 outline-1 outline-offset-[-0.50px] outline-gray-200"></div>
            {data.uploadedImage && (
                <div className="mb-4">
                    <img src={data.uploadedImage} alt="Uploaded" className="w-full h-[190px] rounded-[22px] object-cover" />
                </div>
            )}
            <div className="px-2 text-[15px] line-16 font-medium text-grey-40">{data.writtenCase1}</div>
        </div>
    );
}

export default TodaysResult;