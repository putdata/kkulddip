import { useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { RefreshCw, Wifi, WifiOff } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { StreamCard } from '@/components/pages/streams/StreamCard';
import { useStreamList } from '@/hooks/useStreamList';
import { ROUTE_PATH } from '@/router/route-path';
import { debugInfo, useDebugLifecycle } from '@/utils/debug';

const Streams = () => {
  const navigate = useNavigate();
  const { data: streams, isLoading, error, refetch, isRefetching } = useStreamList();

  // 디버그 라이프사이클
  useEffect(() => {
    const cleanup = useDebugLifecycle('Streams');
    
    // 전체 진단 실행
    debugInfo.fullDiagnostic();
    debugInfo.measurePageLoad();
    
    return cleanup;
  }, []);

  console.log('[Streams] 컴포넌트 렌더링:', {
    timestamp: new Date().toISOString(),
    pathname: window.location.pathname,
    isLoading,
    isRefetching,
    hasError: !!error,
    errorMessage: error instanceof Error ? error.message : error,
    streamsCount: streams?.length || 0,
  });


  const handleStreamClick = (streamId: number) => {
    navigate(ROUTE_PATH.STREAM_DETAIL.replace(':streamId', streamId.toString()));
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
            <div className="grid grid-cols-2 gap-4">
              {streams.map((stream) => (
                <StreamCard
                  key={stream.id}
                  stream={stream}
                  onClick={() => handleStreamClick(stream.id)}
                />
              ))}
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