import { Link } from 'react-router-dom';
import { Home, Package } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { ROUTE_PATH } from '@/router/route-path';

const NotFound = () => {
  return (
    <div className="flex h-full items-center justify-center bg-gradient-to-br from-yellow-50 to-orange-50 p-4">
      <div className="max-w-md space-y-8 text-center">
        {/* 브랜드 곰돌이 일러스트 영역 */}
        <div className="relative">
          <div className="relative mx-auto h-36 w-36">
            {/* 귀 */}
            <div className="absolute left-4 top-1 h-6 w-6 rounded-full bg-yellow-400"></div>
            <div className="absolute right-4 top-1 h-6 w-6 rounded-full bg-yellow-400"></div>

            {/* 곰돌이 얼굴 */}
            <div className="relative mx-auto mt-3 flex h-28 w-28 items-center justify-center rounded-full bg-yellow-300">
              <div className="relative h-20 w-20 rounded-full bg-yellow-400">
                {/* 눈 */}
                <div className="absolute left-3 top-5 h-2 w-2 rounded-full bg-white"></div>
                <div className="absolute right-3 top-5 h-2 w-2 rounded-full bg-white"></div>
                {/* 코 */}
                <div className="absolute left-1/2 top-8 h-2 w-3 -translate-x-1/2 transform rounded-full bg-yellow-600"></div>
                {/* 입 */}
                <div className="absolute left-1/2 top-10 h-3 w-1 -translate-x-1/2 transform rounded-full bg-yellow-600"></div>
              </div>
            </div>

            {/* 띱박스 */}
            <Package className="absolute bottom-2 right-2 h-8 w-8 text-yellow-600" />
          </div>
          {/* 물음표 효과 */}
          <div className="absolute -right-4 -top-2 animate-bounce text-4xl text-yellow-600">
            ?
          </div>
        </div>

        {/* 메시지 영역 */}
        <div className="space-y-3">
          <h1 className="text-6xl font-bold text-yellow-600">404</h1>
          <h2 className="text-2xl font-bold text-gray-800">잘못된 경로예요!</h2>
          <p className="leading-relaxed text-gray-600">
            이 페이지는 존재하지 않거나
            <br />
            다른 곳으로 이동했을 수 있습니다.
          </p>
        </div>

        {/* 버튼 영역 */}
        <Button
          asChild
          className="bg-yellow-500 text-white shadow-lg hover:bg-yellow-600"
        >
          <Link
            to={ROUTE_PATH.INDEX}
            className="inline-flex items-center gap-2"
          >
            <Home className="h-4 w-4" />
            홈으로 돌아가기
          </Link>
        </Button>
      </div>
    </div>
  );
};

export default NotFound;
