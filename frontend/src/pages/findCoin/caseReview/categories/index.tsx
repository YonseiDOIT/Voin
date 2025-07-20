import StrengthSelector from '../../../../components/findCoin/StrengthSelector';

export default function CaseReviewCategory() {
    const handleNext = (selectedItems: { category: string; item: string }[]) => {
        if (selectedItems.length > 0) {
            console.log('Selected items:', selectedItems);
        } else {
            console.warn('No items selected');
        }
    };

    return (
        <StrengthSelector
            title="그 순간, 어떤 장점이 드러났나요?"
            caption="가장 돋보였다고 생각하는 장점 하나만 골라주세요."
            onNext={handleNext}
        />
    );
}
