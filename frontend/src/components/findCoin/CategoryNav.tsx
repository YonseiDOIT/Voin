import type { Category } from '@/constants/categories';

interface CategoryNavProps {
    categories: Category[];
    activeCategoryId: string;
    onCategoryClick: (id: string) => void;
}

export default function CategoryNav({ categories, activeCategoryId, onCategoryClick }: CategoryNavProps) {
    return (
        <nav className="overflow-x-auto whitespace-nowrap no-scrollbar">
            <div className="flex space-x-2">
                <div className="w-4 flex-shrink-0"></div>
                {categories.map((category) => (
                    <button
                        key={category.id}
                        onClick={() => onCategoryClick(category.id)}
                        className={`px-3 py-3 rounded-3xl button-n font-semibold transition-colors duration-300 flex-shrink-0
                        ${activeCategoryId === category.id
                                ? category.color
                                : 'bg-grey-97 text-grey-60'
                            }`}
                    >
                        {category.title}
                    </button>
                ))}
                {/* 끝 여백 */}
                <div className="w-4 flex-shrink-0"></div>
            </div>
        </nav>
    );
}
