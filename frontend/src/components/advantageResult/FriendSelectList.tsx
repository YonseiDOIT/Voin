import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import ActionButton from '@/components/common/ActionButton';

// 선택/비선택 아이콘 SVG
const SelectedIcon = () => (
    <svg width="24" height="24" viewBox="0 0 56 56" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="28" cy="28" r="28" fill="#4CC6F9" />
        <path d="M18 29.5L25 36L38 23" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
);
const UnselectedIcon = () => (
    <svg width="24" height="24" viewBox="0 0 56 56" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="28" cy="28" r="26" stroke="#979CA7" strokeWidth="4" fill="white" />
    </svg>
);

interface FriendSelectListProps {
    friends: {
        id: string;
        name: string;
        profileImage?: string;
        userTag: string;
    }[];
    onFriendSelect: (friend: { id: string; name: string; profileImage?: string; userTag: string }) => void
    navigateTo: string;
}

const FriendSelectList: React.FC<FriendSelectListProps> = ({ friends, onFriendSelect, navigateTo }) => {
    const [selectedId, setSelectedId] = useState<string | null>(null);

    const handleSelect = (friend: typeof friends[number]) => {
        setSelectedId(friend.id === selectedId ? null : friend.id);
        onFriendSelect(friend);
    };

    const navigate = useNavigate();

    return (
        <ul className="relative w-full h-[50vh] overflow-y-auto flex flex-col gap-y-6 py-2 mb-8">
            {friends.map(friend => {
                const isSelected = friend.id === selectedId;
                return (
                    <li
                        key={friend.id}
                        onClick={() => handleSelect(friend)}
                        className="w-full flex flex-col items-center"
                    >
                        <div className="w-full flex flex-row items-center justify-center gap-x-3 px-[14px]">
                            {friend.profileImage ?
                                <img src={friend.profileImage} alt={friend.name} className="w-12 h-12 rounded-full" />
                                :
                                <div className="w-12 h-12 rounded-full bg-[#d9d9d9]" />
                            }
                            <span className="text-[15px] line-14 text-grey-15">{friend.name}</span>
                            <span className="text-[13px] line-14 text-grey-60">{friend.userTag}</span>
                            <div className="ml-auto">
                                {isSelected ? <SelectedIcon /> : <UnselectedIcon />}
                            </div>
                        </div>
                    </li>
                );
            })}
            <div className="fixed w-full px-6 bottom-4 left-0">
                <ActionButton
                    buttonText="완료"
                    onClick={() => {
                        navigate(navigateTo);
                    }}
                    disabled={selectedId === null}
                />
            </div>
        </ul>
    );
}

export default FriendSelectList;