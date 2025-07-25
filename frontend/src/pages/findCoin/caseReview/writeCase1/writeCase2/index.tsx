import WritingPrompt from '@/components/findCoin/WritingPrompt';

const WriteCase = () => {
    return (
        <WritingPrompt
            title="내 행동에 대해 어떻게 생각하나요?"
            tip="그때의 내 행동을 돌이켜봤을 때, 느꼈던 감정이나 인상 깊었던 점을 편하게 적어보세요."
            placeholder="내 행동에 대한 생각을 자유롭게 적어주세요."
            directLinkToAi='/case-review/ai-classify'
            directLinkTo="/case-review/categories"
            minLength={0}
            showBottomSheet={true}
            dataKey="writtenCase2"
        />
    );
}

export default WriteCase;
