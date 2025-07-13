import { useEffect, useRef } from 'react';

import DisabledButtonImage from "../../assets/svgs/TodaysDiary/DisabledButton.svg?react";
import EnabledButtonImage from "../../assets/svgs/TodaysDiary/EnabledButton.svg?react";

interface Props {
    id: string;
    items: string[];
    onMount: (id: string, element: HTMLElement) => void;
    onUnmount: (id: string) => void;
    onSelectItem: (category: string, item: string) => void;
    selectedItems: string[];
}

function AdvantageSection({ id, items, onMount, onUnmount, onSelectItem, selectedItems }: Props) {
    const sectionRef = useRef<HTMLElement>(null);

    useEffect(() => {
        if (sectionRef.current) {
            onMount(id, sectionRef.current);
        }
        // 컴포넌트가 사라질 때 unmount 함수 호출
        return () => {
            onUnmount(id);
        };
    }, [id, onMount, onUnmount]);

    return (
        <section ref={sectionRef} id={id}>
            <div className="grid grid-cols-3 gap-2">
                {items.map((item) => (
                    <div
                        key={item}
                        onClick={() => onSelectItem(id, item)}
                        className={`relative flex flex-col items-center aspect-[1/1.15] justify-center px-6 pt-5 pb-4 gap-y-3 text-gray-700 cursor-pointer transition-colors duration-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]
                            `}
                    >
                        <DisabledButtonImage
                            preserveAspectRatio="none"
                            className={`absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200 ${!selectedItems.includes(item) ? 'opacity-100' : 'opacity-0'}`}
                        />
                        <EnabledButtonImage
                            preserveAspectRatio="none"
                            className={`absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200 ${selectedItems.includes(item) ? 'opacity-100' : 'opacity-0'}`}
                        />
                        <div className="w-12 h-12 rounded-full border-2 border-[#D9D9D9]"></div>
                        <div className={`body-n font-semibold whitespace-nowrap duration-200
                            ${selectedItems.includes(item) ? 'text-white' : 'text-grey-50'}`}>
                            {item}
                        </div>
                    </div>
                ))}
            </div>
        </section>
    );
}

export default AdvantageSection;