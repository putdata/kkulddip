import axios from 'axios';
import { useEffect } from 'react';

import { Button } from '@/components/ui/button';

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

const GoogleLoginButton = () => {
  const baseUrl = 'https://api.kkulddip.store/api/v1';

  // 페이지 로드시 URL에서 code 확인
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    if (code) {
      window.history.replaceState({}, document.title, '/');
      handleGoogleCallback(code);
    }
  }, []);

  const handleGoogleCallback = async (code: string): Promise<void> => {
    try {
      const response = await axios.post<GoogleAuthResponse>(
        `${baseUrl}/auth/cutomer/token`,
        { code },
      );

      console.log('로그인 성공:', response.data);

      localStorage.setItem('accessToken', response.data.accessToken);
      localStorage.setItem('refreshToken', response.data.refreshToken);
      localStorage.setItem('expiresIn', response.data.expiresIn.toString());
      localStorage.setItem('user', JSON.stringify(response.data.user));

      window.location.href = '/';
    } catch (error) {
      console.error('로그인 실패:', error);
      alert('로그인에 실패했습니다. 다시 시도해주세요.');
    }
  };

  const loginWithGoogle = () => {
    const clientId = import.meta.env.VITE_GOOGLE_OAUTH2_CLIENT_ID as string;
    const redirectUri = window.location.origin; // 현재 페이지로 다시 돌아옴
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
  return (
    <Button
      variant="outline"
      className="mx-3 flex h-12 max-w-sm items-center justify-center gap-2 text-sm"
      onClick={loginWithGoogle}
    >
      <img src="google_logo.png" alt="google logo" className="mr-2 h-5 w-5" />
      Google 계정으로 계속하기
    </Button>
  );
};

export default GoogleLoginButton;
