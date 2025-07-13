interface Category {
    id: string;
    title: string;
    color: string;
}

interface CategoryNavProps {
    categories: Category[];
    activeCategoryId: string;
    onCategoryClick: (id: string) => void;
}

export default function CategoryNav({ categories, activeCategoryId, onCategoryClick }: CategoryNavProps) {
    return (
        <nav className="overflow-x-auto whitespace-nowrap no-scrollbar">
            <div className="flex space-x-2">
                {categories.map((category) => (
                    <button
                        key={category.id}
                        onClick={() => onCategoryClick(category.id)}
                        className={`px-3 py-3 rounded-3xl button-n font-semibold transition-colors duration-300
                        ${activeCategoryId === category.id
                                ? category.color
                                : 'bg-grey-97 text-grey-60'
                            }`}
                    >
                        {category.title}
                    </button>
                ))}
            </div>
        </nav>
    );
}