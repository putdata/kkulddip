import { Navigate, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from 'common';
import type { ReactNode } from 'react';
import { useEffect, useState } from 'react';
import { ROUTE_PATH } from '@/router/route-path';

const ProtectedRoute = ({ children }: { children: ReactNode }) => {
  const { accessToken } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();
  const [isAuthChecked, setIsAuthChecked] = useState(false);

  useEffect(() => {
    // 컴포넌트가 마운트될 때 한 번 인증 상태를 체크
    setIsAuthChecked(true);

    // authStore 상태 변경을 감지하여 401 에러로 인한 토큰 제거 시 즉시 리다이렉트
    const unsubscribe = useAuthStore.subscribe(state => {
      // 토큰이 제거되면 리다이렉트 (초기 로딩이 아닌 경우에만)
      if (isAuthChecked && !state.accessToken && accessToken) {
        // 현재 페이지가 로그인 페이지가 아닌 경우에만 리다이렉트
        if (location.pathname !== ROUTE_PATH.LOGIN) {
          navigate(ROUTE_PATH.LOGIN, { replace: true });
        }
      }
    });

    return () => unsubscribe();
  }, [isAuthChecked, accessToken, navigate, location.pathname]);

  // 초기 인증 상태 체크
  if (!accessToken) {
    return <Navigate to={ROUTE_PATH.LOGIN} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
