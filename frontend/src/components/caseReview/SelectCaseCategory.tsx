import DisabledButtonImage from "../../assets/svgs/caseReview/DisabledButton.svg?react";
import EnabledButtonImage from "../../assets/svgs/caseReview/EnabledButton.svg?react";

import React, { useState } from 'react';

interface CaseCategoryItem {
    title: string;
    subtitle: string;
    SvgComponent: React.FC<React.SVGProps<SVGSVGElement>>;
}

interface SelectCaseCategoryProps {
    categories: CaseCategoryItem[];
    onCategorySelect?: (index: number | null) => void;
}

const SelectCaseCategory = ({ categories, onCategorySelect }: SelectCaseCategoryProps) => {
    const [selectedIndex, setSelectedIndex] = useState<number | null>(null);

    const handleCategoryClick = (index: number) => {
        const newSelectedIndex = selectedIndex === index ? null : index;
        setSelectedIndex(newSelectedIndex);
        onCategorySelect?.(newSelectedIndex);
    };

    return (
        <div className="grid grid-cols-2 gap-x-2 gap-y-4">
            {categories.map((category, index) => (
                <div
                    key={index}
                    onClick={() => handleCategoryClick(index)}
                    className={`relative flex flex-col items-center aspect-[4/5] bg-transparent justify-center px-4 pt-6 pb-4 gap-y-3 transition-colors duration-200 rounded-3xl shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)] cursor-pointer`}
                >
                    <DisabledButtonImage
                        preserveAspectRatio="none"
                        className={`absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200 ${selectedIndex !== index ? 'opacity-100' : 'opacity-0'}`}
                    />
                    <EnabledButtonImage
                        preserveAspectRatio="none"
                        className={`absolute top-0 left-0 w-full h-full z-[-1] transition-opacity duration-200 ${selectedIndex === index ? 'opacity-100' : 'opacity-0'}`}
                    />
                    <div className="w-full flex flex-col items-start gap-y-2">
                        <div className={`text-sm text-center duration-200
                            ${selectedIndex === index ? 'text-white' : 'text-grey-70'}`}>
                            {category.subtitle}
                        </div>
                        <div className={`text-lg font-semibold text-left leading-tight whitespace-pre-line duration-200
                            ${selectedIndex === index ? 'text-white' : 'text-grey-30'}`}>
                            {category.title}
                        </div>
                    </div>

                    <div className="mt-auto ml-auto">
                        <category.SvgComponent className="w-12 h-12" />
                    </div>
                </div>
            ))}
        </div>
    );
};

export default SelectCaseCategory;