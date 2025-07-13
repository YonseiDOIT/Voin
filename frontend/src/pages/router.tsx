import { Link } from 'react-router-dom';

const Router = () => {
    return (
        <div className="w-full h-full flex flex-col">
            <h1>Router Page</h1>
            <div>
                <Link to="/test" className='inline-flex flex-row items-center'>
                    <span className='text-blue-500'>컴포넌트 테스트 페이지 바로가기</span>
                </Link>
            </div>
            <div>
                <Link to="/home" className='inline-flex flex-row items-center'>
                    <span className='text-blue-500'>메인 페이지 바로가기</span>
                </Link>
            </div>
            <div>
                <Link to="/register-profile" className='inline-flex flex-row items-center'>
                    <span className='text-blue-500'>프로필 등록 페이지 바로가기</span>
                </Link>
            </div>
        </div>
    );
}

export default Router;