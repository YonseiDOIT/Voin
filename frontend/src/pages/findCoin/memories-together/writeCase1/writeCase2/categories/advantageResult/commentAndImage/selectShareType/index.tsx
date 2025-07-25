import EnabledButton from '@/assets/svgs/selectShare/EnabledButton.svg?react';
import DisabledButton from '@/assets/svgs/selectShare/DisabledButton.svg?react';
import AppShareIcon from '@/assets/svgs/selectShare/AppShareIcon.svg?react';
import KakaoShareIcon from '@/assets/svgs/selectShare/KakaoShareIcon.svg?react';

import TopNavigation from '@/components/common/TopNavigation';
import BottomSheet from '@/components/common/BottomSheet';
import ActionButton from '@/components/common/ActionButton';
import FriendSelectList from '@/components/advantageResult/FriendSelectList';

import { useState } from 'react';

const shareTypes = [
    {
        title: '앱 친구에게 전송',
        subtitle: 'Voin 앱에서 친구를 맺은 분에게 바로 전달할 수 있어요.',
        value: 'app',
        SvgComponent: AppShareIcon
    },
    {
        title: '카카오톡 링크로 공유',
        subtitle: '아직 앱에 가입하지 않은 분에게 링크로 전달할 수 있어요',
        value: 'kakao',
        SvgComponent: KakaoShareIcon
    },
];

const friends = [
    {
        id: '1',
        name: '홍길동',
        profileImage: 'https://randomuser.me/api/portraits/men/1.jpg',
        userTag: '#F45DE2',
    },
    {
        id: '2',
        name: '김철수',
        profileImage: 'https://randomuser.me/api/portraits/men/2.jpg',
        userTag: '#123ABC',
    },
    {
        id: '3',
        name: '이영희',
        profileImage: '',
        userTag: '#A23VB5',
    },
    {
        id: '4',
        name: '박지민',
        profileImage: 'https://randomuser.me/api/portraits/men/4.jpg',
        userTag: '#B45DE2',
    },
    {
        id: '5',
        name: '최지우',
        profileImage: 'https://randomuser.me/api/portraits/men/5.jpg',
        userTag: '#C45DE2',
    },
    {
        id: '6',
        name: '정우성',
        profileImage: 'https://randomuser.me/api/portraits/men/6.jpg',
        userTag: '#D45DE2',
    },
    {
        id: '7',
        name: '한지민',
        profileImage: 'https://randomuser.me/api/portraits/men/7.jpg',
        userTag: '#E45DE2',
    },
];

const SelectShareType = () => {
    const [selectedIndex, setSelectedIndex] = useState<number | null>(null);
    const [isSheetOpen, setIsSheetOpen] = useState(false);

    // const openSheet = () => setIsSheetOpen(true);
    const closeSheet = () => setIsSheetOpen(false);

    const handleClick = (index: number) => {
        console.log(`Selected type: ${index}`);
        setSelectedIndex(selectedIndex === index ? null : index);
    };

    const handleButtonClick = () => {
        if (selectedIndex !== null) {
            setIsSheetOpen(true);
        }
    };

    return (
        <div className="w-full h-full flex flex-col">
            <TopNavigation
                title="이 코인을 어떻게 전해볼까요?"
                caption='서로의 장점을 나누면, 이 코인을 볼 수 있어요.'
            />

            <div className="flex flex-col gap-y-4 px-4 mt-6 w-full h-full">
                {shareTypes.map((type, index) => {
                    const isSelected = selectedIndex === index;
                    return (
                        <div
                            key={type.value}
                            onClick={() => handleClick(index)
                            }
                            className={`relative flex flex-row items-center aspect-[2.3/1] px-6 py-8 rounded-[24px] cursor-pointer shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] transition-all duration-200 bg-transparent`}
                            style={{ minHeight: 140 }}
                        >
                            {/* 배경 SVG */}
                            <DisabledButton
                                preserveAspectRatio="none"
                                className={`absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200 ${!isSelected ? 'opacity-100' : 'opacity-0'}`}
                            />
                            <EnabledButton
                                preserveAspectRatio="none"
                                className={`absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200 ${isSelected ? 'opacity-100' : 'opacity-0'}`}
                            />
                            {/* 아이콘 */}
                            <div className="flex-shrink-0 mr-6">
                                <type.SvgComponent />
                            </div>
                            {/* 텍스트 */}
                            <div className="flex flex-col">
                                <div className={`text-[18px] line-14 font-semibold mb-1 duration-200 ${isSelected ? 'text-white' : 'text-grey-30'}`}>{type.title}</div>
                                <div className={`text-[13px] line-14 duration-200 ${isSelected ? 'text-white' : 'text-grey-60'}`}>{type.subtitle}</div>
                            </div>
                        </div>
                    );
                })}
            </div>
            <div className='w-full mt-auto px-4 mb-4'>
                <ActionButton
                    buttonText="다음"
                    onClick={handleButtonClick}
                    disabled={selectedIndex === null}
                />
            </div>

            <BottomSheet
                title="친구를 선택해주세요"
                isOpen={isSheetOpen}
                onClose={closeSheet}
            >
                <div>
                    <FriendSelectList
                        friends={friends}
                        selectedIndex={selectedIndex}
                        onSelectFriend={(index) => setSelectedIndex(index)}
                        navigateTo='/memories-together/final-result'
                    />
                </div>
            </BottomSheet>
        </div>
    );
};

export default SelectShareType;
