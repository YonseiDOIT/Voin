import { useEffect, useState } from "react";

interface NotificationItemProps {
    title: string;
    content: string;
    imageURL?: string;
    timestamp: number;
}

const NotificationItem = (props: NotificationItemProps) => {
    const { title, content, imageURL, timestamp } = props;
    const format = new Intl.RelativeTimeFormat("ko", {
        numeric: "auto",
    });

    const [ timeAgo, setTimeAgo ] = useState<string>("방금 전");

    useEffect(() => {
        const intervalCallback = () => {
            const newPassedTime = Date.now() - timestamp;
            const newUnit: Intl.RelativeTimeFormatUnit = newPassedTime >= 1000 * 60 * 60 * 24 * 365 ? "year" :
                newPassedTime >= 1000 * 60 * 60 * 24 * 30 ? "month" :
                newPassedTime >= 1000 * 60 * 60 * 24 ? "day" :
                newPassedTime >= 1000 * 60 * 60 ? "hour" :
                newPassedTime >= 1000 * 60 ? "minute" : "second";

            setTimeAgo(format.format(-Math.floor(newPassedTime / (newUnit === "year" ? (1000 * 60 * 60 * 24 * 365) :
                newUnit === "month" ? (1000 * 60 * 60 * 24 * 30) :
                newUnit === "day" ? (1000 * 60 * 60 * 24) :
                newUnit === "hour" ? (1000 * 60 * 60) :
                newUnit === "minute" ? (1000 * 60) : (1000))), newUnit));
        }
        intervalCallback(); // 초기 호출로 즉시 업데이트
        const intervalId = setInterval(intervalCallback, 1000);

        return () => {
            clearInterval(intervalId);
        };
    }, []);

    return (
        <div className="flex flex-row items-center w-full">
            {/* 임시 이미지 대체 */}
            <div className="w-[50px] h-[50px] rounded-full ml-[25px] bg-[#d9d9d9]">
                {imageURL && <img src={imageURL} alt={title} />}
            </div>
            <div className="flex flex-col justify-between ml-4 h-[45px]">
                <div className="text-[#868a93] text-[13px] font-medium">{title}</div>
                <div className="text-black text-[15px]">{content}</div>
            </div>
            <span className="self-start mt-[17.5px] mr-[25px] ml-auto text-[#aeb0b7] text-[13px]">{timeAgo}</span>
        </div>
    );
}

export default NotificationItem;
