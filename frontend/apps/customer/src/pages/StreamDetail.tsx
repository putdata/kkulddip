import { useEffect, useState, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, AlertCircle, Loader2, RotateCcw } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Alert, AlertDescription } from '@/components/ui/alert';
import {
  StreamPlayer,
  StreamInfo,
} from '@/components/pages/streams/StreamPlayer';
import { StreamService } from '@/services/streamService';
import { toast } from 'sonner';

interface LocationState {
  token?: string;
  sessionId?: string;
  preValidated?: boolean;
}

const StreamDetail = () => {
  const { streamId } = useParams<{ streamId: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const locationState = location.state as LocationState | null;

  const [token, setToken] = useState<string | null>(
    locationState?.token || null,
  );
  const [joinError, setJoinError] = useState<string | null>(null);
  const [isJoining, setIsJoining] = useState(false);
  const [isPreValidated] = useState(locationState?.preValidated || false);

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
    enabled: Boolean(numericStreamId),
    retry: 3,
    retryDelay: 1000,
    refetchInterval: query => {
      const data = query.state.data;
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

  // 스트림 참가 (토큰 획득)
  const joinStream = useCallback(async () => {
    if (!numericStreamId) {
      console.error(
        '[StreamDetail] joinStream 호출되었지만 numericStreamId가 없음',
      );
      return;
    }

    if (isJoining) {
      console.warn(
        '[StreamDetail] 이미 스트림 참가 요청 진행 중 - 중복 요청 차단',
      );
      return;
    }

    try {
      setIsJoining(true);
      setJoinError(null);
      const response = await StreamService.joinStream(numericStreamId);

      setToken(response.token);
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : '스트림 참가에 실패했습니다.';

      console.error('스트림 참가 실패:', errorMessage);

      // 자동 재시도 대상 에러 처리
      if (
        errorMessage.includes('진행 중이 아닙니다') ||
        errorMessage.includes('사장님이 아직 방송을')
      ) {
        refetchStream();

        setTimeout(() => {
          if (!token && !isJoining) {
            setJoinError(null);
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
  }, [numericStreamId, isJoining, token, refetchStream]);

  // 사전 검증된 토큰이 없는 경우에만 자동 참가 시도
  useEffect(() => {
    // 사전 검증된 토큰이 있으면 자동 참가 시도 스킵
    if (isPreValidated && token) {
      return;
    }

    // 기존 로직: 스트림이 LIVE이고 토큰이 없을 때 자동 참가
    if (
      stream &&
      stream.status === 'LIVE' &&
      !token &&
      !joinError &&
      !isJoining
    ) {
      if (stream.sessionId) {
        joinStream();
      } else {
        setJoinError(
          '스트림이 아직 준비되지 않았습니다. 잠시 후 다시 시도해주세요.',
        );
      }
    }
  }, [
    stream?.status,
    stream?.sessionId,
    token,
    isPreValidated,
    joinError,
    isJoining,
    stream,
    joinStream,
  ]);

  const handleBack = useCallback(() => {
    navigate(-1);
  }, [navigate]);

  const handleRetryJoin = useCallback(() => {
    if (isJoining) {
      return;
    }

    setJoinError(null);
    joinStream();
  }, [isJoining, joinStream]);

  const handleRetryStream = useCallback(() => {
    refetchStream();
  }, [refetchStream]);

  // 스트림 ID가 유효하지 않은 경우
  if (!numericStreamId) {
    return (
      <div className="flex h-full flex-col">
        <div className="flex flex-1 items-center justify-center p-4">
          <div className="space-y-4 text-center">
            <AlertCircle className="mx-auto h-12 w-12 text-red-400" />
            <div>
              <h3 className="mb-2 text-lg font-medium text-gray-900">
                잘못된 스트림 ID
              </h3>
              <p className="mb-4 text-sm text-gray-500">
                유효하지 않은 스트림 주소입니다.
              </p>
              <Button onClick={handleBack}>
                <ArrowLeft className="mr-2 h-4 w-4" />
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
      <div className="flex h-full flex-col">
        <div className="flex flex-1 items-center justify-center p-4">
          <div className="space-y-4 text-center">
            <Loader2 className="mx-auto h-8 w-8 animate-spin text-blue-500" />
            <p className="text-sm text-gray-500">
              스트림 정보를 불러오는 중...
            </p>
          </div>
        </div>
      </div>
    );
  }

  // 스트림 정보 로드 에러
  if (streamError || !stream) {
    return (
      <div className="flex h-full flex-col">
        <div className="flex flex-1 items-center justify-center p-4">
          <div className="space-y-4 text-center">
            <AlertCircle className="mx-auto h-12 w-12 text-red-400" />
            <div>
              <h3 className="mb-2 text-lg font-medium text-gray-900">
                스트림을 찾을 수 없습니다
              </h3>
              <p className="mb-4 text-sm text-gray-500">
                요청하신 스트림이 존재하지 않거나 삭제되었습니다.
              </p>
              <div className="space-x-2">
                <Button onClick={handleRetryStream} variant="outline">
                  <RotateCcw className="mr-2 h-4 w-4" />
                  다시 시도
                </Button>
                <Button onClick={handleBack}>
                  <ArrowLeft className="mr-2 h-4 w-4" />
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
    <div className="flex h-full flex-col">
      {/* 메인 콘텐츠 */}
      <div className="flex-1 overflow-y-auto">
        <div className="space-y-4 p-4">
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
                      <Loader2 className="mr-1 h-3 w-3 animate-spin" />
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
            <StreamPlayer
              stream={stream}
              token={token}
              preValidated={isPreValidated}
            />
          ) : (
            <div className="flex aspect-video items-center justify-center rounded-lg bg-gray-100">
              <div className="space-y-2 text-center">
                <Loader2 className="mx-auto h-6 w-6 animate-spin text-gray-400" />
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
