import { ROUTE_PATH } from '@/router/route-path';
import { Link } from 'react-router-dom';
import GoogleLoginButton from '@/components/pages/login/GoogleLoginButton';
import LoginCarousel from '@/components/pages/login/LoginCarousel';

const Login = () => {
  return (
    <div className="flex min-h-dvh w-full flex-col items-center justify-center bg-amber-200">
      <div className="flex w-full flex-1 items-center justify-center px-10">
        <div className="w-full max-w-xs">
          <LoginCarousel />
        </div>
      </div>
      <div className="pb-30 flex w-full justify-center">
        <div className="flex w-full max-w-xs flex-col items-center gap-3">
          <GoogleLoginButton />
          <Link
            to={ROUTE_PATH.HOME}
            className="text-sm text-gray-600 underline"
          >
            다음에 로그인할게요
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
