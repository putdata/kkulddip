import { useState, useEffect } from 'react';
import { WifiOff, Wifi } from 'lucide-react';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useOnlineStatus } from '@/hooks/useOnlineStatus';

const OfflineIndicator = () => {
  const { isOnline, isOffline, wasOffline } = useOnlineStatus();
  const [showRecoveryAlert, setShowRecoveryAlert] = useState(false);

  // 온라인 복구 시 잠시 알림 표시 후 숨김
  useEffect(() => {
    if (isOnline && wasOffline) {
      setShowRecoveryAlert(true);
      // 다음 렌더 사이클에서 숨김
      const hideAlert = () => setShowRecoveryAlert(false);
      requestAnimationFrame(hideAlert);
    }
  }, [isOnline, wasOffline]);

  // 온라인 복구 알림 (잠시만 표시)
  if (showRecoveryAlert) {
    return (
      <Alert className="top-safe-or-4 fixed left-4 right-4 z-50 border-green-200 bg-green-50 shadow-lg md:left-auto md:right-4 md:w-80 lg:w-96">
        <Wifi className="h-4 w-4 text-green-600" />
        <AlertDescription className="text-sm font-medium text-green-800">
          인터넷 연결이 복구되었습니다.
        </AlertDescription>
      </Alert>
    );
  }

  // 오프라인 상태 알림
  if (isOffline) {
    return (
      <Alert
        variant="destructive"
        className="top-safe-or-4 fixed left-4 right-4 z-50 shadow-lg md:left-auto md:right-4 md:w-80 lg:w-96"
      >
        <WifiOff className="h-4 w-4" />
        <AlertDescription className="text-sm font-medium">
          인터넷 연결이 끊어졌습니다. 일부 기능이 제한될 수 있습니다.
        </AlertDescription>
      </Alert>
    );
  }

  return null;
};

export default OfflineIndicator;
