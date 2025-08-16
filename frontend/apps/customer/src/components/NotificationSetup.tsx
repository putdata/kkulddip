import { useState } from 'react';
import { useNotification } from '@/hooks/useNotification';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Bell, UserPlus } from 'lucide-react';
import { registerFCMToken } from '@/services/notificationService';

const NotificationSetup = () => {
  const { requestPermission, token } = useNotification();

  const [isLoading1, setIsLoading1] = useState(false);
  const [isLoading2, setIsLoading2] = useState(false);

  const handleRequestPermission = async () => {
    setIsLoading1(true);
    try {
      await requestPermission();
    } finally {
      setIsLoading1(false);
    }
  };

  const handleRegisterToken = async () => {
    if (!token) {
      return;
    }
    setIsLoading2(true);
    try {
      await registerFCMToken(token);
    } finally {
      setIsLoading2(false);
    }
  };


  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Bell className="h-5 w-5" />
          알림 설정
        </CardTitle>
        <CardDescription>할인 알림을 받기 위해 알림을 설정하세요.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-3">
        <Button
          onClick={handleRequestPermission}
          disabled={isLoading1}
          className="w-full"
        >
          <Bell className="mr-2 h-4 w-4" />
          {isLoading1 ? '요청 중...' : '1. 알림 허용하기'}
        </Button>

        <Button
          onClick={handleRegisterToken}
          disabled={isLoading2 || !token}
          className="w-full"
          variant="outline"
        >
          <UserPlus className="mr-2 h-4 w-4" />
          {isLoading2 ? '등록 중...' : '2. 할인 알림 받기'}
        </Button>

        {token && (
          <details className="mt-4">
            <summary className="cursor-pointer text-xs text-gray-500">
              FCM 토큰 (개발용)
            </summary>
            <p className="mt-1 break-all font-mono text-xs text-gray-600">
              {token}
            </p>
          </details>
        )}
      </CardContent>
    </Card>
  );
};

export default NotificationSetup;