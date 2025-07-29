import { useRouteError, useNavigate } from 'react-router-dom';

interface GlobalErrorFallbackProps {
  error?: Error;
  resetErrorBoundary?: () => void;
}

/**
 * 글로벌 에러 폴백 컴포넌트
 *
 * 애플리케이션에서 처리되지 않은 에러가 발생했을 때 사용자에게 표시되는 UI입니다.
 * Error Boundary와 React Router의 라우트 에러를 모두 처리할 수 있습니다.
 *
 * @param error - Error Boundary에서 전달받은 에러 객체
 * @param resetErrorBoundary - 에러 상태를 리셋하는 함수
 * @returns 에러 폴백 UI 컴포넌트
 */
const GlobalErrorFallback = ({
  error,
  resetErrorBoundary,
}: GlobalErrorFallbackProps) => {
  const routeError = useRouteError() as Error | null;
  const navigate = useNavigate();

  const displayError = error || routeError;

  const handleRetry = () => {
    if (resetErrorBoundary) {
      resetErrorBoundary();
    } else {
      window.location.reload();
    }
  };

  const handleGoHome = () => {
    if (resetErrorBoundary) {
      resetErrorBoundary();
    }

    try {
      navigate('/');
    } catch {
      window.location.href = '/';
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md text-center">
        <div className="mb-6">
          <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-red-100">
            <svg
              className="h-8 w-8 text-red-600"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z"
              />
            </svg>
          </div>
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            앗, 문제가 발생했어요
          </h1>
          <p className="mb-6 text-gray-600">
            예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.
          </p>
        </div>

        {process.env.NODE_ENV === 'development' && displayError && (
          <div className="mb-6 rounded-lg bg-red-50 p-4 text-left">
            <h3 className="mb-2 text-sm font-medium text-red-800">
              개발 모드 - 에러 정보:
            </h3>
            <pre className="whitespace-pre-wrap break-words text-xs text-red-700">
              {displayError.message || 'Unknown error'}
            </pre>
          </div>
        )}

        <div className="space-y-3">
          <button
            onClick={handleRetry}
            className="w-full rounded-lg bg-blue-600 px-4 py-3 font-medium text-white transition-colors hover:bg-blue-700"
          >
            다시 시도
          </button>
          <button
            onClick={handleGoHome}
            className="w-full rounded-lg bg-gray-100 px-4 py-3 font-medium text-gray-700 transition-colors hover:bg-gray-200"
          >
            홈으로 돌아가기
          </button>
        </div>

        <p className="mt-6 text-sm text-gray-500">
          문제가 계속 발생하면 고객센터로 문의해주세요.
        </p>
      </div>
    </div>
  );
};

export default GlobalErrorFallback;
