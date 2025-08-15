import { Navigate, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from 'common';
import type { ReactNode } from 'react';
import { useEffect, useRef } from 'react';
import { ROUTE_PATH } from '@/router/route-path';

const ProtectedRoute = ({ children }: { children: ReactNode }) => {
  const { accessToken } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();
  const prevTokenRef = useRef<string | null>(accessToken);

  useEffect(() => {
    // 이전 토큰 값 업데이트
    prevTokenRef.current = accessToken;

    // authStore 상태 변경을 감지하여 401 에러로 인한 토큰 제거 시 즉시 리다이렉트
    const unsubscribe = useAuthStore.subscribe(state => {
      // 토큰이 있었다가 제거된 경우 (401 에러 상황)
      if (prevTokenRef.current && !state.accessToken) {
        // 현재 페이지가 로그인 페이지가 아닌 경우에만 리다이렉트
        if (location.pathname !== ROUTE_PATH.LOGIN) {
          navigate(ROUTE_PATH.LOGIN, { replace: true });
        }
        prevTokenRef.current = null;
      }
    });

    return () => unsubscribe();
  }, [accessToken, navigate, location.pathname]);

  // 초기 인증 상태 체크
  if (!accessToken) {
    return <Navigate to={ROUTE_PATH.LOGIN} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
