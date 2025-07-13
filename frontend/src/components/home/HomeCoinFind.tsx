import React from 'react';

import HomeCoinImage from '../../assets/svgs/Home_Coin_Case.svg?url';
import HomeCoinAdd from '../../assets/svgs/Home_Coin_Add.svg?react';

interface HomeCoinFindProps {
    onButtonClick?: () => void
}

const HomeCoinFind: React.FC<HomeCoinFindProps> = ({ onButtonClick }) => {
    return (
        <div className="relative w-full aspect-square grid place-items-center">
            <div className="absolute w-full h-full -z-10 col-start-1 row-start-1">
                {/* IOS에서 깨지는 문제 때문에 img로 처리 */}
                <img src={HomeCoinImage} className='w-full aspect-square'/>
            </div>

            <div className="flex p-16 flex-col items-center justify-center gap-y-1 col-start-1 row-start-1">
                <HomeCoinAdd onClick={onButtonClick}/>
                <div className="title-n font-medium text-white">코인 찾기</div>
            </div>
        </div>
    )
}

export default HomeCoinFind;