import { useEffect } from 'react';
import { useAuthStore } from 'common';
import { useUserStore } from 'common';
import { useMutation } from '@tanstack/react-query';
import { apiClient } from 'common';
import { useNavigate } from 'react-router-dom';

interface GoogleAuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: {
    email: string;
    name: string;
    role: string;
    profileImageUrl: string;
  };
}

export const useGoogleLogin = () => {
  const setAccessToken = useAuthStore(state => state.setAccessToken);
  const redirectUri = import.meta.env.VITE_OAUTH2_SUCCESS_REDIRECT_URL;
  const setUser = useUserStore(state => state.setUser);
  const navigate = useNavigate();

  const { mutate: exchangeCodeForToken } = useMutation({
    mutationFn: (code: string) =>
      apiClient.post<GoogleAuthResponse>('/auth/customer/token', { code }),
    onSuccess: data => {
      setAccessToken(data.accessToken);
      setUser(data.user);
      navigate('/');
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
      window.history.replaceState({}, document.title, '/');
      exchangeCodeForToken(code);
    }
  }, [exchangeCodeForToken]);

  const loginWithGoogle = () => {
    const clientId = import.meta.env.VITE_GOOGLE_OAUTH2_CLIENT_ID;
    const scope = 'openid email profile';

    const googleAuthUrl =
      `https://accounts.google.com/o/oauth2/v2/auth?` +
      `client_id=${clientId}&` +
      `redirect_uri=${redirectUri}&` +
      `response_type=code&` +
      `scope=${scope}&` +
      `access_type=offline`;

    window.location.href = googleAuthUrl;
  };

  return { loginWithGoogle };
};
