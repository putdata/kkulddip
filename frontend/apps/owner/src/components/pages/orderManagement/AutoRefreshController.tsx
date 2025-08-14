import { useState, useEffect, useCallback } from 'react';
import { Button } from '@/components/ui/button';
import { Clock, RefreshCw } from 'lucide-react';
import { toast } from 'sonner';

interface AutoRefreshControllerProps {
  onRefresh: () => void;
  intervalMs: number;
  isEnabled: boolean;
  onToggle: (enabled: boolean) => void;
  showCountdown?: boolean;
}

const AutoRefreshController = ({
  onRefresh,
  intervalMs,
  isEnabled,
  onToggle,
  showCountdown = false,
}: AutoRefreshControllerProps) => {
  const [countdown, setCountdown] = useState(intervalMs / 1000);

  // 자동 새로고침 로직
  useEffect(() => {
    if (!isEnabled) {
      setCountdown(intervalMs / 1000);
      return;
    }

    const interval = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          onRefresh();
          return intervalMs / 1000;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(interval);
  }, [isEnabled, intervalMs, onRefresh]);

  // 수동 새로고침 핸들러
  const handleManualRefresh = useCallback(() => {
    onRefresh();
    setCountdown(intervalMs / 1000);
    toast.success('새로고침했습니다.');
  }, [onRefresh, intervalMs]);

  // 자동 새로고침 토글 핸들러
  const handleToggle = useCallback(() => {
    onToggle(!isEnabled);
    if (!isEnabled) {
      setCountdown(intervalMs / 1000);
    }
  }, [isEnabled, onToggle, intervalMs]);

  return (
    <div className="flex items-center gap-2">
      <Button
        variant="outline"
        size="icon"
        onClick={handleManualRefresh}
        title="새로고침"
      >
        <RefreshCw className="h-4 w-4" />
      </Button>

      <Button
        variant={isEnabled ? 'default' : 'outline'}
        size="sm"
        onClick={handleToggle}
        className="relative"
      >
        <Clock className="mr-2 h-4 w-4" />
        <span>{isEnabled ? '자동 새로고침 ON' : '자동 새로고침 OFF'}</span>
        {isEnabled && showCountdown && (
          <span className="ml-2 text-xs opacity-75">({countdown}s)</span>
        )}
      </Button>
    </div>
  );
};

export default AutoRefreshController;
