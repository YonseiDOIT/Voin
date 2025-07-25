import { create } from 'zustand';

// CaseReview, TodaysDiary 등 다양한 활동에서 사용할 데이터 타입
export interface ActivityData {
    categoryName?: string;
    caseName?: string;
    writtenCase1?: string;
    writtenCase2?: string;
    strengthName?: string;
    strengthDescription?: string;
    coinColor?: string;
    fullDescription?: string;
    coinName?: string;
    uploadedImage?: string;
    comment?: string;
    classify?: string;
}

interface ActivityStore {
    data: ActivityData;
    setData: (data: Partial<ActivityData>) => void;
    reset: () => void;
}

export const useActivityStore = create<ActivityStore>((set) => ({
    data: {},
    setData: (newData) => set((state) => ({ data: { ...state.data, ...newData } })),
    reset: () => set({ data: {} }),
}));