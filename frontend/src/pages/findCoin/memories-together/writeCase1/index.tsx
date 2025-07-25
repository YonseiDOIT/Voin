import WritingPrompt from '@/components/findCoin/WritingPrompt';

const WriteCase = () => {
    return (
        <WritingPrompt
            title="그때 나는 어떤 행동을 했었나요?"
            tip="기억이 남는 순간에서 상대방이 구체적으로 어떤 행동과 선택을 했는지 편하게 적어보세요."
            placeholder="과거의 순간에서 상대방이 어떤 행동을 했었는지 자유롭게 작성해 보세요. 최소 40자 이상 작성해야 장점을 찾을 수 있어요."
            directLinkTo="/memories-together/write-case-2"
            dataKey="writtenCase1"
        />
    );
}

export default WriteCase;
