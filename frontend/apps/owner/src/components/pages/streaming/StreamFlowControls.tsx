import type { StreamFlowStatus } from '@/types/stream';
import StreamControlButton from './StreamControlButton';

interface StreamFlowControlsProps {
  status: StreamFlowStatus;
  isLoading?: boolean;
  onConnect?: () => void;
  onStartStreaming?: () => void;
  onEndStreaming?: () => void;
  className?: string;
}

const StreamFlowControls = ({
  status,
  isLoading,
  onConnect,
  onStartStreaming,
  onEndStreaming,
  className,
}: StreamFlowControlsProps) => {
  return (
    <div className={className}>
      <StreamControlButton
        status={status}
        isLoading={isLoading}
        onConnect={onConnect}
        onStartStreaming={onStartStreaming}
        onEndStreaming={onEndStreaming}
      />

      {status === 'READY' && (
        <p className="text-muted-foreground mt-2 text-center text-xs">
          카메라와 마이크 권한을 확인해주세요
        </p>
      )}

      {status === 'CONNECTING' && (
        <p className="text-muted-foreground mt-2 text-center text-xs">
          스트림 세션에 연결하는 중입니다...
        </p>
      )}

      {status === 'CONNECTED' && (
        <p className="text-muted-foreground mt-2 text-center text-xs">
          모든 준비가 완료되었습니다
        </p>
      )}

      {status === 'LIVE' && (
        <p className="mt-2 text-center text-xs font-medium text-red-500">
          🔴 라이브 방송 중
        </p>
      )}
    </div>
  );
};

export default StreamFlowControls;
