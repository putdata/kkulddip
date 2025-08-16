import { Navigate, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore, useRedirectStore } from 'common';
import type { ReactNode } from 'react';
import { useEffect, useRef } from 'react';
import { ROUTE_PATH } from '@/router/route-path';

const ProtectedRoute = ({ children }: { children: ReactNode }) => {
  const { accessToken } = useAuthStore();
  const { setRedirectUrl } = useRedirectStore();
  const navigate = useNavigate();
  const location = useLocation();
  const prevTokenRef = useRef<string | null>(accessToken);

  useEffect(() => {
    // 초기 인증 상태 체크 - 토큰이 없으면 리다이렉트 URL 설정
    if (!accessToken) {
      setRedirectUrl(location.pathname + location.search);
    }

    // 이전 토큰 값 업데이트
    prevTokenRef.current = accessToken;

    // authStore 상태 변경을 감지하여 401 에러로 인한 토큰 제거 시 즉시 리다이렉트
    const unsubscribe = useAuthStore.subscribe(state => {
      // 토큰이 있었다가 제거된 경우 (401 에러 상황)
      if (prevTokenRef.current && !state.accessToken) {
        // 현재 페이지가 로그인 페이지가 아닌 경우에만 리다이렉트
        if (location.pathname !== ROUTE_PATH.LOGIN) {
          // 현재 경로를 저장하고 로그인 페이지로 이동
          setRedirectUrl(location.pathname + location.search);
          navigate(ROUTE_PATH.LOGIN, { replace: true });
        }
        prevTokenRef.current = null;
      }
    });

    return () => unsubscribe();
  }, [
    accessToken,
    navigate,
    location.pathname,
    location.search,
    setRedirectUrl,
  ]);

  // 초기 인증 상태 체크
  if (!accessToken) {
    return <Navigate to={ROUTE_PATH.LOGIN} replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
