import { useEffect } from 'react';
import { useAuthStore, useUserStore } from 'common';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/router';
import { AuthService } from '@/services/authService';

export const useGoogleLogin = () => {
  const setAccessToken = useAuthStore(state => state.setAccessToken);
  const setUser = useUserStore(state => state.setUser);
  const navigate = useNavigate();

  const { mutate: exchangeCodeForToken } = useMutation({
    mutationFn: AuthService.exchangeCodeForToken,
    onSuccess: data => {
      setAccessToken(data.accessToken);
      setUser(data.user);
      navigate(ROUTE_PATH.HOME);
    },
    onError: error => {
      console.error('구글 로그인 실패:', error);
      throw error;
    },
  });

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    if (code) {
      window.history.replaceState({}, document.title, ROUTE_PATH.HOME);
      exchangeCodeForToken(code);
    }
  }, [exchangeCodeForToken]);

  const loginWithGoogle = () => {
    const googleAuthUrl = AuthService.generateGoogleAuthUrl();
    window.location.href = googleAuthUrl;
  };

  return { loginWithGoogle };
};
