import { useState, useRef, useEffect, useCallback } from 'react';

import CategoryNav from './CategoryNav';
import AdvantageSection from './AdvantageSection';
import TopNavigation from '../common/TopNavigation';
import ActionButton from '../common/ActionButton';

const CATEGORIES = [
    { id: 'growth', title: '관리와 성장', color: 'bg-blue-100 text-blue-800' },
    { id: 'emotion', title: '감정과 태도', color: 'bg-orange-300 text-orange-700' },
    { id: 'creativity', title: '창의와 몰입', color: 'bg-violet-200 text-purple-700' },
    { id: 'problemSolving', title: '사고와 해결', color: 'bg-green-200 text-green-700' },
    { id: 'relationship', title: '관계와 공감', color: 'bg-yellow-200 text-yellow-700' },
    { id: 'Beliefs', title: '신념과 실행', color: 'bg-red-200 text-rose-700' },
];

const DUMMY_ITEMS = {
    growth: ["끈기", "인내심", "성실함", "절제력", "침착함", "학습력", "성찰력", "적응력", "수용력"],
    emotion: ["유머 감각", "감수성", "표현력", "밝은 에너지", "긍정성", "열정"],
    creativity: ["호기심", "탐구력", "창의력", "집중력", "몰입력", "기획력"],
    problemSolving: ["판단력", "논리력", "분석력", "통찰력", "신중성", "문제해결력", "융통성"],
    relationship: ["공감력", "배려심", "포용력", "경청 태도", "친화력", "지지력", "온화함", "중재력", "조율력", "겸손함", "예의 바름"],
    Beliefs: ["신념", "주체성", "정직함", "정의감", "도덕심", "용기", "결단력", "주도성", "실행력", "리더십", "공정성", "책임감", "계획성", "도전력"],
};

interface StrengthSelectorProps {
    title: string;
    caption: string;
    onNext: (selectedItems: { category: string; item: string }[]) => void;
}

export default function StrengthSelector({ title, caption, onNext }: StrengthSelectorProps) {
    const [activeCategoryId, setActiveCategoryId] = useState(CATEGORIES[0].id);
    const [selectedItems, setSelectedItems] = useState<{ category: string; item: string }[]>([]);
    const sectionRefs = useRef<Map<string, HTMLElement>>(new Map());
    const mainScrollRef = useRef<HTMLElement>(null);
    const observer = useRef<IntersectionObserver | null>(null);

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
                return [{ category, item }];
            }
        });
    };

    const handleCategoryClick = (id: string) => {
        const sectionElement = sectionRefs.current.get(id);
        const mainElement = mainScrollRef.current;

        if (sectionElement && mainElement) {
            // hr 태그가 있는 경우를 고려하여 실제 콘텐츠 영역으로 스크롤
            const contentElement = sectionElement.querySelector('div.grid') as HTMLElement;
            const targetElement = contentElement || sectionElement;
            
            const targetRect = targetElement.getBoundingClientRect();
            const mainRect = mainElement.getBoundingClientRect();
            
            // 현재 스크롤 위치 + 타겟의 상대 위치 계산
            const targetScrollTop = mainElement.scrollTop + targetRect.top - mainRect.top;
            
            mainElement.scrollTo({
                top: targetScrollTop,
                behavior: 'smooth',
            });
        }
    };

    useEffect(() => {
        if (!mainScrollRef.current) return;

        observer.current = new IntersectionObserver(
            (entries) => {
                // 가장 많이 보이는 섹션을 찾기
                let bestEntry: IntersectionObserverEntry | null = null;
                let maxVisibleArea = 0;
                
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        const visibleArea = entry.intersectionRatio * entry.boundingClientRect.height;
                        if (visibleArea > maxVisibleArea) {
                            maxVisibleArea = visibleArea;
                            bestEntry = entry;
                        }
                    }
                });
                
                if (bestEntry) {
                    setActiveCategoryId((bestEntry as IntersectionObserverEntry).target.id);
                }
            },
            {
                root: mainScrollRef.current,
                rootMargin: '-20% 0px -50% 0px', // 상단 20%, 하단 50% 마진으로 조정
                threshold: [0, 0.1, 0.25, 0.5, 0.75, 1.0] // 여러 threshold 설정
            }
        );

        // 이미 마운트된 자식 요소들을 다시 관찰 대상으로 추가
        sectionRefs.current.forEach(el => observer.current?.observe(el));

        return () => observer.current?.disconnect();
    }, []);

    return (
        <div className="w-full max-w-md mx-auto flex flex-col h-full">
            <header className="pt-4">
                <TopNavigation title={title} caption={caption} />
                <div className="pt-1 pb-6 px-6">
                    <CategoryNav
                        categories={CATEGORIES}
                        activeCategoryId={activeCategoryId}
                        onCategoryClick={handleCategoryClick}
                    />
                </div>
            </header>

            <main ref={mainScrollRef} className="relative flex-grow overflow-y-auto px-6 pb-32">
                {CATEGORIES.map((category, index) => (
                    <div key={category.id}>
                        <AdvantageSection
                            id={category.id}
                            items={DUMMY_ITEMS[category.id as keyof typeof DUMMY_ITEMS]}
                            onMount={handleMount}
                            onUnmount={handleUnmount}
                            onSelectItem={handleSelectItem}
                            selectedItems={selectedItems.filter((si) => si.category === category.id).map((si) => si.item)}
                            isFirst={index === 0}
                        />
                    </div>
                ))}
            </main>

            <div className='fixed bottom-0 left-0 right-0 flex flex-col w-full max-w-md mx-auto'>
                <div className="h-8 w-full bg-gradient-to-b from-[#F7F7F8]/0 to-grey-99"></div>
                <div className='relative bg-grey-99 px-6' style={{ paddingBottom: 'calc(env(safe-area-inset-bottom) + 1rem)' }}>
                    <ActionButton
                        buttonText="다음"
                        onClick={() => onNext(selectedItems)}
                        disabled={selectedItems.length === 0}
                    />
                </div>
            </div>
        </div>
    );
}
