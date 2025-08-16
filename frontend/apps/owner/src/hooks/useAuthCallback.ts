import { useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { useAuthStore, useUserStore, useRedirectStore } from 'common';
import { toast } from 'sonner';
import { AuthService } from '@/services/authService';
import { ROUTE_PATH } from '@/router/route-path';

/**
 * OAuth 콜백 처리를 위한 훅
 *
 * @description
 * Google OAuth 인증 완료 후 콜백 URL을 처리합니다.
 * 인증 코드를 액세스 토큰으로 교환하고 사용자 정보를 저장합니다.
 */
export const useAuthCallback = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const setAccessToken = useAuthStore(state => state.setAccessToken);
  const setUser = useUserStore(state => state.setUser);
  const { getAndClearRedirectUrl } = useRedirectStore();
  const hasProcessed = useRef(false);

  const { mutate: exchangeCodeForToken } = useMutation({
    mutationFn: AuthService.exchangeCodeForToken,
    onSuccess: data => {
      setAccessToken(data.accessToken);
      setUser({
        ...data.user,
      });
      toast.success('로그인 성공!');

      const redirectUrl = getAndClearRedirectUrl();
      if (redirectUrl) {
        navigate(redirectUrl, { replace: true });
      } else {
        navigate(ROUTE_PATH.INDEX, { replace: true });
      }
    },
    onError: error => {
      console.error('Token exchange failed:', error);
      toast.error('로그인 처리 중 오류가 발생했습니다.');
      navigate(ROUTE_PATH.LOGIN, { replace: true });
    },
  });

  useEffect(() => {
    if (hasProcessed.current) {
      return;
    }

    const urlParams = new URLSearchParams(location.search);
    const code = urlParams.get('code');
    const errorParam = urlParams.get('error');

    if (errorParam !== null) {
      toast.error('로그인이 취소되었습니다.');
      navigate(ROUTE_PATH.LOGIN, { replace: true });
      return;
    }

    if (!code) {
      toast.error('인증 코드가 없습니다.');
      navigate(ROUTE_PATH.LOGIN, { replace: true });
      return;
    }

    hasProcessed.current = true;
    exchangeCodeForToken(code);
    // OAuth 콜백은 컴포넌트 마운트 시 한 번만 실행되어야 함
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};
