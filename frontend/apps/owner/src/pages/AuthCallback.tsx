import { useEffect } from 'react';
import { useAuthCallback } from '@/hooks/useAuthCallback';

const AuthCallback = () => {
  const { processCallback } = useAuthCallback();

  useEffect(() => {
    processCallback();
  }, [processCallback]);

  return (
    <div className="flex min-h-full items-center justify-center">
      <div className="flex flex-col items-center space-y-4">
        <div className="border-primary h-8 w-8 animate-spin rounded-full border-2 border-r-transparent" />
        <p className="text-muted-foreground text-sm">로그인 처리 중...</p>
      </div>
    </div>
  );
};

export default AuthCallback;
