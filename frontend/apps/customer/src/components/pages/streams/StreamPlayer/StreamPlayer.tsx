import { useEffect, useState, useRef } from 'react';
import { Loader2, AlertCircle, Users } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Badge } from '@/components/ui/badge';
import { useStreamViewer } from '@/hooks/useStreamViewer';
import type { StreamDetail } from '@/types/stream';

interface StreamPlayerProps {
  stream: StreamDetail;
  token: string;
}

const StreamPlayer = ({ stream, token }: StreamPlayerProps) => {
  const [hasConnected, setHasConnected] = useState(false);
  const connectionAttemptedRef = useRef(false);
  const currentTokenRef = useRef<string | null>(null);

  const {
    connectionStatus,
    error,
    isConnected,
    isConnecting,
    isError,
    isEnded,
    connectToStream,
    disconnectFromStream,
  } = useStreamViewer({
    onStreamConnected: () => {
      console.log('스트림에 연결되었습니다.');
    },
    onStreamEnded: () => {
      console.log('스트림이 종료되었습니다.');
    },
    onError: (error) => {
      console.error('스트림 에러:', error);
    },
  });

  const videoElementId = `stream-video-${stream.id}`;

  // 네트워크 상태 모니터링 (자동 재연결 제거)
  // useEffect(() => {
  //   const handleNetworkChange = (status: NetworkStatus) => {
  //     setNetworkStatus(status);
  //   };
  //   networkMonitor.addListener(handleNetworkChange);
  //   return () => {
  //     networkMonitor.removeListener(handleNetworkChange);
  //   };
  // }, []);

  useEffect(() => {
    // 강력한 중복 방지 로직
    const shouldConnect = 
      token && 
      stream.status === 'LIVE' && 
      !hasConnected &&
      !connectionAttemptedRef.current &&
      currentTokenRef.current !== token;

    if (shouldConnect) {
      connectionAttemptedRef.current = true;
      currentTokenRef.current = token;
      setHasConnected(true);
      
      console.log('[StreamPlayer] 단일 연결 시도 (중복 방지 적용):', { 
        token: token.substring(0, 50) + '...',
        streamStatus: stream.status,
        hasConnected,
        attempted: connectionAttemptedRef.current
      });
      
      connectToStream(token, videoElementId);
    }

    return () => {
      disconnectFromStream(false);
    };
  }, [token, stream.status, hasConnected]); // 함수 의존성 제거하여 중복 실행 방지

  // 컴포넌트 언마운트 시 연결 상태 초기화
  useEffect(() => {
    return () => {
      connectionAttemptedRef.current = false;
      currentTokenRef.current = null;
    };
  }, []);

  // 재시도 로직 제거됨
  // const handleRetry = () => {
  //   // 재시도 없음 - 사용자가 수동으로 페이지 새로고침 필요
  // };

  const formatViewerCount = (count: number) => {
    if (count >= 1000) {
      return `${(count / 1000).toFixed(1)}k`;
    }
    return count.toString();
  };

  if (stream.status !== 'LIVE') {
    return (
      <div className="aspect-video bg-gray-900 rounded-lg flex items-center justify-center">
        <div className="text-center text-white">
          <AlertCircle className="w-12 h-12 mx-auto mb-4 text-gray-400" />
          <h3 className="text-lg font-medium mb-2">스트림이 종료되었습니다</h3>
          <p className="text-sm text-gray-400">
            이 방송은 현재 진행되지 않습니다.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {/* 비디오 플레이어 영역 */}
      <div className="relative">
        <div 
          id={videoElementId}
          className="aspect-video bg-gray-900 rounded-lg overflow-hidden"
        >
          {isConnecting && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-900">
              <div className="text-center text-white">
                <Loader2 className="w-8 h-8 mx-auto mb-4 animate-spin" />
                <p className="text-sm">스트림에 연결 중...</p>
              </div>
            </div>
          )}
          
          {isError && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-900">
              <div className="text-center text-white space-y-4">
                <AlertCircle className="w-12 h-12 mx-auto text-red-400" />
                <div>
                  <h3 className="text-lg font-medium mb-2">연결 실패</h3>
                  <p className="text-sm text-gray-400 mb-4">
                    {error?.message || '스트림 연결에 실패했습니다.'}
                  </p>
                  <p className="text-xs text-gray-500">
                    페이지를 새로고침해 주세요.
                  </p>
                </div>
              </div>
            </div>
          )}
          
          {isEnded && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-900">
              <div className="text-center text-white">
                <AlertCircle className="w-12 h-12 mx-auto mb-4 text-gray-400" />
                <h3 className="text-lg font-medium mb-2">방송 종료</h3>
                <p className="text-sm text-gray-400">
                  스트리머가 방송을 종료했습니다.
                </p>
              </div>
            </div>
          )}
        </div>

        {/* 라이브 배지 및 시청자 수 */}
        {isConnected && (
          <div className="absolute top-4 left-4 flex items-center space-x-2">
            <Badge className="bg-red-500 hover:bg-red-600 text-white">
              🔴 LIVE
            </Badge>
            <Badge variant="secondary" className="bg-black/70 text-white border-none">
              <Users className="w-3 h-3 mr-1" />
              {formatViewerCount(stream.viewerCount)}
            </Badge>
          </div>
        )}
      </div>

      {/* 에러 알림 */}
      {isError && error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            {error.message}
            <span className="block mt-2 text-sm">
              페이지를 새로고침해 주세요.
            </span>
          </AlertDescription>
        </Alert>
      )}

      {/* 디버그 정보 제거됨 */}
    </div>
  );
};

export default StreamPlayer;