// import NotificationList from "@/components/NotificationList";
import NotificationItem from "@/components/notification/NotificationItem";
import { Link } from 'react-router-dom';

const NotificationPage = () => {
    // 알림 아이템 존재 여부를 관리하는 상태 변수 (임시)
    // 실제로는 API 호출 등을 통해 알림 데이터를 가져올 예정.
    // 추후에 코드 수정 예정
    const notificatonItems = 1;

    return (
        <div className="h-full w-full">
            <Link to="/home" className='inline-flex flex-row items-center'>
                <span className="text-lg font-bold">뒤로 가기</span>
            </Link>
            { notificatonItems ?
            <div className="flex flex-col h-full w-full overflow-y-auto gap-y-10">
                <NotificationItem
                    title="새로운 알림"
                    content="알림 상세 내용이 여기에 표시됩니다."
                    timestamp={Date.now()}
                />
                <NotificationItem
                    title="다른 알림"
                    content="또 다른 알림 상세 내용이 여기에 표시됩니다."
                    timestamp={Date.now() - 1000000}
                />
            </div>
            :
            <div className="flex flex-col items-center justify-center h-full w-full">
                <div className="title-n text-grey-70">새로운 알림이 없습니다.</div>
            </div>
            }
        </div>
    );
};

export default NotificationPage;
