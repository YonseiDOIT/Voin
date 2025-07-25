import { useState, useRef, useEffect, useCallback } from 'react';
import { useActivityStore } from '@/store/useActivityStore';
import { classifyText } from '@/services/gptService';
// import { fetchAllStrengths } from '@/services/strengthService';
import { useStrengthStore } from '@/store/useStrengthStore';

import CategoryNav from './CategoryNav';
import AdvantageSection from './AdvantageSection';
import TopNavigation from '@/components/common/TopNavigation';
import ActionButton from '@/components/common/ActionButton';
import type { Category } from '@/constants/categories';
import { CATEGORY_COLOR_MAP } from '@/constants/categories';

interface StrengthSelectorProps {
    title: string;
    caption: string;
    onNext: (selectedItems: { category: string; item: string }[]) => void;
}

export default function StrengthSelector({ title, caption, onNext }: StrengthSelectorProps) {
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [activeCategoryId, setActiveCategoryId] = useState<string>('');
    const [selectedItems, setSelectedItems] = useState<{ category: string; item: string }[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const { isLoading, error, fetchStrengths, categories: strengthData } = useStrengthStore();
    
    const [isScrolling, setIsScrolling] = useState(false);
    const sectionRefs = useRef<Map<string, HTMLElement>>(new Map());
    const mainScrollRef = useRef<HTMLElement>(null);
    const observer = useRef<IntersectionObserver | null>(null);
    const scrollTimeoutRef = useRef<number | null>(null);
    const writtenCase1 = useActivityStore(state => state.data.writtenCase1 || '');
const setData = useActivityStore(state => state.setData);

    const handleNext = useCallback(async () => {
        setIsSubmitting(true);
        try {
            if (writtenCase1) {
                const result = await classifyText(writtenCase1);
                setData({ classify: result.classify });
            }
            onNext(selectedItems);
        } catch (e) {
            console.error('API 요청 실패:', e);
            onNext(selectedItems); // 실패하더라도 다음 단계로 진행
        } finally {
            setIsSubmitting(false);
        }
    }, [writtenCase1, selectedItems, onNext, setData]);

    useEffect(() => {
        if (strengthData.size > 0) {
            const processedCategories = Array.from(strengthData.entries()).map(([categoryName, data], index) => ({
                title: categoryName,
                id: `category_${index}`,
                color: CATEGORY_COLOR_MAP[categoryName] || 'bg-gray-100 text-gray-800',
                items: data.keywords.map(keyword => keyword.name) || []
            }));
            setCategories(processedCategories);
            if (processedCategories.length > 0) {
                setActiveCategoryId(processedCategories[0].id);
            }
        }
    }, [strengthData]);

    const isReady = !isLoading && !error && categories.length > 0;
    const isFetchFailed = !isLoading && error !== null;

    const handleMount = useCallback((id: string, element: HTMLElement) => {
        sectionRefs.current.set(id, element);
        if (observer.current) {
            observer.current.observe(element);
        }
    }, []);

    const handleUnmount = useCallback((id: string) => {
        const element = sectionRefs.current.get(id);
        if (element && observer.current) {
            observer.current.unobserve(element);
        }
        sectionRefs.current.delete(id);
    }, []);

    const handleSelectItem = (category: string, item: string) => {
        setSelectedItems((prev) => {
            const existingIndex = prev.findIndex((selected) => selected.category === category && selected.item === item);
            if (existingIndex > -1) {
                return prev.filter((_, index) => index !== existingIndex);
            } else {
                const categoryTitle = categories.find(cat => cat.id === category)?.title || category;
                return [{ category: categoryTitle, item }];
            }
        });
    };

    const handleCategoryClick = (id: string) => {
        const sectionElement = sectionRefs.current.get(id);
        const mainElement = mainScrollRef.current;

        if (sectionElement && mainElement) {
            setIsScrolling(true);
            const targetRect = sectionElement.getBoundingClientRect();
            const mainRect = mainElement.getBoundingClientRect();
            const isFirstCategory = id === 'category_0';
            
            let targetScrollTop = isFirstCategory ? 0 : mainElement.scrollTop + targetRect.top - mainRect.top - (mainElement.clientHeight * 0.2);
            
            setActiveCategoryId(id);
            mainElement.scrollTo({
                top: Math.max(0, targetScrollTop),
                behavior: 'smooth',
            });
            
            setTimeout(() => {
                setIsScrolling(false);
            }, 500);
        }
    };

    useEffect(() => {
        const mainElement = mainScrollRef.current;
        if (!mainElement) return;

        const handleScroll = () => {
            if (isScrolling) return;
            if (scrollTimeoutRef.current) clearTimeout(scrollTimeoutRef.current);
            
            scrollTimeoutRef.current = window.setTimeout(() => {
                if (!mainElement) return;
                if (mainElement.scrollTop < 50) {
                    setActiveCategoryId('category_0');
                    return;
                }
                
                let bestId = '';
                let maxVisibleRatio = 0;
                
                sectionRefs.current.forEach((element, id) => {
                    const rect = element.getBoundingClientRect();
                    const containerRect = mainElement.getBoundingClientRect();
                    const visibleTop = Math.max(rect.top, containerRect.top);
                    const visibleBottom = Math.min(rect.bottom, containerRect.bottom);
                    const visibleHeight = Math.max(0, visibleBottom - visibleTop);
                    const visibleRatio = rect.height > 0 ? visibleHeight / rect.height : 0;
                    
                    if (visibleRatio > maxVisibleRatio) {
                        maxVisibleRatio = visibleRatio;
                        bestId = id;
                    }
                });
                
                if (bestId && maxVisibleRatio > 0.1) {
                    setActiveCategoryId(bestId);
                }
            }, 100);
        };

        observer.current = new IntersectionObserver(
            (entries) => {
                if (isScrolling) return;
                if (mainElement.scrollTop < 50) {
                    setActiveCategoryId('category_0');
                    return;
                }
                
                let bestEntry: IntersectionObserverEntry | null = null;
                let maxVisibleRatio = 0;
                
                entries.forEach((entry) => {
                    if (entry.isIntersecting && entry.intersectionRatio > maxVisibleRatio) {
                        maxVisibleRatio = entry.intersectionRatio;
                        bestEntry = entry;
                    }
                });
                
                if (bestEntry && maxVisibleRatio > 0.1) {
                    setActiveCategoryId((bestEntry as IntersectionObserverEntry).target.id);
                }
            },
            {
                root: mainElement,
                rootMargin: '-20% 0px -50% 0px',
                threshold: Array.from({ length: 21 }, (_, i) => i * 0.05)
            }
        );

        mainElement.addEventListener('scroll', handleScroll, { passive: true });
        sectionRefs.current.forEach(el => observer.current?.observe(el));

        return () => {
            observer.current?.disconnect();
            mainElement.removeEventListener('scroll', handleScroll);
            if (scrollTimeoutRef.current) clearTimeout(scrollTimeoutRef.current);
        };
    }, [isScrolling]);

    return (
        <div className="w-full max-w-md mx-auto flex flex-col h-full">
            <header>
                <TopNavigation title={title} caption={caption} />
                <div className="pt-1 pb-6">
                    <CategoryNav
                        categories={categories}
                        activeCategoryId={activeCategoryId}
                        onCategoryClick={handleCategoryClick}
                    />
                </div>
            </header>

            <main ref={mainScrollRef} className="relative flex-grow overflow-y-auto px-6 pb-32">
                {isFetchFailed ? (
                    <div className="flex flex-col justify-center items-center h-64 text-center">
                        <div className="mb-4 text-6xl">😵</div>
                        <div className="text-xl font-bold text-gray-800 mb-2">
                            데이터를 불러올 수 없습니다
                        </div>
                        <div className="text-gray-600 mb-6">
                            네트워크 연결을 확인하고 다시 시도해주세요
                        </div>
                        <button
                            onClick={fetchStrengths}
                            className="px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
                        >
                            다시 시도
                        </button>
                    </div>
                ) : isLoading ? (
                    <div className="flex flex-col justify-center items-center h-64 text-center">
                        <div className="mb-6">
                            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
                        </div>
                        <div className="text-lg font-medium text-gray-700 mb-2">
                            데이터를 불러오는 중...
                        </div>
                        <div className="text-gray-500">
                            잠시만 기다려주세요
                        </div>
                    </div>
                ) : (
                    categories.map((category: Category, index: number) => (
                        <div key={category.id} data-category={category.title} data-index={index}>
                            <AdvantageSection
                                id={category.id}
                                categoryTitle={category.title}
                                items={category.items || []}
                                onMount={handleMount}
                                onUnmount={handleUnmount}
                                onSelectItem={handleSelectItem}
                                selectedItems={selectedItems.filter((si) => si.category === category.title).map((si) => si.item)}
                                isFirst={index === 0}
                            />
                        </div>
                    ))
                )}
            </main>

            <div className='fixed bottom-0 left-0 right-0 flex flex-col w-full max-w-md mx-auto'>
                <div className="h-8 w-full bg-gradient-to-b from-[#F7F7F8]/0 to-grey-99"></div>
                <div className='relative bg-grey-99 px-6' style={{ paddingBottom: 'calc(env(safe-area-inset-bottom) + 1rem)' }}>
                    <ActionButton
                        buttonText={isSubmitting ? "잠시만 기다려주세요" : "다음"}
                        onClick={handleNext}
                        disabled={selectedItems.length === 0 || !isReady || isFetchFailed || isSubmitting}
                    />
                </div>
            </div>
        </div>
    );
}
