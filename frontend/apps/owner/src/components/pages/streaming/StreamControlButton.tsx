import { Button } from '@/components/ui/button';
import type { StreamFlowStatus } from '@/types/stream';
import {
  Radio,
  StopCircle,
  Loader2,
  CheckCircle,
  Settings,
} from 'lucide-react';

interface StreamControlButtonProps {
  status: StreamFlowStatus;
  isLoading?: boolean;
  onConnect?: () => void;
  onStartStreaming?: () => void;
  onEndStreaming?: () => void;
}

const StreamControlButton = ({
  status,
  isLoading = false,
  onConnect,
  onStartStreaming,
  onEndStreaming,
}: StreamControlButtonProps) => {
  const getButtonProps = () => {
    switch (status) {
      case 'READY':
        return {
          onClick: onConnect,
          disabled: isLoading,
          variant: 'secondary' as const,
          children: (
            <>
              {isLoading ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Settings className="h-4 w-4" />
              )}
              {isLoading ? '연결 중...' : '화면 점검'}
            </>
          ),
        };

      case 'CONNECTED':
        return {
          onClick: onStartStreaming,
          disabled: isLoading,
          variant: 'default' as const,
          children: (
            <>
              {isLoading ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Radio className="h-4 w-4" />
              )}
              {isLoading ? '시작 중...' : '라이브 시작'}
            </>
          ),
        };

      case 'LIVE':
        return {
          onClick: onEndStreaming,
          disabled: isLoading,
          variant: 'destructive' as const,
          children: (
            <>
              {isLoading ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <StopCircle className="h-4 w-4" />
              )}
              {isLoading ? '종료 중...' : '방송 종료'}
            </>
          ),
        };

      case 'CONNECTING':
        return {
          disabled: true,
          variant: 'secondary' as const,
          children: (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              연결 중...
            </>
          ),
        };

      case 'PUBLISHING':
        return {
          disabled: true,
          variant: 'default' as const,
          children: (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              시작 중...
            </>
          ),
        };

      case 'ENDING':
        return {
          disabled: true,
          variant: 'destructive' as const,
          children: (
            <>
              <Loader2 className="h-4 w-4 animate-spin" />
              종료 중...
            </>
          ),
        };

      case 'ENDED':
        return {
          disabled: true,
          variant: 'default' as const,
          children: (
            <>
              <CheckCircle className="h-4 w-4" />
              방송 종료됨
            </>
          ),
        };

      case 'ERROR':
        return {
          disabled: true,
          variant: 'destructive' as const,
          children: (
            <>
              <CheckCircle className="h-4 w-4" />
              오류 발생
            </>
          ),
        };

      default:
        return {
          disabled: true,
          variant: 'default' as const,
          children: (
            <>
              <CheckCircle className="h-4 w-4" />
              대기 중
            </>
          ),
        };
    }
  };

  const buttonProps = getButtonProps();

  return (
    <Button
      variant={buttonProps.variant}
      size="lg"
      className="min-w-[120px] gap-2"
      onClick={buttonProps.onClick}
      disabled={buttonProps.disabled}
    >
      {buttonProps.children}
    </Button>
  );
};

export default StreamControlButton;
