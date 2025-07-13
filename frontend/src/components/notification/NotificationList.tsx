interface NotificationListProps {
    children?: React.ReactNode;
    className?: string;
}

const NotificationList = (props: NotificationListProps) => {
    const {
        children,
        className = "",
    } = props;

    const baseClasses = "m-0 p-0 w-full";

    const conditionalClasses = children
        ? ""
        : "flex flex-col items-center justify-center h-full";

    return (
        <ul className={`${baseClasses} ${conditionalClasses} ${className || ''}`}>
            {
                children ?
                    children
                    :
                    <div className="title-n text-grey-70">새로운 알림이 없어요</div>
            }
        </ul>
    )
}

export default NotificationList;
