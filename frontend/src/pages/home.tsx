import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import ProfileImage from '../components/ProfileImage';
import HomeCoinFind from '../components/home/HomeCoinFind';
import HomeCoinStatus from '../components/home/HomeCoinStatus';
import Carousel from '../components/home/Carousel';
import NavigationBar from '../components/NavigationBar';
import BottomSheet from '../components/BottomSheet';

import NotificationIcon from '../assets/svgs/notificationIcon.svg?react';

const Home = () => {
    useEffect(() => {
        // meta 태그 테마 색상 설정 (AOS 전용) 나중에 없앨듯?
        const themeColorMetaTag = document.querySelector('meta[name="theme-color"]');

        if (!themeColorMetaTag) {
            return;
        }

        document.body.style.background = "linear-gradient(to bottom, #55CFE5, #F7F5F4)";
        document.body.style.backgroundSize = 'cover';
        themeColorMetaTag.setAttribute('content', "#55cfe5");

        // 클리님 함수
        return () => {
            document.body.style.background = '#F7F7F8';
            document.body.style.backgroundSize = '';
            themeColorMetaTag.setAttribute('content', "#F7F7F8");
        };
    }, []);

    // BottomSheet 상태 관리
    const [isSheetOpen, setIsSheetOpen] = useState(false);

    const openSheet = () => setIsSheetOpen(true);
    const closeSheet = () => setIsSheetOpen(false);

    // 케러셀 슬라이드 요소
    const carouselSlide = [
        <HomeCoinFind onButtonClick={openSheet} />,
        <HomeCoinStatus
            title="가장 많이 받은 코인"
            titleValue="공감력"
            subtitle={["지금까지 ", "개를 찾아냈어요"]} // Numvalue를 중간에 넣기 위해 배열로 전달
            subtitleNumValue={7}
        />,
        <HomeCoinStatus
            title="최근 새로 찾은 코인"
            titleValue="리더십"
            subtitle={["새로운 모습을 발견했네요!"]}
        />,
        <HomeCoinStatus
            title="코인을 많이 나눈 친구"
            titleValue="김도청"
            subtitle={["코인을 ", "회 주고 받았어요"]}
            subtitleNumValue={5}
        />
    ]

    return (
        <div className="w-full h-full flex flex-col">
            {/* Header */}
            <div className="w-full h-12 px-4 py-2 pt-4 mb-2 flex flex-row items-center">
                <div className="h-full inline-flex flex-row items-center gap-2">
                    <ProfileImage />
                    {/* 사용자 이름. 기능 구현 예정 */}
                    <div className="min-w-fit">
                        <span className="text-base text-white font-semibold">사용자이름</span>
                        <span className="text-base text-white font-medium mx-0.5">님</span>
                    </div>
                </div>
                <div className='ml-auto h-full inline-flex'>
                    <Link to="/notification" className='inline-flex flex-row items-center'>
                        <NotificationIcon />
                    </Link>
                </div>
            </div>
            
            {/* Home Message */}
            <div className='px-6 py-2 mb-14'>
                <div className="display-n font-bold bg-gradient-to-b from-white to-white/40 bg-clip-text text-transparent">
                    메세지 내용이<br />들어가는 곳으로<br />최대 3줄이 출력됨
                </div>
            </div>

            {/* Home Coin */}
            <Carousel slides={carouselSlide} />

            {/* Navigation Bar */}
            <div className='w-full mt-auto px-16 flex flex-col items-center justify-center pb-4'>
                <NavigationBar />
            </div>

            {/* Bottom Sheet */}
            <BottomSheet title="코인 찾기" isOpen={isSheetOpen} onClose={closeSheet}>
                <div className="px-1 pt-4 pb-2">
                    <span className="title-n text-grey-25">나의 장점 찾기</span>
                </div>
                <div className="w-full grid grid-cols-2 gap-2">
                    <Link to="/todays-diary" className="pt-6 w-full bg-gradient-to-t from-white/0 to-gray-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
                        <div className="w-full flex flex-col items-center px-3 gap-y-1">
                            <div className="title-n font-semibold text-[#00BDDE]">오늘의 일기</div>
                            <div className="text-center button-n text-grey-60">오늘의 일상을 기록하면서<br />내 장점을 찾아봐요</div>
                        </div>
                        <div className="self-stretch h-28 py-4 inline-flex justify-center items-center">
                            <img className="w-28 h-28" src="https://placehold.co/110x110" />
                        </div>
                    </Link>
                    <div className="pt-6 w-full bg-gradient-to-t from-white/0 to-gray-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-col justify-start items-start">
                        <div className="w-full flex flex-col items-center px-3 gap-y-1">
                            <div className="title-n font-semibold text-[#00BDDE]">사례 돌아보기</div>
                            <div className="text-center button-n text-grey-60">이전 경험을 돌아보면서<br />내 장점을 찾아봐요</div>
                        </div>
                        <div className="self-stretch h-28 py-4 inline-flex justify-center items-center">
                            <img className="w-28 h-28" src="https://placehold.co/110x110" />
                        </div>
                    </div>
                </div>
                <div className="px-1 pt-4 pb-2">
                    <span className="title-n text-grey-25">친구의 장점 찾기</span>
                </div>
                    <div className="px-4 py-3 w-full bg-gradient-to-t from-white/0 to-gray-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] outline-2 outline-offset-[-2px] outline-white inline-flex flex-row items-center">
                        <div className="self-stretch h-28 py-4 inline-flex justify-center items-center mr-8">
                            <img className="w-28 h-28" src="https://placehold.co/110x110" />
                        </div>
                        <div className="flex flex-col gap-y-1">
                            <div className="title-n font-semibold text-[#00BDDE]">함께한 추억 떠올리기</div>
                            <div className="button-n text-grey-60">친구의 장점을 찾아줄 수 있어요</div>
                        </div>
                    </div>
            </BottomSheet>
        </div>
    )
}

export default Home;