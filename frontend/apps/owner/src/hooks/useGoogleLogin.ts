import { AuthService } from '@/services/authService';

/**
 * Google OAuth 로그인을 처리하는 훅
 * 
 * @description
 * Google OAuth 인증을 통한 로그인 기능을 제공합니다.
 * 
 * @returns {Object} Google 로그인 관련 함수들
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
