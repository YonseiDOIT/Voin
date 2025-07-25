import AiClassify from "@/components/findCoin/AiClassify"

const AiClassifyPage = () => {
    return (
        <div className="w-full h-full">
            <AiClassify nextPath="/todays-diary/advantage-result" />
        </div>
    );
}

export default AiClassifyPage;