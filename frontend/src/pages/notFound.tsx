import { Link } from 'react-router-dom';

const NotFound = () => {
    return (
        <div className="w-full bg-white h-screen flex flex-col">
            <div className="flex flex-col items-center justify-center h-screen">
                <h1 className="text-4xl font-bold text-red-600">404 - Page Not Found</h1>
                <p className="mt-4 text-lg text-gray-700">The page you are looking for does not exist.</p>
                <Link to="/home" className="mt-6 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors">
                    Go back to Home
                </Link>
            </div>
        </div>
    );
}

export default NotFound;