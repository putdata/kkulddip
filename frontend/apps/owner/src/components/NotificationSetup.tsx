import { useState } from 'react';
import { useNotification } from '@/hooks/useNotification';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
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

  const [testData, setTestData] = useState({
    title: '알림 테스트',
    content: '테스트 알림 내용',
    publisherId: 1001,
    publisherType: 'SYSTEM',
    subscriberId: 5,
    subscriberType: 'OWNER',
    notificationType: 'ORDER',
    actionUrl: '/orders/12345',
  });

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
      await sendTestNotification(testData);
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

        <div className="space-y-4 border-t pt-4">
          <h3 className="text-sm font-medium">알림 테스트 설정</h3>
          <div className="grid grid-cols-2 gap-2">
            <div>
              <label htmlFor="title" className="text-xs text-gray-600">
                제목
              </label>
              <Input
                id="title"
                value={testData.title}
                onChange={e =>
                  setTestData(prev => ({ ...prev, title: e.target.value }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label htmlFor="content" className="text-xs text-gray-600">
                내용
              </label>
              <Input
                id="content"
                value={testData.content}
                onChange={e =>
                  setTestData(prev => ({ ...prev, content: e.target.value }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label htmlFor="publisherId" className="text-xs text-gray-600">
                발신자 ID
              </label>
              <Input
                id="publisherId"
                type="number"
                value={testData.publisherId}
                onChange={e =>
                  setTestData(prev => ({
                    ...prev,
                    publisherId: parseInt(e.target.value) || 0,
                  }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label htmlFor="publisherType" className="text-xs text-gray-600">
                발신자 타입
              </label>
              <Input
                id="publisherType"
                value={testData.publisherType}
                onChange={e =>
                  setTestData(prev => ({
                    ...prev,
                    publisherType: e.target.value,
                  }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label htmlFor="subscriberId" className="text-xs text-gray-600">
                수신자 ID
              </label>
              <Input
                id="subscriberId"
                type="number"
                value={testData.subscriberId}
                onChange={e =>
                  setTestData(prev => ({
                    ...prev,
                    subscriberId: parseInt(e.target.value) || 0,
                  }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label htmlFor="subscriberType" className="text-xs text-gray-600">
                수신자 타입
              </label>
              <Input
                id="subscriberType"
                value={testData.subscriberType}
                onChange={e =>
                  setTestData(prev => ({
                    ...prev,
                    subscriberType: e.target.value,
                  }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label
                htmlFor="notificationType"
                className="text-xs text-gray-600"
              >
                알림 타입
              </label>
              <Input
                id="notificationType"
                value={testData.notificationType}
                onChange={e =>
                  setTestData(prev => ({
                    ...prev,
                    notificationType: e.target.value,
                  }))
                }
                className="h-8"
              />
            </div>
            <div>
              <label htmlFor="actionUrl" className="text-xs text-gray-600">
                액션 URL
              </label>
              <Input
                id="actionUrl"
                value={testData.actionUrl}
                onChange={e =>
                  setTestData(prev => ({ ...prev, actionUrl: e.target.value }))
                }
                className="h-8"
              />
            </div>
          </div>
        </div>

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
