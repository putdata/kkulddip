import { useEffect } from 'react';
import { useAuthStore, useUserStore } from 'common';
import { useMutation } from '@tanstack/react-query';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ROUTE_PATH } from '@/router/route-path';
import { AuthService } from '@/services/authService';

const AuthCallback = () => {
  const setAccessToken = useAuthStore(state => state.setAccessToken);
  const setUser = useUserStore(state => state.setUser);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const {
    mutate: exchangeCodeForToken,
    isPending,
    isError,
  } = useMutation({
    mutationFn: AuthService.exchangeCodeForToken,
    onSuccess: data => {
      setAccessToken(data.accessToken);
      setUser({
        ...data.user,
        userid: data.user.id,
      });
      navigate(ROUTE_PATH.HOME);
    },
    onError: error => {
      console.error('구글 로그인 실패:', error);
      navigate(ROUTE_PATH.LOGIN);
    },
  });

  useEffect(() => {
    const code = searchParams.get('code');
    const error = searchParams.get('error');

    if (error) {
      console.error('OAuth 오류:', error);
      navigate(ROUTE_PATH.LOGIN);
      return;
    }

    if (code) {
      exchangeCodeForToken(code);
    } else {
      navigate(ROUTE_PATH.LOGIN);
    }
  }, [searchParams, exchangeCodeForToken, navigate]);

  if (isPending) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <div className="mb-4 h-8 w-8 animate-spin rounded-full border-4 border-gray-300 border-t-blue-600"></div>
          <p>로그인 처리 중...</p>
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div className="text-center">
          <p className="mb-4 text-red-600">로그인 중 오류가 발생했습니다.</p>
          <button
            onClick={() => navigate(ROUTE_PATH.LOGIN)}
            className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
          >
            로그인 화면으로 돌아가기
          </button>
        </div>
      </div>
    );
  }

  return null;
};

export default AuthCallback;
