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
import { Bell, Send, UserPlus } from 'lucide-react';
import {
  sendTestNotification,
  registerFCMToken,
} from '@/services/notificationService';

const NotificationSetup = () => {
  const { requestPermission, token } = useNotification();

  const [isLoading1, setIsLoading1] = useState(false);
  const [isLoading2, setIsLoading2] = useState(false);
  const [isLoading3, setIsLoading3] = useState(false);

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

  const handleTestNotification = async () => {
    setIsLoading3(true);
    try {
      await sendTestNotification();
    } finally {
      setIsLoading3(false);
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Bell className="h-5 w-5" />
          알림 테스트
        </CardTitle>
        <CardDescription>3단계로 알림 기능을 테스트해보세요.</CardDescription>
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
          {isLoading2 ? '등록 중...' : '2. FCM 토큰 서버에 등록'}
        </Button>

        <Button
          onClick={handleTestNotification}
          disabled={isLoading3}
          className="w-full"
          variant="outline"
        >
          <Send className="mr-2 h-4 w-4" />
          {isLoading3 ? '발송 중...' : '3. 테스트 알림 발송'}
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
