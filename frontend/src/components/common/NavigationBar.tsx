import CopyIcon from '../../assets/svgs/navigationBar/copy.svg?react';
import HomeIcon from '../../assets/svgs/navigationBar/home.svg?react';
import MoneyBagIcon from '../../assets/svgs/navigationBar/money-bag.svg?react';

const NavigationBar = () => {
    return (
        <div className="px-8 py-4 bg-gradient-to-b from-white/0 to-white rounded-full shadow-[0px_10px_20px_-5px_rgba(35,48,59,0.05)] outline-1 outline-offset-[-1px] outline-white/70 backdrop-blur-[50px] inline-flex justify-start items-center gap-6 overflow-hidden">
            <div className="w-11 aspect-square">
                <div className="flex justify-center mb-auto">
                    <CopyIcon fill="#DBDCDF" />
                </div>
                <div className='w-full text-center mt-auto'>
                    <span className="text-grey-95 line-15 font-semibold text-[13px]">피드</span>
                </div>
            </div>
            <div className="w-11 aspect-square">
                <div className="flex justify-center">
                    <HomeIcon fill="#00BEDB" />
                </div>
                <div className='w-full text-center'>
                    <span className="text-VB-50 line-15 font-semibold text-[13px]">홈</span>
                </div>
            </div>
            <div className="w-11 aspect-square">
                <div className="flex justify-center">
                    <MoneyBagIcon fill="#DBDCDF" />
                </div>
                <div className='w-full text-center'>
                    <span className="text-grey-95 line-15 font-semibold whitespace-nowrap text-[13px]">아카이브</span>
                </div>
            </div>
        </div>
    );
}

export default NavigationBar;