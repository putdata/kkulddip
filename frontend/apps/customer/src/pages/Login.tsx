import { ROUTE_PATH } from '@/router/route-path';
import { Link } from 'react-router-dom';
import GoogleLoginButton from '@/components/pages/login/GoogleLoginButton';
import LoginCarousel from '@/components/pages/login/LoginCarousel';

const Login = () => {
  return (
    <div className="flex flex-col items-center bg-amber-200">
      <div className="m-10 h-full w-full max-w-xs">
        <LoginCarousel />
      </div>
      <div className="m-3 flex w-full max-w-xs flex-col items-center">
        <GoogleLoginButton />
        <Link
          to={ROUTE_PATH.HOME}
          className="m-3 mb-5 text-sm text-gray-600 underline"
        >
          다음에 로그인할게요
        </Link>
      </div>
    </div>
  );
};

export default Login;
