import { Link } from 'react-router-dom';
import { ROUTE_PATH } from '@/router/route-path';

const NotFound = () => {
  return (
    <div className="flex min-h-full items-center justify-center bg-gray-100">
      <div className="w-full max-w-md rounded-lg bg-white p-6 text-center shadow-lg">
        <h1 className="mb-4 text-6xl font-bold text-gray-400">404</h1>
        <h2 className="mb-4 text-2xl font-bold text-gray-800">
          페이지를 찾을 수 없습니다
        </h2>
        <p className="mb-6 text-gray-600">
          요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.
        </p>
        <Link
          to={ROUTE_PATH.INDEX}
          className="inline-block rounded bg-blue-600 px-6 py-3 text-white transition-colors hover:bg-blue-700"
        >
          홈으로 돌아가기
        </Link>
      </div>
    </div>
  );
};

export default NotFound;
