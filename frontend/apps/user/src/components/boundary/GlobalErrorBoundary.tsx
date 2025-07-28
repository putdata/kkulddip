import { ErrorBoundary as ReactErrorBoundary } from 'react-error-boundary';
import { type ReactNode, type ErrorInfo } from 'react';
import GlobalErrorFallback from '@/components/fallback/GlobalErrorFallback';

interface GlobalErrorBoundaryProps {
  children: ReactNode;
}

/**
 * 글로벌 에러 바운더리 레퍼 컴포넌트
 *
 * 애플리케이션 전체를 감싸는 에러 바운더리입니다.
 * React 컴포넌트 렌더링 중 발생하는 JavaScript 에러를 포착하고
 * 사용자에게 친화적인 에러 UI를 보여줍니다.
 *
 * @returns 에러 바운더리가 적용된 레퍼 컴포넌트
 */
const GlobalErrorBoundary = ({ children }: GlobalErrorBoundaryProps) => {
  const handleError = (error: Error, errorInfo: ErrorInfo) => {
    // 에러 로깅
    console.error('Global Error Boundary caught an error:', error);
    console.error('Component Stack:', errorInfo.componentStack);
  };

  const handleReset = () => {
    // 에러 발생 시 필요한 상태 초기화
    console.log('Error boundary reset triggered');
  };

  return (
    <ReactErrorBoundary
      FallbackComponent={GlobalErrorFallback}
      onError={handleError}
      onReset={handleReset}
      resetKeys={[]}
    >
      {children}
    </ReactErrorBoundary>
  );
};

export default GlobalErrorBoundary;
