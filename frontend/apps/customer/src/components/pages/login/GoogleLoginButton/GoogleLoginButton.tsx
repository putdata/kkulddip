import { Button } from '@/components/ui/button';
import { useGoogleLogin } from '@/hooks/useGoogleLogin';

const GoogleLoginButton = () => {
  const { loginWithGoogle } = useGoogleLogin();

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
