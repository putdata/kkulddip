import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useStreamDetails } from '@/queries/stream';
import { useStreamFlowManager } from '@/hooks/useStreamFlowManager';
import { StreamStatusCard } from '@/components/stream/StreamStatusCard';
import { StreamFlowControls } from '@/components/stream/StreamFlowControls';
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
import { mapApiStatusToFlowStatus } from '@/utils/streamUtils';
import { ArrowLeft, Video, AlertCircle } from 'lucide-react';

const StreamingLive = () => {
  const navigate = useNavigate();
  const { storeId, streamId } = useParams<{
    storeId: string;
    streamId: string;
  }>();
  const [showExitConfirm, setShowExitConfirm] = useState(false);
  const [showEndConfirm, setShowEndConfirm] = useState(false);
  const [hasTriedConnect, setHasTriedConnect] = useState(false);

  const { data: stream, isLoading, error } = useStreamDetails(Number(streamId));

  const streamFlowManager = useStreamFlowManager({
    initialStream: stream,
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
  }, [stream?.id, stream?.status, streamFlowManager.streamFlow.status]);

  // LIVE 상태 스트림에 들어왔을 때 한 번만 자동 연결 시도
  useEffect(() => {
    if (!stream || hasTriedConnect) {
      return;
    }

    const handleStream = async () => {
      if (stream.status === 'ENDED') {
        // 종료된 스트림 - 백엔드에 정리 요청
        try {
          console.log('종료된 스트림 서버 정리 요청');
          await streamFlowManager.endStream();
        } catch (error) {
          console.error('종료된 스트림 정리 실패:', error);
        }
      } else if (stream.status === 'LIVE' && !streamFlowManager.openVidu.isConnected) {
        // LIVE 스트림 - 연결 시도
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
      
      setHasTriedConnect(true);
    };

    handleStream();
  }, [stream, hasTriedConnect]);

  // 페이지 새로고침/닫기 시 세션 정리
  useEffect(() => {
    const handleBeforeUnload = () => {
      // 즉시 OpenVidu 세션 정리
      streamFlowManager.openVidu.cleanup();
      
      // LIVE 상태면 백엔드에도 종료 요청
      if (streamFlowManager.streamFlow.status === 'LIVE') {
        streamFlowManager.endStream();
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    window.addEventListener('unload', handleBeforeUnload);

    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
      window.removeEventListener('unload', handleBeforeUnload);
    };
  }, []);

  // 컴포넌트 언마운트 시 OpenVidu 세션 정리
  useEffect(() => {
    return () => {
      // 상태에 관계없이 OpenVidu 세션을 정리
      streamFlowManager.openVidu.cleanup();
      
      // LIVE 상태면 백엔드에도 종료 요청
      if (streamFlowManager.streamFlow.status === 'LIVE') {
        streamFlowManager.endStream();
      }
    };
  }, []);


  const handleGoBack = () => {
    if (streamFlowManager.streamFlow.status === 'LIVE') {
      setShowExitConfirm(true);
      return;
    }
    navigate(`/${storeId}/streaming`);
  };

  const handleConfirmExit = async () => {
    setShowExitConfirm(false);
    await streamFlowManager.endStream();
    navigate(`/${storeId}/streaming`);
  };

  const handleCancelExit = () => {
    setShowExitConfirm(false);
  };

  const handleEndStream = () => {
    if (streamFlowManager.streamFlow.status === 'LIVE') {
      setShowEndConfirm(true);
    }
  };

  const handleConfirmEnd = async () => {
    setShowEndConfirm(false);
    await streamFlowManager.endStream();
  };

  const handleCancelEnd = () => {
    setShowEndConfirm(false);
  };

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <Button variant="outline" size="sm" onClick={handleGoBack}>
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <h1 className="text-2xl font-bold">라이브 스트림</h1>
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
        <div className="flex items-center gap-4">
          <Button variant="outline" size="sm" onClick={handleGoBack}>
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <h1 className="text-2xl font-bold">라이브 스트림</h1>
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
        <div className="flex items-center gap-4">
          <Button variant="outline" size="sm" onClick={handleGoBack}>
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <h1 className="text-2xl font-bold">라이브 스트림</h1>
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
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="outline" size="sm" onClick={handleGoBack}>
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <h1 className="text-2xl font-bold">라이브 스트림</h1>
        </div>

        {streamFlowManager.canEndStreaming && (
          <Button
            variant="destructive"
            onClick={handleEndStream}
            disabled={streamFlowManager.isLoading}
          >
            방송 종료
          </Button>
        )}
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
                  canConnect={streamFlowManager.canConnect}
                  canStartStreaming={streamFlowManager.canStartStreaming}
                  canEndStreaming={streamFlowManager.canEndStreaming}
                  onConnect={streamFlowManager.connectToStream}
                  onStartStreaming={streamFlowManager.startStream}
                  onEndStreaming={handleEndStream}
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
                <span>{streamFlowManager.streamFlow.error || '연결에 실패했습니다.'}</span>
                <Button 
                  variant="outline" 
                  size="sm"
                  onClick={() => {
                    streamFlowManager.openVidu.resetConnection();
                    setHasTriedConnect(false);
                  }}
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

      <AlertDialog open={showEndConfirm} onOpenChange={setShowEndConfirm}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>방송 종료</AlertDialogTitle>
            <AlertDialogDescription>
              정말로 방송을 종료하시겠습니까? 종료된 스트림은 다시 시작할 수
              없습니다.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={handleCancelEnd}>
              취소
            </AlertDialogCancel>
            <AlertDialogAction onClick={handleConfirmEnd}>
              방송 종료
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};

export default StreamingLive;
