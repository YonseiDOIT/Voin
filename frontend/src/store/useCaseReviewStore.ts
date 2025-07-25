import { create } from 'zustand';

export interface CaseReviewAdvantageData {

    categoryName: string;
    caseName: string;
    writtenCase1: string;
    writtenCase2: string;
    strengthName: string;
    strengthDescription: string;
    coinColor: string;
    fullDescription: string;
    coinName: string;
    uploadedImage?: string;
    comment?: string;
    classify?: string;
}

interface CaseReviewStore {
    data: Partial<CaseReviewAdvantageData>;
    setData: (data: Partial<CaseReviewAdvantageData>) => void;
    reset: () => void;
}

export const useCaseReviewStore = create<CaseReviewStore>((set) => ({
    data: {},
    setData: (newData) => set((state) => ({ data: { ...state.data, ...newData } })),
    reset: () => set({ data: {} }),
}));
