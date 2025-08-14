import { AuthService } from '@/services/authService';

export const useGoogleLogin = () => {
  const loginWithGoogle = () => {
    const googleAuthUrl = AuthService.generateGoogleAuthUrl();
    window.location.href = googleAuthUrl;
  };

  return { loginWithGoogle };
};
