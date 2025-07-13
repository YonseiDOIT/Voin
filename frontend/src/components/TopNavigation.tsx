import { useNavigate } from 'react-router-dom';

interface TopNavigationProps {
    title: string;
    caption?: string;
    onBackClick?: () => void;
    TitleEnlarge?: boolean;
}

export default function TopNavigation({ title="타이틀 텍스트", caption, onBackClick, TitleEnlarge = false }: TopNavigationProps) {
    const navigate = useNavigate();

    const handleBackClick = () => {
        if (onBackClick) {
            onBackClick();
        } else {
            navigate(-1);
        }
    };

    return (
        <header
            className="flex flex-col items-center w-full px-4 bg-transparent">
            <div className="w-full mb-4">
                <button
                    onClick={handleBackClick}
                    className="w-10 h-10 p-2 bg-gradient-to-b from-white/0 to-white rounded-3xl shadow-[0px_5px_20px_-5px_rgba(35,48,59,0.25)] outline-1 outline-offset-[-1px] outline-white/70 backdrop-blur-[50px] inline-flex justify-center items-center overflow-hidden transition-all duration-150 active:bg-black/20">
                    <div className="h-6 inline-flex flex-col justify-center items-center">
                        <div className="w-6 h-6 relative overflow-hidden">
                            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
                            </svg>
                        </div>
                    </div>
                </button>
            </div>

            <div className="w-full pl-2 pr-4 mb-6">
                <div className={`mb-2 font-semibold leading-loose text-grey-15 ${TitleEnlarge ? 'text-3xl' : 'text-2xl'}`}>{title}</div>
                {caption && <div className="body-n text-grey-70">{caption}</div>}
            </div>
        </header>
    );
} 