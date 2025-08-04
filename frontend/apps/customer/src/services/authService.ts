import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import { type GoogleAuthResponse } from '@/types/auth';

export const AuthService = {
  /**
   * Google OAuth 인증 코드로 토큰 교환
   */
  exchangeCodeForToken: (code: string) => {
    return apiClient.post<GoogleAuthResponse>(API_PATH.AUTH_EXCHANGE_TOKEN, {
      code,
    });
  },

  /**
   * 구글 로그인 URL 생성기
   */
  generateGoogleAuthUrl: (): string => {
    const clientId = import.meta.env.VITE_GOOGLE_OAUTH2_CLIENT_ID;
    const redirectUri = import.meta.env.VITE_OAUTH2_SUCCESS_REDIRECT_URL;
    const scope = 'openid email profile';

    return (
      `https://accounts.google.com/o/oauth2/v2/auth?` +
      `client_id=${clientId}&` +
      `redirect_uri=${redirectUri}&` +
      `response_type=code&` +
      `scope=${scope}&` +
      `access_type=offline`
    );
  },
};
