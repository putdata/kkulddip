import { Button } from '@/components/ui/button';
import type { StreamFlowStatus } from '@/types/stream';
import { getStreamActionText } from '@/utils/streamUtils';
import { 
  Play, 
  Radio, 
  StopCircle, 
  Loader2,
  CheckCircle,
  Settings 
} from 'lucide-react';

interface StreamFlowControlsProps {
  status: StreamFlowStatus;
  isLoading: boolean;
  canConnect: boolean;
  canStartStreaming: boolean;
  canEndStreaming: boolean;
  onConnect: () => void;
  onStartStreaming: () => void;
  onEndStreaming: () => void;
  className?: string;
}

const getActionIcon = (status: StreamFlowStatus, isLoading: boolean) => {
  const iconProps = { className: "h-4 w-4" };
  
  if (isLoading) {
    return <Loader2 {...iconProps} className="animate-spin" />;
  }

  switch (status) {
    case 'READY':
      return <Settings {...iconProps} />;
    case 'CONNECTED':
      return <Radio {...iconProps} />;
    case 'LIVE':
      return <StopCircle {...iconProps} />;
    default:
      return <Play {...iconProps} />;
  }
};

const getButtonVariant = (status: StreamFlowStatus) => {
  switch (status) {
    case 'LIVE':
      return 'destructive' as const;
    case 'CONNECTED':
      return 'default' as const;
    case 'READY':
      return 'secondary' as const;
    default:
      return 'default' as const;
  }
};

export const StreamFlowControls = ({
  status,
  isLoading,
  canConnect,
  canStartStreaming,
  canEndStreaming,
  onConnect,
  onStartStreaming,
  onEndStreaming,
  className,
}: StreamFlowControlsProps) => {
  const actionText = getStreamActionText(status);
  const actionIcon = getActionIcon(status, isLoading);
  const buttonVariant = getButtonVariant(status);

  const getButtonProps = () => {
    if (canConnect) {
      return {
        onClick: onConnect,
        disabled: isLoading,
        children: (
          <>
            {actionIcon}
            {isLoading ? '연결 중...' : actionText}
          </>
        ),
      };
    }

    if (canStartStreaming) {
      return {
        onClick: onStartStreaming,
        disabled: isLoading,
        children: (
          <>
            {actionIcon}
            {isLoading ? '시작 중...' : actionText}
          </>
        ),
      };
    }

    if (canEndStreaming) {
      return {
        onClick: onEndStreaming,
        disabled: isLoading,
        children: (
          <>
            {actionIcon}
            {isLoading ? '종료 중...' : actionText}
          </>
        ),
      };
    }

    return {
      disabled: true,
      children: (
        <>
          <CheckCircle className="h-4 w-4" />
          {status === 'ENDED' ? '방송 종료됨' : '대기 중'}
        </>
      ),
    };
  };

  const buttonProps = getButtonProps();

  if (!actionText && status !== 'ENDED') {
    return null;
  }

  return (
    <div className={className}>
      <Button
        variant={buttonVariant}
        size="lg"
        className="gap-2 min-w-[120px]"
        {...buttonProps}
      />
      
      {status === 'READY' && (
        <p className="text-xs text-muted-foreground mt-2 text-center">
          카메라와 마이크 권한을 확인해주세요
        </p>
      )}
      
      {status === 'CONNECTING' && (
        <p className="text-xs text-muted-foreground mt-2 text-center">
          스트림 세션에 연결하는 중입니다...
        </p>
      )}
      
      {status === 'CONNECTED' && (
        <p className="text-xs text-muted-foreground mt-2 text-center">
          모든 준비가 완료되었습니다
        </p>
      )}
      
      {status === 'LIVE' && (
        <p className="text-xs text-red-500 mt-2 text-center font-medium">
          🔴 라이브 방송 중
        </p>
      )}
    </div>
  );
};