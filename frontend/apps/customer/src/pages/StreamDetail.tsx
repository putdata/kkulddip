import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, AlertCircle, Loader2, RotateCcw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { StreamPlayer, StreamInfo } from '@/components/pages/streams/StreamPlayer';
import { StreamService } from '@/services/streamService';
import { toast } from 'sonner';
import { openviduDiagnostic, extractServerUrlFromToken } from '@/utils/openviduDiagnostic';

const StreamDetail = () => {
  const { streamId } = useParams<{ streamId: string }>();
  const navigate = useNavigate();
  const [token, setToken] = useState<string | null>(null);
  const [joinError, setJoinError] = useState<string | null>(null);
  const [isJoining, setIsJoining] = useState(false); // 중복 요청 방지용

  const numericStreamId = streamId ? parseInt(streamId, 10) : null;

  // 스트림 정보 조회 (실시간 상태 업데이트)
  const {
    data: stream,
    isLoading: isStreamLoading,
    error: streamError,
    refetch: refetchStream,
  } = useQuery({
    queryKey: ['stream', numericStreamId],
    queryFn: () => StreamService.getStreamDetail(numericStreamId!),
    enabled: !!numericStreamId,
    retry: 3,
    retryDelay: 1000,
    refetchInterval: (data) => {
      // 스트림이 LIVE 상태이고 토큰이 없으면 5초마다 상태 확인
      if (data?.status === 'LIVE' && !token) {
        return 5000;
      }
      // 스트림이 READY 상태이면 3초마다 LIVE 전환 확인
      if (data?.status === 'READY') {
        return 3000;
      }
      // 그 외에는 30초마다 확인
      return 30000;
    },
    refetchIntervalInBackground: false,
  });

  // TODO: Remove debug log
  // console.log('[StreamDetail] 컴포넌트 렌더링 시작:', {
  //   streamId,
  //   numericStreamId,
  //   hasToken: !!token,
  //   joinError,
  //   pathname: window.location.pathname,
  //   streamStatus: stream?.status,
  //   streamTitle: stream?.title,
  //   isJoining,
  //   timestamp: new Date().toISOString()
  // });

  // 스트림 참가 (토큰 획득)
  const joinStream = async () => {
    if (!numericStreamId) {
      console.error('[StreamDetail] joinStream 호출되었지만 numericStreamId가 없음');
      return;
    }

    if (isJoining) {
      console.warn('[StreamDetail] 이미 스트림 참가 요청 진행 중 - 중복 요청 차단');
      return;
    }

    // TODO: Remove debug log
    // console.log('[StreamDetail] joinStream 시작:', { numericStreamId });

    try {
      setIsJoining(true);
      setJoinError(null);
      const response = await StreamService.joinStream(numericStreamId);
      
      // TODO: Remove debug log (keep basic success info for now)
      // console.log('[StreamDetail] joinStream 성공:', {
      //   hasToken: !!response.token,
      //   sessionId: response.sessionId,
      //   tokenLength: response.token?.length || 0,
      //   fullResponse: response
      // });
      
      // TODO: Remove OpenVidu diagnostic (development only)
      // if (import.meta.env.DEV) {
      //   const serverUrl = extractServerUrlFromToken(response.token);
      //   if (serverUrl) {
      //     openviduDiagnostic.fullDiagnostic(response.token);
      //   }
      // }
      
      setToken(response.token);
    } catch (error) {
      const errorMessage = error instanceof Error 
        ? error.message 
        : '스트림 참가에 실패했습니다.';
      
      // TODO: Keep error logging but reduce verbosity
      console.error('[StreamDetail] joinStream 실패:', errorMessage);
      
      // 400 에러 (스트림 진행 중 아님) 또는 401 에러 (방송 미시작)인 경우 자동 재시도
      if (errorMessage.includes('진행 중이 아닙니다') || errorMessage.includes('사장님이 아직 방송을')) {
        // TODO: Remove debug log
        // console.log('[StreamDetail] 400 에러 감지 - 스트림 상태 새로고침 후 재시도 예약');
        
        // 스트림 상태 즉시 새로고침
        refetchStream();
        
        // 3초 후 자동 재시도
        setTimeout(() => {
          // TODO: Remove debug log
          // console.log('[StreamDetail] 400 에러 후 자동 재시도 시작');
          if (!token && !isJoining) { // 아직 토큰이 없고 진행 중이 아니면
            setJoinError(null); // 에러 초기화
            // 다음 useEffect 트리거를 위해 상태는 그대로 둠
          }
        }, 3000);
        
        setJoinError(`${errorMessage} (3초 후 자동 재시도)`);
      } else {
        setJoinError(errorMessage);
        toast.error(errorMessage);
      }
    } finally {
      setIsJoining(false); // 요청 완료 후 상태 초기화
    }
  };

  // 스트림 정보 로드 완료 후 자동으로 참가 시도 (중복 요청 방지)
  useEffect(() => {
    // TODO: Remove debug log
    // console.log('[StreamDetail] useEffect 트리거 - 스트림 참가 조건 검사:', {
    //   hasStream: !!stream,
    //   streamStatus: stream?.status,
    //   hasToken: !!token,
    //   hasJoinError: !!joinError,
    //   isJoining,
    //   shouldJoin: stream && stream.status === 'LIVE' && !token && !joinError && !isJoining
    // });

    if (stream && stream.status === 'LIVE' && !token && !joinError && !isJoining) {
      // TODO: Remove debug log
      // console.log('[StreamDetail] 자동 스트림 참가 조건 충족 - joinStream 호출');
      
      // 추가 안전장치: 스트림 상태 재확인
      if (stream.sessionId) {
        // TODO: Remove debug log
        // console.log('[StreamDetail] 스트림 sessionId 존재 - 참가 가능:', stream.sessionId);
        joinStream();
      } else {
        // TODO: Remove debug log
        // console.warn('[StreamDetail] 스트림 sessionId 없음 - 실제로 LIVE 상태가 아닐 수 있음');
        setJoinError('스트림이 아직 준비되지 않았습니다. 잠시 후 다시 시도해주세요.');
      }
    }
  }, [stream?.status, token]); // 의존성 최소화로 중복 요청 방지

  const handleBack = () => {
    navigate(-1);
  };

  const handleRetryJoin = () => {
    if (isJoining) {
      // TODO: Remove debug log
      // console.warn('[StreamDetail] 재시도 버튼 클릭했지만 이미 요청 진행 중');
      return;
    }
    
    // TODO: Remove debug log
    // console.log('[StreamDetail] 수동 재시도 시작');
    setJoinError(null);
    joinStream();
  };

  const handleRetryStream = () => {
    refetchStream();
  };

  // 스트림 ID가 유효하지 않은 경우
  if (!numericStreamId) {
    return (
      <div className="flex flex-col h-full">
        <div className="flex items-center justify-between p-4 border-b bg-white">
          <Button variant="ghost" size="icon" onClick={handleBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-bold">스트림</h1>
          <div className="w-10" />
        </div>

        <div className="flex-1 flex items-center justify-center p-4">
          <div className="text-center space-y-4">
            <AlertCircle className="w-12 h-12 mx-auto text-red-400" />
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                잘못된 스트림 ID
              </h3>
              <p className="text-sm text-gray-500 mb-4">
                유효하지 않은 스트림 주소입니다.
              </p>
              <Button onClick={handleBack}>
                <ArrowLeft className="w-4 h-4 mr-2" />
                돌아가기
              </Button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // 스트림 정보 로딩 중
  if (isStreamLoading) {
    return (
      <div className="flex flex-col h-full">
        <div className="flex items-center justify-between p-4 border-b bg-white">
          <Button variant="ghost" size="icon" onClick={handleBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-bold">스트림</h1>
          <div className="w-10" />
        </div>

        <div className="flex-1 flex items-center justify-center p-4">
          <div className="text-center space-y-4">
            <Loader2 className="w-8 h-8 mx-auto animate-spin text-blue-500" />
            <p className="text-sm text-gray-500">스트림 정보를 불러오는 중...</p>
          </div>
        </div>
      </div>
    );
  }

  // 스트림 정보 로드 에러
  if (streamError || !stream) {
    return (
      <div className="flex flex-col h-full">
        <div className="flex items-center justify-between p-4 border-b bg-white">
          <Button variant="ghost" size="icon" onClick={handleBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <h1 className="text-lg font-bold">스트림</h1>
          <div className="w-10" />
        </div>

        <div className="flex-1 flex items-center justify-center p-4">
          <div className="text-center space-y-4">
            <AlertCircle className="w-12 h-12 mx-auto text-red-400" />
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">
                스트림을 찾을 수 없습니다
              </h3>
              <p className="text-sm text-gray-500 mb-4">
                요청하신 스트림이 존재하지 않거나 삭제되었습니다.
              </p>
              <div className="space-x-2">
                <Button onClick={handleRetryStream} variant="outline">
                  <RotateCcw className="w-4 h-4 mr-2" />
                  다시 시도
                </Button>
                <Button onClick={handleBack}>
                  <ArrowLeft className="w-4 h-4 mr-2" />
                  돌아가기
                </Button>
              </div>
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
        <Button variant="ghost" size="icon" onClick={handleBack}>
          <ArrowLeft className="w-5 h-5" />
        </Button>
        <h1 className="text-lg font-bold truncate px-2">{stream.title}</h1>
        <div className="w-10" />
      </div>

      {/* 메인 콘텐츠 */}
      <div className="flex-1 overflow-y-auto">
        <div className="p-4 space-y-4">
          {/* 참가 에러 알림 */}
          {joinError && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription className="flex items-center justify-between">
                <span>{joinError}</span>
                <Button 
                  size="sm" 
                  variant="outline"
                  onClick={handleRetryJoin}
                  disabled={isJoining}
                  className="ml-4"
                >
                  {isJoining ? (
                    <>
                      <Loader2 className="w-3 h-3 mr-1 animate-spin" />
                      재시도 중...
                    </>
                  ) : (
                    '재시도'
                  )}
                </Button>
              </AlertDescription>
            </Alert>
          )}

          {/* 스트림 플레이어 */}
          {token && !joinError ? (
            <StreamPlayer stream={stream} token={token} />
          ) : (
            <div className="aspect-video bg-gray-100 rounded-lg flex items-center justify-center">
              <div className="text-center space-y-2">
                <Loader2 className="w-6 h-6 mx-auto animate-spin text-gray-400" />
                <p className="text-sm text-gray-500">
                  {isJoining ? '스트림 참가 중...' : '스트림 연결 준비 중...'}
                </p>
              </div>
            </div>
          )}

          {/* 스트림 정보 */}
          <StreamInfo stream={stream} />
        </div>
      </div>
    </div>
  );
};

export default StreamDetail;