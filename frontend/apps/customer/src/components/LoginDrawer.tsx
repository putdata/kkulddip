import GoogleLoginButton from '@/components/GoogleLoginButton';
import { Button } from '@/components/ui/button';
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from '@/components/ui/drawer';

const LoginDrawer = () => {
  return (
    <Drawer>
      <DrawerTrigger asChild>
        <Button variant="outline">로그인</Button>
      </DrawerTrigger>
      <DrawerContent>
        <div className="mx-auto w-full max-w-sm space-y-6">
          <DrawerHeader>
            <DrawerTitle className="text-lg font-semibold text-amber-400">
              로그인이 필요한 서비스입니다
            </DrawerTitle>
            <DrawerDescription className="text-sm text-gray-600">
              꿀띱과 함께 맛있는 발견을 시작해보세요!
            </DrawerDescription>
          </DrawerHeader>
          <div className="flex flex-col justify-center space-y-2 pb-6">
            <DrawerDescription className="text-center text-xs text-gray-600">
              로그인하고 우리 동네 띱박스 만나기
            </DrawerDescription>
            <GoogleLoginButton />
          </div>
        </div>
      </DrawerContent>
    </Drawer>
  );
};

export default LoginDrawer;
