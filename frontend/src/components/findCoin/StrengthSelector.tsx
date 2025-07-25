import { useState, useRef, useEffect, useCallback } from 'react';

import CategoryNav from './CategoryNav';
import AdvantageSection from './AdvantageSection';
import TopNavigation from '../common/TopNavigation';
import ActionButton from '../common/ActionButton';
import type { Category } from '../../constants/categories';
import { CATEGORY_COLOR_MAP } from '../../constants/categories';

interface KeywordData {
    name: string;
    description: string;
    coinName: string;
    fullInfo: string;
}

interface ApiResponse {
    success: boolean;
    message: string;
    data: {
        [categoryName: string]: KeywordData[];
    };
    errorCode: string | null;
}

interface StrengthSelectorProps {
    title: string;
    caption: string;
    onNext: (selectedItems: { category: string; item: string }[]) => void;
}

export default function StrengthSelector({ title, caption, onNext }: StrengthSelectorProps) {
    const [activeCategoryId, setActiveCategoryId] = useState<string>('');
    const [selectedItems, setSelectedItems] = useState<{ category: string; item: string }[]>([]);
    const [categories, setCategories] = useState<Category[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isScrolling, setIsScrolling] = useState(false); // 스크롤 상태 추가
    const sectionRefs = useRef<Map<string, HTMLElement>>(new Map());
    const mainScrollRef = useRef<HTMLElement>(null);
    const observer = useRef<IntersectionObserver | null>(null);
    const scrollTimeoutRef = useRef<number | null>(null);

    // 로딩 및 에러 상태 계산
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
                // 이미 선택된 아이템을 다시 클릭하면 선택 해제
                return prev.filter((_, index) => index !== existingIndex);
            } else {
                // 새로운 아이템 선택 시 기존 선택을 모두 제거하고 새 아이템만 선택
                // 카테고리 이름을 ID가 아닌 실제 title로 저장
                const categoryTitle = categories.find(cat => cat.id === category)?.title || category;
                return [{ category: categoryTitle, item }];
            }
        });
    };

    const handleCategoryClick = (id: string) => {
        const sectionElement = sectionRefs.current.get(id);
        const mainElement = mainScrollRef.current;

        if (sectionElement && mainElement) {
            // 스크롤 시작
            setIsScrolling(true);
            
            // 섹션의 실제 위치 계산
            const targetRect = sectionElement.getBoundingClientRect();
            const mainRect = mainElement.getBoundingClientRect();
            
            // 첫 번째 카테고리인지 확인 (category_0)
            const isFirstCategory = id === 'category_0';
            
            let targetScrollTop;
            if (isFirstCategory) {
                // 첫 번째 카테고리는 맨 위로 스크롤
                targetScrollTop = 0;
            } else {
                // IntersectionObserver의 rootMargin(-20% 0px -50% 0px)을 고려한 조정
                // 상단 20% 지점에 섹션이 오도록 조정
                const rootMarginOffset = mainElement.clientHeight * 0.2;
                targetScrollTop = mainElement.scrollTop + targetRect.top - mainRect.top - rootMarginOffset;
            }
            
            // 스크롤 후 activeCategoryId를 즉시 업데이트
            setActiveCategoryId(id);
            
            mainElement.scrollTo({
                top: Math.max(0, targetScrollTop), // 음수 방지
                behavior: 'smooth',
            });
            
            // 스크롤 완료 후 IntersectionObserver가 제대로 작동하도록 약간의 지연 후 재확인
            setTimeout(() => {
                setIsScrolling(false); // 스크롤 완료
                
                if (sectionElement && observer.current) {
                    // 첫 번째 카테고리는 항상 활성화
                    if (isFirstCategory) {
                        setActiveCategoryId(id);
                    } else {
                        // 스크롤이 완료된 후 해당 섹션이 제대로 인식되는지 확인
                        const rect = sectionElement.getBoundingClientRect();
                        const containerRect = mainElement.getBoundingClientRect();
                        const isVisible = rect.top < containerRect.bottom && rect.bottom > containerRect.top;
                        
                        if (isVisible) {
                            setActiveCategoryId(id);
                        }
                    }
                }
            }, 500); // 스크롤 애니메이션 완료 대기
        }
    };

    useEffect(() => {
        const mainElement = mainScrollRef.current;
        if (!mainElement) return;

        // 수동 스크롤 감지를 위한 이벤트 리스너
        const handleScroll = () => {
            // 이미 스크롤 중이면 (프로그래밍 방식) 무시
            if (isScrolling) return;
            
            // 수동 스크롤 시 잠시 IntersectionObserver 비활성화
            if (scrollTimeoutRef.current) {
                clearTimeout(scrollTimeoutRef.current);
            }
            
            // 스크롤이 멈춘 후 100ms 후에 IntersectionObserver 재활성화
            scrollTimeoutRef.current = window.setTimeout(() => {
                // 여기서 현재 가장 많이 보이는 섹션을 찾아서 업데이트
                if (!mainElement) return;
                
                // 최상단 근처에 있으면 첫 번째 카테고리 활성화
                const scrollTop = mainElement.scrollTop;
                if (scrollTop < 50) { // 50px 이내면 첫 번째 카테고리
                    setActiveCategoryId('category_0');
                    return;
                }
                
                let bestId = '';
                let maxVisibleRatio = 0;
                
                sectionRefs.current.forEach((element, id) => {
                    const rect = element.getBoundingClientRect();
                    const containerRect = mainElement.getBoundingClientRect();
                    
                    // 뷰포트 내에서 보이는 영역 계산
                    const visibleTop = Math.max(rect.top, containerRect.top);
                    const visibleBottom = Math.min(rect.bottom, containerRect.bottom);
                    const visibleHeight = Math.max(0, visibleBottom - visibleTop);
                    const elementHeight = rect.height;
                    const visibleRatio = elementHeight > 0 ? visibleHeight / elementHeight : 0;
                    
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
                // 스크롤 중이면 색상 변경을 무시
                if (isScrolling) return;
                
                // 현재 스크롤 위치 확인
                const scrollTop = mainElement.scrollTop;
                
                // 최상단 근처에 있으면 첫 번째 카테고리 활성화
                if (scrollTop < 50) { // 50px 이내면 첫 번째 카테고리
                    setActiveCategoryId('category_0');
                    return;
                }
                
                // 현재 보이는 섹션들 중에서 가장 많이 보이는 것 찾기
                let bestEntry: IntersectionObserverEntry | null = null;
                let maxVisibleRatio = 0;
                
                entries.forEach((entry) => {
                    if (entry.isIntersecting && entry.intersectionRatio > maxVisibleRatio) {
                        maxVisibleRatio = entry.intersectionRatio;
                        bestEntry = entry;
                    }
                });
                
                // 가장 많이 보이는 섹션이 있으면 업데이트
                if (bestEntry && maxVisibleRatio > 0.1) { // 최소 10% 이상 보일 때만
                    setActiveCategoryId((bestEntry as IntersectionObserverEntry).target.id);
                }
            },
            {
                root: mainElement,
                rootMargin: '-20% 0px -50% 0px', // 상단 20%, 하단 50% 마진으로 조정
                threshold: Array.from({ length: 21 }, (_, i) => i * 0.05) // 0부터 1까지 0.05 간격
            }
        );

        // 스크롤 이벤트 리스너 추가
        mainElement.addEventListener('scroll', handleScroll, { passive: true });
        
        // 이미 마운트된 자식 요소들을 다시 관찰 대상으로 추가
        sectionRefs.current.forEach(el => observer.current?.observe(el));

        return () => {
            observer.current?.disconnect();
            mainElement.removeEventListener('scroll', handleScroll);
            if (scrollTimeoutRef.current) {
                clearTimeout(scrollTimeoutRef.current);
            }
        };
    }, [isScrolling]);

    // 통합된 데이터 fetch 함수
    const fetchData = useCallback(async () => {
        try {
            setIsLoading(true);
            setError(null);
            
            const response = await fetch('/api/master/keywords');
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const apiResponse: ApiResponse = await response.json();
            
            if (!apiResponse.success) {
                throw new Error(apiResponse.message || '데이터를 불러오는 중 오류가 발생했습니다.');
            }
            
            // 백엔드 응답 구조에 맞게 카테고리 데이터 처리
            const processedCategories = Object.entries(apiResponse.data).map(([categoryName, keywords], index) => ({
                title: categoryName,
                id: `category_${index}`,
                color: CATEGORY_COLOR_MAP[categoryName] || 'bg-gray-100 text-gray-800',
                items: keywords.map(keyword => keyword.name) || []
            }));
            
            setCategories(processedCategories);
            
            if (processedCategories.length > 0) {
                // 첫 번째 카테고리를 기본 활성화
                setActiveCategoryId(processedCategories[0].id);
            }
            
        } catch (error) {
            setError(error instanceof Error ? error.message : '데이터를 불러오는 중 오류가 발생했습니다.');
        } finally {
            setIsLoading(false);
        }
    }, []);

    // 컴포넌트 마운트 시 데이터 로드
    useEffect(() => {
        fetchData();
    }, [fetchData]);



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
                            onClick={() => {
                                setError(null);
                                fetchData();
                            }}
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
                        buttonText="다음"
                        onClick={() => onNext(selectedItems)}
                        disabled={selectedItems.length === 0 || !isReady || isFetchFailed}
                    />
                </div>
            </div>
        </div>
    );
}
