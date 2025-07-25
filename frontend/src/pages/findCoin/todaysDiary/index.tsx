import WritingPrompt from '@/components/findCoin/WritingPrompt';

const TodaysDiary = () => {
    return (
        <WritingPrompt
            title="오늘 하루, 어떤 일이 있었나요?"
            tip="거창하지 않아도 괜찮아요. 오늘의 생각, 선택, 감정을 편하게 적어보세요. 일상을 적다 보면, 그 안에서 나만의 장점을 발견할 수 있어요."
            placeholder="오늘 일상 내용을 자유롭게 작성해 보세요. 최소 40자 이상 작성해야 장점을 찾을 수 있어요."
            directLinkTo="/todays-diary/categories"
            directLinkToAi="/todays-diary/ai-classify"
            showBottomSheet={true}
            dataKey="writtenCase1"
        />
    );
}

export default TodaysDiary;
