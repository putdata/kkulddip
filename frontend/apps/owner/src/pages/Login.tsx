import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Store, Sparkles } from 'lucide-react';
import GoogleLoginButton from '@/components/GoogleLoginButton';

const Login = () => {
  return (
    <div className="h-full bg-gradient-to-br from-amber-50 via-white to-orange-50 p-4">
      {/* Animated Background Elements */}
      <div className="absolute inset-0 overflow-hidden">
        <div className="animate-blob absolute -left-24 top-1/4 h-64 w-64 rounded-full bg-amber-300 opacity-20 mix-blend-multiply blur-xl filter"></div>
        <div className="animate-blob animation-delay-2000 absolute -right-24 top-1/3 h-64 w-64 rounded-full bg-orange-300 opacity-20 mix-blend-multiply blur-xl filter"></div>
        <div className="animate-blob animation-delay-4000 absolute bottom-1/4 left-1/2 h-64 w-64 -translate-x-1/2 rounded-full bg-yellow-300 opacity-20 mix-blend-multiply blur-xl filter"></div>
      </div>

      <div className="relative z-10 flex h-full items-center justify-center">
        <Card className="w-full max-w-md border-0 bg-white/90 shadow-2xl backdrop-blur-sm">
          <CardHeader className="space-y-6 text-center">
            {/* Logo */}
            <div className="mx-auto flex items-center justify-center">
              <div className="rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 p-3 shadow-lg">
                <Store className="h-8 w-8 text-white" />
              </div>
            </div>

            {/* Title */}
            <div className="space-y-2">
              <h1 className="text-3xl font-bold">
                <span className="bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-transparent">
                  꿀띱
                </span>
              </h1>
              <CardTitle className="text-xl font-semibold text-gray-900">
                사장님 로그인
              </CardTitle>
              <p className="text-gray-600">띱박스로 새로운 수익을 창출하세요</p>
            </div>
          </CardHeader>

          <CardContent className="space-y-6 px-8 pb-8">
            {/* Login Button */}
            <div className="flex w-full justify-center">
              <GoogleLoginButton />
            </div>

            {/* Features Preview */}
            <div className="space-y-3 rounded-lg bg-gradient-to-r from-amber-50 to-orange-50 p-4">
              <div className="flex items-center gap-2 text-sm font-medium text-amber-800">
                <Sparkles className="h-4 w-4" />
                로그인하고 시작하세요
              </div>
              <div className="space-y-2 text-xs text-gray-600">
                <div className="flex items-center gap-2">
                  <div className="h-1.5 w-1.5 rounded-full bg-amber-500"></div>
                  <span>남는 재료를 띱박스로 만들어 판매</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="h-1.5 w-1.5 rounded-full bg-orange-500"></div>
                  <span>실시간 매출 분석과 고객 관리</span>
                </div>
                <div className="flex items-center gap-2">
                  <div className="h-1.5 w-1.5 rounded-full bg-amber-500"></div>
                  <span>환경 보호에 동참하며 수익 창출</span>
                </div>
              </div>
            </div>

            {/* Info Text */}
            <div className="text-center text-sm text-gray-500">
              <p>Google 계정으로 로그인하여</p>
              <p>꿀띱 사장님 서비스를 이용하세요</p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Login;
