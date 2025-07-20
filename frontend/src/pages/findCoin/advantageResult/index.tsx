import AdvantageResultComponent from '../../../components/advantageResult/AdvantageResult';
import { getCategoryTheme } from '../../../components/advantageResult/advantageResultTypes';

const AdvantageResultPage = () => {
    return (
        <div className="w-full h-full overflow-y-auto flex flex-col px-4">
            <AdvantageResultComponent
                theme={getCategoryTheme('emotion')}
                category="관리와 성장"
                title="계획 세우기"
                titleDescription="체계적인 계획을 통해 목표를 달성하는 능력"
                description="당신은 미래를 내다보고 체계적으로 계획을 세우는데 뛰어납니다."
            />
        </div>
    );
};

export default AdvantageResultPage;
