import React from 'react';

interface BottomSheetProps {
    title?: string;
    isOpen: boolean;
    onClose: () => void;
    children: React.ReactNode;
}

const BottomSheet: React.FC<BottomSheetProps> = ({ title, isOpen, onClose, children }) => {
    return (

        <div className={`fixed inset-0 z-50 transition-all duration-300 ${isOpen ? 'visible' : 'invisible'} `}>
            <div onClick={onClose} className={`absolute inset-0 bg-black/50 transition-opacity duration-300 ${isOpen ? 'opacity-100' : 'opacity-0'}`}></div>

            <div className={`absolute bottom-0 left-0 right-0 px-4 pb-16 transform bg-white rounded-t-[40px] shadow-[-4px_0_16px_rgba(0,0,0,0.1)] transition-transform duration-300 ease-in-out ${isOpen ? 'translate-y-0' : 'translate-y-full'}`}>
                {title &&
                    <div className="relative w-full px-1 py-7 flex flex-row items-center justify-center">
                        <span className="line-14 text-[18px] font-semibold text-grey-15">{title}</span>
                        <button
                            onClick={onClose}
                            className="absolute right-1 w-10 h-10 p-2 bg-gradient-to-b from-white/0 to-white rounded-3xl shadow-[0px_5px_20px_-5px_rgba(35,48,59,0.25)] outline-1 outline-offset-[-1px] outline-white/70 backdrop-blur-[50px] inline-flex justify-center items-center overflow-hidden transition-all duration-150 active:bg-black/20">
                            <div className="h-6 inline-flex flex-col justify-center items-center">
                                <div className="w-6 h-6 relative overflow-hidden">
                                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18 18 6M6 6l12 12" />
                                    </svg>
                                </div>
                            </div>
                        </button>
                    </div>
                }
                {children}
            </div>
        </div>
    );
};

export default BottomSheet;