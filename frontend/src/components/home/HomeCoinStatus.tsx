import React from 'react';

import HomeCoinImage from '../../assets/svgs/Home_Coin_Case.svg?url';
import EmptyCoinImage from '../../assets/svgs/emptyCoin.svg?react';

interface HomeCoinStatusProps {
    title: string;
    titleValue: string;
    subtitle: string[];
    subtitleNumValue?: number;
}

const HomeCoinStatus: React.FC<HomeCoinStatusProps> = ({
    title,
    titleValue,
    subtitle,
    subtitleNumValue = null,
}) => {

    return (
        <div className="relative w-full aspect-square grid place-items-center">
            <div className="absolute w-full h-full -z-10 col-start-1 row-start-1">
                {/* IOS에서 깨지는 문제 때문에 img로 처리 */}
                <img src={HomeCoinImage} className='w-full aspect-square' />
            </div>

            <div className="flex p-16 flex-col items-center justify-center gap-y-1 col-start-1 row-start-1">
                <div className="text-grey-97 text-[16px] line-14 font-semibold mb-4">{title}</div>
                <div className='mb-4'>
                    <EmptyCoinImage />
                </div>
                <div className="text-white line-14 text-[20px] font-semibold">{titleValue}</div>
                {subtitleNumValue ?
                    <div className="text-grey-99 line-15 font-medium text-[13px]">{subtitle[0]}{subtitleNumValue}{subtitle[1]}</div>
                    :
                    <div className="text-grey-99 line-15 font-medium text-[13px]">{subtitle[0]}</div>
                }
            </div>
        </div>
    )
}

export default HomeCoinStatus;