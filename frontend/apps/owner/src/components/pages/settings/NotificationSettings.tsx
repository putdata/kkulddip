import { useState, useEffect } from 'react';
import { Bell, BellOff, AlertCircle, CheckCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Switch } from '@/components/ui/switch';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useNotification } from '@/hooks/useNotification';
import {
  registerFCMToken,
  deactivateFCMToken,
} from '@/services/notificationService';
import { useNotificationStore, useUserStore } from 'common';

const NotificationSettings = () => {
  const { token, requestPermission } = useNotification();
  const { isTokenRegistered, enableNotifications, disableNotifications } =
    useNotificationStore();
  const { user } = useUserStore();
  const [isLoading, setIsLoading] = useState(false);
  const [permissionStatus, setPermissionStatus] =
    useState<NotificationPermission>('default');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    setPermissionStatus(Notification.permission);
  }, []);

  const handleEnableNotifications = async () => {
    setIsLoading(true);
    setError(null);
    setSuccess(null);

    try {
      // 이미 권한이 있고 토큰이 있으면 바로 등록
      if (permissionStatus === 'granted' && token) {
        await registerFCMToken(token);
        enableNotifications();
        setSuccess('알림이 성공적으로 활성화되었습니다!');
        return;
      }

      // 권한이 없으면 요청
      const granted = await requestPermission();

      if (granted && token) {
        await registerFCMToken(token);
        enableNotifications();
        setPermissionStatus('granted');
        setSuccess('알림이 성공적으로 활성화되었습니다!');
      } else {
        setError('알림 권한이 거부되었거나 토큰 발급에 실패했습니다.');
      }
    } catch (err) {
      console.error('Notification setup failed:', err);
      setError('알림 설정 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDisableNotifications = async () => {
    if (!user?.userId) {
      setError('사용자 정보를 찾을 수 없습니다.');
      return;
    }

    setIsLoading(true);
    setError(null);
    setSuccess(null);

    try {
      await deactivateFCMToken(user.userId);
      disableNotifications();
      setSuccess('알림이 비활성화되었습니다.');
    } catch (err) {
      console.error('FCM token deactivation failed:', err);
      setError('알림 비활성화 중 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const getPermissionStatusText = () => {
    switch (permissionStatus) {
      case 'granted':
        return '허용됨';
      case 'denied':
        return '거부됨';
      default:
        return '미설정';
    }
  };

  const getPermissionStatusColor = () => {
    switch (permissionStatus) {
      case 'granted':
        return 'text-green-600';
      case 'denied':
        return 'text-red-600';
      default:
        return 'text-yellow-600';
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Bell className="h-5 w-5" />
          푸시 알림 설정
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* 권한 상태 표시 */}
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-medium">알림 권한 상태</h3>
            <p className="text-muted-foreground text-sm">
              브라우저 알림 권한 상태입니다
            </p>
          </div>
          <span className={`font-medium ${getPermissionStatusColor()}`}>
            {getPermissionStatusText()}
          </span>
        </div>

        {/* 알림 토글 */}
        <div className="flex items-center justify-between">
          <div>
            <h3 className="font-medium">푸시 알림 수신</h3>
            <p className="text-muted-foreground text-sm">
              주문, 리뷰 등 중요한 알림을 받아보세요
            </p>
          </div>
          <Switch
            checked={isTokenRegistered}
            onCheckedChange={checked => {
              if (checked) {
                handleEnableNotifications();
              } else {
                handleDisableNotifications();
              }
            }}
            disabled={isLoading}
          />
        </div>

        {/* 에러/성공 메시지 */}
        {error && (
          <Alert variant="destructive">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {success && (
          <Alert>
            <CheckCircle className="h-4 w-4" />
            <AlertDescription>{success}</AlertDescription>
          </Alert>
        )}

        {/* 권한이 거부된 경우 안내 */}
        {permissionStatus === 'denied' && (
          <Alert>
            <BellOff className="h-4 w-4" />
            <AlertDescription>
              알림 권한이 차단되었습니다. 브라우저 설정에서 알림을 허용해주세요.
              <br />
              <strong>Chrome:</strong> 주소창 왼쪽 자물쇠 아이콘 → 알림 허용
              <br />
              <strong>Safari:</strong> 환경설정 → 웹사이트 → 알림
            </AlertDescription>
          </Alert>
        )}

        {/* 권한이 허용되지 않은 경우 활성화 버튼 */}
        {permissionStatus !== 'granted' && (
          <Button
            onClick={handleEnableNotifications}
            disabled={isLoading || permissionStatus === 'denied'}
            className="w-full"
          >
            <Bell className="mr-2 h-4 w-4" />
            {isLoading ? '설정 중...' : '알림 허용하기'}
          </Button>
        )}

        {/* FCM 토큰 정보 (개발용) */}
        {token && process.env.NODE_ENV === 'development' && (
          <div className="border-t pt-4">
            <h4 className="text-muted-foreground text-sm font-medium">
              FCM 토큰 (개발용)
            </h4>
            <p className="text-muted-foreground mt-1 break-all text-xs">
              {token}
            </p>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

export default NotificationSettings;
