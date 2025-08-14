import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import GoogleLoginButton from '@/components/GoogleLoginButton';

const Login = () => {
  return (
    <div className="flex min-h-full items-center justify-center bg-gray-50 p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl font-bold">Owner 로그인</CardTitle>
          <p className="text-gray-600">스트리밍 서비스 관리자 로그인</p>
        </CardHeader>
        <CardContent className="flex flex-col items-center space-y-6">
          <div className="flex w-full justify-center">
            <GoogleLoginButton />
          </div>

          <div className="text-center text-sm text-gray-500">
            <p>Google 계정으로 로그인하여</p>
            <p>스트리밍 서비스를 관리하세요</p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default Login;
