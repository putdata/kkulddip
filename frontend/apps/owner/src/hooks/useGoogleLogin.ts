import { AuthService } from '@/services/authService';

/**
 * Google OAuth 로그인을 처리하는 훅
 */
export const useGoogleLogin = () => {
  /**
   * Google OAuth 로그인 페이지로 리다이렉트
   */
  const loginWithGoogle = () => {
    const googleAuthUrl = AuthService.generateGoogleAuthUrl();
    window.location.href = googleAuthUrl;
  };

  return { loginWithGoogle };
};
