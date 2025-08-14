import { useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore, useUserStore } from 'common';
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

  /**
   * OAuth 콜백 URL 처리 및 토큰 교환
   */
  const processCallback = async () => {
    const urlParams = new URLSearchParams(location.search);
    const code = urlParams.get('code');
    const errorParam = urlParams.get('error');

    if (errorParam !== null) {
      toast.error('로그인이 취소되었습니다.');
      navigate(ROUTE_PATH.LOGIN);
      return;
    }

    if (code === null || code === '') {
      toast.error('인증 코드가 없습니다.');
      navigate(ROUTE_PATH.LOGIN);
      return;
    }

    const data = await AuthService.exchangeCodeForToken(code);

    setAccessToken(data.accessToken);
    setUser({
      ...data.user,
      userId: data.user.id,
    });
    toast.success('로그인 성공!');

    navigate(ROUTE_PATH.INDEX, { replace: true });
  };

  return { processCallback };
};
