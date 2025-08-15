import { useEffect, useState, useCallback } from 'react';
import { generatePath, useNavigate } from 'react-router-dom';
import { useNumberParam } from 'common';
import { useStoreSelection } from '@/hooks/useStoreSelection';
import { ArrowLeft, Video, AlertCircle } from 'lucide-react';

import { useStreamDetails } from '@/queries/stream';
import { useStreamFlowManager } from '@/hooks/useStreamFlowManager';
import {
  StreamStatusCard,
  StreamFlowControls,
  EndStreamAlertDialog,
} from '@/components/pages/streaming';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Skeleton } from '@/components/ui/skeleton';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import {
  mapApiStatusToFlowStatus,
  validateStreamStatus,
} from '@/utils/streamUtils';
import { ROUTE_PATH } from '@/router/route-path';

const StreamingLive = () => {
  const navigate = useNavigate();
  const { storeId } = useStoreSelection();
  const streamId = useNumberParam('streamId');
  const [showExitConfirm, setShowExitConfirm] = useState(false);
  const [showEndDialog, setShowEndDialog] = useState(false);
  const [hasTriedConnect, setHasTriedConnect] = useState(false);

  const { data: stream, isLoading, error } = useStreamDetails(streamId);

  const handleNavigateToStream = () => {
    navigate(
      generatePath(ROUTE_PATH.STORE.STREAMING, {
        storeId: String(storeId),
      }),
    );
  };

  const streamFlowManager = useStreamFlowManager({
    initialStream: stream,
    onStreamEnded: () => handleNavigateToStream,
  });

  useEffect(() => {
    if (stream && streamFlowManager.streamFlow.status === 'IDLE') {
      streamFlowManager.updateStreamFlow({
        id: stream.id,
        title: stream.title,
        description: stream.description || '',
        status: mapApiStatusToFlowStatus(stream.status),
      });
    }
  }, [stream, streamFlowManager]);

  /** LIVE 상태 스트림에 들어왔을 때 한 번만 자동 연결 시도 */
  const handleAutoConnection = useCallback(
    async (streamData: typeof stream) => {
      if (!streamData) {
        return;
      }

      if (streamData.status === 'ENDED') {
        try {
          console.log('종료된 스트림 서버 정리 요청');
          await streamFlowManager.endStream();
        } catch (error) {
          console.error('종료된 스트림 정리 실패:', error);
        }
      } else if (
        streamData.status === 'LIVE' &&
        !streamFlowManager.openVidu.isConnected
      ) {
        try {
          console.log('LIVE 스트림 자동 연결 시도');
          await streamFlowManager.connectToStream();
          if (!streamFlowManager.openVidu.isPublishing) {
            await streamFlowManager.openVidu.startPublishing('video-container');
          }
        } catch (error) {
          console.error('LIVE 스트림 자동 연결 실패:', error);
        }
      }
    },
    [streamFlowManager],
  );

  useEffect(() => {
    if (!stream || hasTriedConnect) {
      return;
    }

    handleAutoConnection(stream).finally(() => setHasTriedConnect(true));
  }, [stream, hasTriedConnect, handleAutoConnection]);

  /** 페이지 나갈 때 세션 정리 */
  const handlePageUnload = useCallback(() => {
    streamFlowManager.openVidu.cleanup();
    if (streamFlowManager.streamFlow.status === 'LIVE') {
      streamFlowManager.endStream();
    }
  }, [streamFlowManager]);

  useEffect(() => {
    window.addEventListener('beforeunload', handlePageUnload);
    window.addEventListener('unload', handlePageUnload);

    return () => {
      window.removeEventListener('beforeunload', handlePageUnload);
      window.removeEventListener('unload', handlePageUnload);
    };
  }, [handlePageUnload]);

  /** 뒤로가기 핸들러 - LIVE 상태면 확인 다이얼로그 표시 */
  const handleGoBack = () => {
    const statusValidation = validateStreamStatus(
      streamFlowManager.streamFlow.status,
      streamFlowManager.openVidu.isConnected,
      streamFlowManager.openVidu.isPublishing,
    );

    if (statusValidation.canEndStreaming) {
      setShowExitConfirm(true);
      return;
    }

    handleNavigateToStream();
  };

  /** 나가기 확인 핸들러 */
  const handleConfirmExit = async () => {
    setShowExitConfirm(false);
    await streamFlowManager.endStream();
  };

  /** 방송 종료 다이얼로그 열기 */
  const handleShowEndDialog = () => {
    setShowEndDialog(true);
  };

  /** 에러 상태 재시도 핸들러 */
  const handleRetryConnection = () => {
    streamFlowManager.openVidu.resetConnection();
    setHasTriedConnect(false);
  };

  /** 나가기 취소 핸들러 */
  const handleCancelExit = () => {
    setShowExitConfirm(false);
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between px-4">
          <div className="flex items-center gap-4">
            <Button variant="outline" size="sm" onClick={handleGoBack}>
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <div>
              <h1 className="text-2xl font-bold">라이브 스트림</h1>
              <p className="text-muted-foreground">
                실시간 방송을 시작하고 관리하세요
              </p>
            </div>
          </div>
        </div>
        <div className="grid gap-6 lg:grid-cols-2">
          <Skeleton className="h-[400px]" />
          <Skeleton className="h-[400px]" />
        </div>
      </div>
    );
  }

  if (error || !stream) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between px-4">
          <div className="flex items-center gap-4">
            <Button variant="outline" size="sm" onClick={handleGoBack}>
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <div>
              <h1 className="text-2xl font-bold">라이브 스트림</h1>
              <p className="text-muted-foreground">
                실시간 방송을 시작하고 관리하세요
              </p>
            </div>
          </div>
        </div>
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            스트림 정보를 불러오는데 실패했습니다. 스트림이 존재하지 않거나
            액세스 권한이 없을 수 있습니다.
          </AlertDescription>
        </Alert>
      </div>
    );
  }

  if (stream.status === 'ENDED') {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between px-4">
          <div className="flex items-center gap-4">
            <Button variant="outline" size="sm" onClick={handleGoBack}>
              <ArrowLeft className="h-4 w-4" />
            </Button>
            <div>
              <h1 className="text-2xl font-bold">라이브 스트림</h1>
              <p className="text-muted-foreground">
                실시간 방송을 시작하고 관리하세요
              </p>
            </div>
          </div>
        </div>
        <Alert>
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            이 스트림은 이미 종료되었습니다. 종료된 스트림은 다시 시작할 수
            없습니다. 새로운 라이브 방송을 위해서는 새 스트림을 생성해주세요.
          </AlertDescription>
        </Alert>
        <div className="flex justify-center">
          <Button onClick={handleGoBack} className="gap-2">
            <ArrowLeft className="h-4 w-4" />
            대시보드로 돌아가기
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between px-4">
        <div className="flex items-center gap-4">
          <Button variant="outline" size="sm" onClick={handleGoBack}>
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <h1 className="text-2xl font-bold">라이브 스트림</h1>
            <p className="text-muted-foreground">
              실시간 방송을 시작하고 관리하세요
            </p>
          </div>
        </div>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        <div className="space-y-6 lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Video className="h-5 w-5" />
                방송 미리보기
              </CardTitle>
              <CardDescription>
                카메라와 마이크를 확인하고 방송을 시작하세요.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="aspect-video overflow-hidden rounded-lg bg-black">
                <div
                  id="video-container"
                  className="relative h-full w-full [&>video]:h-full [&>video]:w-full [&>video]:rounded-lg [&>video]:object-cover"
                >
                  {!['CONNECTED', 'LIVE'].includes(
                    streamFlowManager.streamFlow.status,
                  ) && (
                    <div className="absolute inset-0 z-10 flex items-center justify-center text-center text-white">
                      <div>
                        <Video className="mx-auto mb-4 h-12 w-12 text-gray-500" />
                        <p className="text-lg font-medium text-gray-300">
                          카메라 준비 중
                        </p>
                        <p className="text-sm text-gray-500">
                          화면 점검을 눌러 카메라를 확인하세요
                        </p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>방송 제어</CardTitle>
              <CardDescription>
                현재 상태에 따라 적절한 액션을 수행하세요.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="flex justify-center">
                <StreamFlowControls
                  status={streamFlowManager.streamFlow.status}
                  isLoading={streamFlowManager.isLoading}
                  onConnect={streamFlowManager.connectToStream}
                  onStartStreaming={streamFlowManager.startStream}
                  onEndStreaming={handleShowEndDialog}
                />
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="space-y-6">
          <StreamStatusCard
            title={stream.title}
            description={stream.description || undefined}
            status={streamFlowManager.streamFlow.status}
            error={streamFlowManager.streamFlow.error}
            viewerCount={stream.viewerCount}
            startedAt={stream.startedAt || undefined}
            endedAt={stream.endedAt || undefined}
          />

          {streamFlowManager.streamFlow.status === 'READY' && (
            <Alert>
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>
                방송을 시작하기 전에 카메라와 마이크 권한을 허용해주세요.
              </AlertDescription>
            </Alert>
          )}

          {streamFlowManager.streamFlow.status === 'ERROR' && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription className="flex items-center justify-between">
                <span>
                  {streamFlowManager.streamFlow.error || '연결에 실패했습니다.'}
                </span>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={handleRetryConnection}
                >
                  다시 시도
                </Button>
              </AlertDescription>
            </Alert>
          )}
        </div>
      </div>

      <AlertDialog open={showExitConfirm} onOpenChange={setShowExitConfirm}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>방송 종료 확인</AlertDialogTitle>
            <AlertDialogDescription>
              방송이 진행 중입니다. 페이지를 나가면 방송이 종료됩니다.
              계속하시겠습니까?
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={handleCancelExit}>
              취소
            </AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirmExit}>
              방송 종료하고 나가기
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      <EndStreamAlertDialog
        open={showEndDialog}
        onOpenChange={setShowEndDialog}
        onConfirm={streamFlowManager.endStream}
        isLoading={streamFlowManager.isLoading}
        streamTitle={stream?.title}
      />
    </div>
  );
};

export default StreamingLive;
