import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Link } from 'react-router-dom';

export const containerClassName =
  'w-full h-screen flex items-center justify-center px-4';

/* TODO: 디자인 확정 필요 */

const SignupCard = () => {
  return (
    <Card className="min-w-sm mx-auto max-w-md">
      <CardHeader>
        <CardTitle className="text-lg text-amber-400">
          꿀띱에 오신 것을 환영해요
        </CardTitle>
        <CardDescription>
          계정을 만들기 위해 간단한 정보를 입력해주세요
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="grid gap-4">
          <div className="grid grid-cols-1 gap-4">
            <div className="grid gap-2">
              <Label htmlFor="username">닉네임</Label>
              <Input id="username" required />
            </div>
          </div>
          <div className="grid gap-2">
            <Label htmlFor="tel">전화번호</Label>
            <Input id="tel" type="tel" required />
          </div>

          <Button
            type="submit"
            className="border-1 mt-1.5 w-full border-gray-100 bg-amber-200 font-bold text-gray-600"
          >
            이대로 가입할게요
          </Button>
          <Button variant="outline" className="w-full">
            Google로 가입하기
          </Button>
        </div>
        <div className="mt-4 text-center text-sm">
          이미 계정이 있으신가요?
          <Link
            to={{
              pathname: '',
              search: '?query=string',
              hash: '#hash',
            }}
            className="underline"
          >
            로그인
          </Link>
        </div>
      </CardContent>
    </Card>
  );
};

export default SignupCard;
