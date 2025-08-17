import { useNavigate } from 'react-router-dom';
import { RefreshCw, Home, Store } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';

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
    <div className="h-full bg-gradient-to-br from-amber-50 via-white to-orange-50 p-4">
      {/* Animated Background Elements */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="animate-blob absolute -left-24 top-1/4 h-64 w-64 rounded-full bg-amber-300 opacity-10 mix-blend-multiply blur-xl filter"></div>
        <div className="animate-blob animation-delay-2000 absolute -right-24 top-1/3 h-64 w-64 rounded-full bg-orange-300 opacity-10 mix-blend-multiply blur-xl filter"></div>
        <div className="animate-blob animation-delay-4000 absolute bottom-1/4 left-1/2 h-64 w-64 -translate-x-1/2 rounded-full bg-yellow-300 opacity-10 mix-blend-multiply blur-xl filter"></div>
      </div>

      <div className="relative z-10 flex h-full items-center justify-center">
        <Card className="w-full max-w-lg border-0 bg-white/90 shadow-2xl backdrop-blur-sm">
          <CardContent className="space-y-8 p-12 text-center">
            {/* Logo and Brand */}
            <div className="space-y-4">
              <div className="mx-auto flex items-center justify-center">
                <div className="rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 p-3 shadow-lg">
                  <Store className="h-8 w-8 text-white" />
                </div>
              </div>
              <h1 className="text-2xl font-bold">
                <span className="bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent">
                  꿀띱
                </span>
              </h1>
            </div>

            {/* Error Message */}
            <div className="space-y-4">
              <div className="space-y-2">
                <h2 className="text-2xl font-bold text-gray-900">
                  앗! 문제가 발생했어요
                </h2>
                <p className="leading-relaxed text-gray-600">
                  예상치 못한 오류가 발생했습니다.
                  <br />
                  페이지를 새로고침하거나 잠시 후 다시 시도해주세요.
                </p>
              </div>

              {/* Error Details */}
              {error && (
                <details className="rounded-lg bg-gray-50 p-4 text-left text-sm">
                  <summary className="cursor-pointer font-medium text-gray-700 hover:text-gray-900">
                    오류 상세 정보
                  </summary>
                  <div className="mt-3 rounded-md bg-gray-100 p-3 font-mono text-xs text-gray-600">
                    {error.message}
                    {error.stack && (
                      <div className="mt-2 text-gray-500">
                        {error.stack.split('\n').slice(0, 3).join('\n')}
                      </div>
                    )}
                  </div>
                </details>
              )}
            </div>

            {/* Action Buttons */}
            <div className="space-y-3">
              <Button
                onClick={handleRetry}
                className="w-full bg-gradient-to-r from-amber-500 to-orange-500 text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl"
              >
                <RefreshCw className="mr-2 h-4 w-4" />
                다시 시도
              </Button>

              <Button
                onClick={handleGoHome}
                variant="outline"
                className="w-full border-amber-300 text-amber-700 hover:bg-amber-50"
              >
                <Home className="mr-2 h-4 w-4" />
                홈으로 돌아가기
              </Button>
            </div>

            {/* Help Text */}
            <div className="rounded-lg bg-gradient-to-r from-amber-50 to-orange-50 p-4">
              <p className="text-sm text-gray-600">
                문제가 계속 발생한다면
                <span className="font-medium text-amber-700">고객센터</span>로
                문의해 주세요
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default GlobalErrorFallback;
