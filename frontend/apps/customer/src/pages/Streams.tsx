import { useNavigate } from 'react-router-dom';
import { useState } from 'react';
import { RefreshCw, Wifi, WifiOff, Loader2, AlertCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { StreamCard } from '@/components/pages/streams/StreamCard';
import { useStreamList } from '@/hooks/useStreamList';
import { useStreamConnection } from '@/hooks/useStreamConnection';
import { ROUTE_PATH } from '@/router/route-path';
import { toast } from 'sonner';
import type { JoinStreamResponse, StreamPlayerError } from '@/types/stream';

const Streams = () => {
  const navigate = useNavigate();
  const { data: streams, isLoading, error, refetch, isRefetching } = useStreamList();
  const [connectingStreamId, setConnectingStreamId] = useState<number | null>(null);
  
  const {
    error: connectionError,
    connectToStream,
    resetConnection,
    isConnecting,
  } = useStreamConnection({
    onSuccess: (data: JoinStreamResponse, streamId: number) => {
      // 연결 성공 시 StreamDetail 페이지로 이동 (토큰과 함께)
      if (streamId && data.token) {
        navigate(ROUTE_PATH.STREAM_DETAIL.replace(':streamId', streamId.toString()), {
          state: {
            token: data.token,
            sessionId: data.sessionId,
            preValidated: true, // 사전 검증 완료 표시
          },
        });
      }
      setConnectingStreamId(null);
    },
    onError: (error: StreamPlayerError) => {
      toast.error(error.message);
      setConnectingStreamId(null);
    },
  });


  const handleStreamClick = async (streamId: number) => {
    // 유효성 검증
    if (!streamId || isNaN(streamId)) {
      toast.error('잘못된 스트림 ID입니다.');
      return;
    }

    if (isConnecting || connectingStreamId) {
      return; // 이미 연결 중인 경우 무시
    }

    try {
      setConnectingStreamId(streamId);
      resetConnection();
      
      // 토큰 요청 및 연결 검증 시작
      await connectToStream(streamId);
    } catch (error) {
      console.error('스트림 연결 시도 중 에러:', error);
      setConnectingStreamId(null);
      toast.error('스트림 연결에 실패했습니다.');
    }
  };

  const handleRefresh = () => {
    refetch();
  };

  if (isLoading) {
    return (
      <div className="flex flex-col h-full">
        {/* 헤더 */}
        <div className="flex items-center justify-between p-4 border-b bg-white">
          <h1 className="text-lg font-bold">라이브 스트림</h1>
          <Button variant="ghost" size="icon" disabled>
            <RefreshCw className="w-4 h-4" />
          </Button>
        </div>

        {/* 로딩 스켈레톤 */}
        <div className="flex-1 p-4">
          <div className="grid grid-cols-2 gap-4">
            {Array.from({ length: 6 }).map((_, index) => (
              <div key={index} className="animate-pulse">
                <div className="aspect-video bg-gray-200 rounded-lg mb-2"></div>
                <div className="h-4 bg-gray-200 rounded mb-1"></div>
                <div className="h-3 bg-gray-200 rounded w-3/4"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col h-full">
        {/* 헤더 */}
        <div className="flex items-center justify-between p-4 border-b bg-white">
          <h1 className="text-lg font-bold">라이브 스트림</h1>
          <Button 
            variant="ghost" 
            size="icon" 
            onClick={handleRefresh}
            disabled={isRefetching}
          >
            <RefreshCw className={`w-4 h-4 ${isRefetching ? 'animate-spin' : ''}`} />
          </Button>
        </div>

        {/* 에러 상태 */}
        <div className="flex-1 flex items-center justify-center p-4">
          <div className="text-center space-y-4">
            <WifiOff className="w-12 h-12 mx-auto text-gray-400" />
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                연결 오류
              </h3>
              <p className="text-sm text-gray-500 mb-4">
                스트림 목록을 불러올 수 없습니다.
              </p>
              <Button onClick={handleRefresh} disabled={isRefetching}>
                {isRefetching ? (
                  <>
                    <RefreshCw className="w-4 h-4 mr-2 animate-spin" />
                    재시도 중...
                  </>
                ) : (
                  <>
                    <RefreshCw className="w-4 h-4 mr-2" />
                    다시 시도
                  </>
                )}
              </Button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full">
      {/* 헤더 */}
      <div className="flex items-center justify-between p-4 border-b bg-white">
        <div>
          <h1 className="text-lg font-bold">라이브 스트림</h1>
          {streams && streams.length > 0 && (
            <p className="text-sm text-gray-500">
              {streams.length}개의 라이브 방송
            </p>
          )}
        </div>
        <Button 
          variant="ghost" 
          size="icon" 
          onClick={handleRefresh}
          disabled={isRefetching}
        >
          <RefreshCw className={`w-4 h-4 ${isRefetching ? 'animate-spin' : ''}`} />
        </Button>
      </div>

      {/* 스트림 목록 */}
      <div className="flex-1 overflow-y-auto">
        {streams && streams.length > 0 ? (
          <div className="p-4">
            <div className="space-y-4">
              {/* 연결 상태 알림 */}
              {isConnecting && connectingStreamId && (
                <Alert>
                  <Loader2 className="h-4 w-4 animate-spin" />
                  <AlertDescription>
                    스트림 연결을 확인하고 있습니다. 잠시만 기다려주세요...
                  </AlertDescription>
                </Alert>
              )}
              
              {connectionError && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription className="flex items-center justify-between">
                    <span>{connectionError.message}</span>
                    <Button 
                      size="sm" 
                      variant="outline"
                      onClick={() => {
                        resetConnection();
                        setConnectingStreamId(null);
                      }}
                    >
                      확인
                    </Button>
                  </AlertDescription>
                </Alert>
              )}
              
              <div className="grid grid-cols-2 gap-4">
                {streams.map((stream) => (
                  <StreamCard
                    key={stream.id}
                    stream={stream}
                    onClick={() => handleStreamClick(stream.id)}
                    disabled={isConnecting}
                    isConnecting={connectingStreamId === stream.id}
                  />
                ))}
              </div>
            </div>
          </div>
        ) : (
          /* 빈 상태 */
          <div className="flex-1 flex items-center justify-center p-4">
            <div className="text-center space-y-4">
              <Wifi className="w-12 h-12 mx-auto text-gray-400" />
              <div>
                <h3 className="text-lg font-medium text-gray-900 mb-2">
                  라이브 스트림이 없습니다
                </h3>
                <p className="text-sm text-gray-500 mb-4">
                  현재 진행 중인 라이브 방송이 없습니다.
                </p>
                <Button onClick={handleRefresh} variant="outline">
                  <RefreshCw className="w-4 h-4 mr-2" />
                  새로고침
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Streams;