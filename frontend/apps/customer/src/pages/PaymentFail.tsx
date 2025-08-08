import { useSearchParams, useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router/route-path';

const PaymentFail = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const code = searchParams.get('code');
  const message = searchParams.get('message');
  const orderId = searchParams.get('orderId');

  const handleRetry = () => {
    // 결제 페이지로 다시 이동
    navigate(ROUTE_PATH.PAY);
  };

  const handleGoHome = () => {
    navigate(ROUTE_PATH.HOME);
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-md rounded-lg bg-white p-8 text-center shadow-lg">
        <div className="mb-6 h-20 w-20 rounded-full bg-red-100 flex items-center justify-center mx-auto">
          <span className="text-3xl">❌</span>
        </div>
        
        <h1 className="mb-4 text-2xl font-bold text-red-600">결제 실패</h1>
        
        <div className="mb-6 space-y-2 text-left">
          {orderId && (
            <div className="rounded bg-gray-50 p-3">
              <span className="text-sm font-medium text-gray-600">주문번호:</span>
              <p className="font-mono text-sm">{orderId}</p>
            </div>
          )}
          
          {code && (
            <div className="rounded bg-gray-50 p-3">
              <span className="text-sm font-medium text-gray-600">오류 코드:</span>
              <p className="font-mono text-sm">{code}</p>
            </div>
          )}
          
          {message && (
            <div className="rounded bg-gray-50 p-3">
              <span className="text-sm font-medium text-gray-600">오류 메시지:</span>
              <p className="text-sm">{message}</p>
            </div>
          )}
        </div>

        <div className="space-y-3">
          <button
            onClick={handleRetry}
            className="w-full rounded-lg bg-amber-500 py-3 text-white font-semibold hover:bg-amber-600 transition-colors"
          >
            다시 결제하기
          </button>
          
          <button
            onClick={handleGoHome}
            className="w-full rounded-lg border border-gray-300 py-3 text-gray-700 font-semibold hover:bg-gray-50 transition-colors"
          >
            홈으로 돌아가기
          </button>
        </div>

        <div className="mt-6 text-xs text-gray-500">
          <p>문제가 지속되면 고객센터로 문의해 주세요.</p>
        </div>
      </div>
    </div>
  );
};

export default PaymentFail;