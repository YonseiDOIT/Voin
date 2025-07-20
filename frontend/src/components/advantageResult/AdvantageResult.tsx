import type { AdvantageResultTheme } from './advantageResultTypes';

type AdvantageResultColorPalette = {
    titleStyle: string;
    categoryStyle: string;
    backgroundClass: string;
}

const colorPalettes: Record<AdvantageResultTheme, AdvantageResultColorPalette> = {
    'GROWTH': {
        titleStyle: "text-blue-800",
        categoryStyle: "bg-blue-100 text-blue-800",
        backgroundClass: "bg-gradient-to-b from-blue-50 to-white",
    },
    'EMOTION': {
        titleStyle: "text-orange-700",
        categoryStyle: "bg-orange-300 text-orange-700",
        backgroundClass: "bg-gradient-to-b from-orange-50 to-white",
    },
    'CREATIVITY': {
        titleStyle: "text-purple-700",
        categoryStyle: "bg-violet-200 text-purple-700",
        backgroundClass: "bg-gradient-to-b from-purple-50 to-white",
    },
    'PROBLEM_SOLVING': {
        titleStyle: "text-green-700",
        categoryStyle: "bg-green-200 text-green-700",
        backgroundClass: "bg-gradient-to-b from-green-50 to-white",
    },
    'RELATIONSHIP': {
        titleStyle: "text-yellow-700",
        categoryStyle: "bg-yellow-200 text-yellow-700",
        backgroundClass: "bg-gradient-to-b from-yellow-50 to-white",
    },
    'BELIEFS': {
        titleStyle: "text-rose-700",
        categoryStyle: "bg-red-200 text-rose-700",
        backgroundClass: "bg-gradient-to-b from-red-50 to-white",
    },
};

export interface AdvantageResultProps {
    theme: AdvantageResultTheme;
    category: string;
    title: string;
    titleDescription: string;
    description: string;
}

const AdvantageResult = (props: AdvantageResultProps) => {
    const palette = colorPalettes[props.theme];
    return (
        <div className={`
            w-full px-6 py-8
            ${palette.backgroundClass}
            rounded-[32px]
            shadow-[0px_5px_15px_-5px_rgba(35,48,59,0.10)]
            outline-2 outline-offset-[-2px] outline-white
            inline-flex flex-col justify-start items-center gap-8
            overflow-hidden
        `}>
            <div className={`
                self-stretch
                flex
                flex-col
                justify-start
                items-start
                gap-4
            `}>
                <div data-active="True" data-color="Blue" data-count="False" data-type="Category" className={`p-3 ${palette.categoryStyle} rounded-3xl inline-flex justify-center items-center gap-2`}>
                    <span className={`${palette.titleStyle} body-n font-semibold`}>
                        {props.category}
                    </span>
                </div>
                <div className={`
                    self-stretch
                    px-1
                    flex
                    flex-col
                    justify-start
                    items-start
                    gap-1
                `}>
                    <span className={`${palette.titleStyle} text-3xl font-bold leading-10`}>
                        {props.title}
                    </span>
                    <span className={`
                        self-stretch
                        text-grey-50
                        text-lg
                        font-medium
                        leading-relaxed
                    `}>{props.titleDescription}</span>
                </div>
            </div>
            <div className={`
                inline-flex
                justify-center
                items-end
                gap-2
            `}>
                <img className={`
                    w-60
                    h-60
                `} src="https://placehold.co/240x240" />
            </div>
            <div className={`
                inline-flex
                justify-start
                items-center
                gap-2
            `}>
                <span className={`
                    flex-1
                    w-full
                    h-16
                    justify-center
                    text-grey-40
                    body-n
                    font-medium
                `}>{props.description}</span>
            </div>
        </div>
    )
}

export default AdvantageResult;
