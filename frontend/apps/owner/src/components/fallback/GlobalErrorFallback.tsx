import { useNavigate } from 'react-router-dom';

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
  const navigate = useNavigate();

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
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md rounded-lg bg-white p-6 text-center shadow-lg">
        <h1 className="mb-4 text-2xl font-bold text-red-600">
          오류가 발생했습니다
        </h1>
        <p className="mb-4 text-gray-600">
          예상치 못한 오류가 발생했습니다. 페이지를 새로고침하거나 잠시 후 다시
          시도해주세요.
        </p>
        {error && (
          <details className="mb-4 text-left text-sm text-gray-500">
            <summary className="cursor-pointer">오류 상세 정보</summary>
            <pre className="mt-2 rounded bg-gray-100 p-2">{error.message}</pre>
          </details>
        )}
        <button
          onClick={handleRetry}
          className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
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
    </div>
  );
};

export default GlobalErrorFallback;
