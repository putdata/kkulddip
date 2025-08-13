import { generatePath, useNavigate } from 'react-router-dom';
import { useStoreIdParam } from '@/hooks/useStoreIdParam';
import { useMyStreams } from '@/queries/stream';
import { useStreamFlowManager } from '@/hooks/useStreamFlowManager';
import { CreateStreamDialog } from '@/components/stream/CreateStreamDialog';
import { StreamStatusCard } from '@/components/stream/StreamStatusCard';
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
import { mapApiStatusToFlowStatus } from '@/utils/streamUtils';
import { Play, Plus, AlertCircle } from 'lucide-react';
import type { Stream } from 'common';
import { ROUTE_PATH } from '@/router/route-path';

const StreamingDashboard = () => {
  const navigate = useNavigate();
  const storeId = useStoreIdParam();

  const { data: streams, isLoading, error } = useMyStreams();
  const streamFlowManager = useStreamFlowManager();

  const activeStream = streams?.find(
    stream => stream.status === 'READY' || stream.status === 'LIVE',
  );

  const endedStreams =
    streams?.filter(stream => stream.status === 'ENDED').slice(0, 3) || [];

  const handleStreamStart = (stream: Stream) => {
    navigate(
      generatePath(ROUTE_PATH.STORE.STREAMING_LIVE, {
        storeId: String(storeId),
        streamId: String(stream.id),
      }),
    );
  };

  const handleCreateStream = streamFlowManager.createStream;

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="mb-6 text-2xl font-bold">라이브 대시보드</h1>
        </div>
        <div className="grid gap-6 md:grid-cols-2">
          <Skeleton className="h-[200px]" />
          <Skeleton className="h-[200px]" />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="mb-6 text-2xl font-bold">라이브 대시보드</h1>
        </div>
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            스트림 정보를 불러오는데 실패했습니다. 페이지를 새로고침 해주세요.
          </AlertDescription>
        </Alert>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">라이브 대시보드</h1>
      </div>

      {activeStream && (
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Play className="h-5 w-5" />
                  활성 스트림
                </div>
              </CardTitle>
              <CardDescription>
                {activeStream.status === 'LIVE'
                  ? '현재 라이브 방송 중입니다.'
                  : '준비된 스트림입니다. 방송을 시작하세요.'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <StreamStatusCard
                  title={activeStream.title}
                  description={activeStream.description || undefined}
                  status={mapApiStatusToFlowStatus(activeStream.status)}
                  viewerCount={activeStream.viewerCount}
                  startedAt={activeStream.startedAt || undefined}
                />
                <div className="flex items-center justify-between">
                  <div className="text-muted-foreground text-sm">
                    {activeStream.status === 'LIVE'
                      ? '방송 관리 페이지에서 방송을 제어할 수 있습니다.'
                      : '방송 페이지로 이동하여 스트림을 시작하세요.'}
                  </div>
                  <Button
                    onClick={() => handleStreamStart(activeStream)}
                    className="gap-2"
                  >
                    <Play className="h-4 w-4" />
                    {activeStream.status === 'LIVE' ? '방송 관리' : '방송 시작'}
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {!activeStream && (
        <Card>
          <CardContent className="py-16 text-center">
            <div className="bg-muted mx-auto mb-4 w-fit rounded-full p-4">
              <Plus className="text-muted-foreground h-8 w-8" />
            </div>
            <h3 className="mb-2 text-lg font-semibold">
              활성 스트림이 없습니다
            </h3>
            <p className="text-muted-foreground mb-6">
              새로운 라이브 스트림을 생성하여 고객들과 실시간으로 소통하세요.
            </p>
            <CreateStreamDialog
              onSubmit={handleCreateStream}
              isLoading={streamFlowManager.isLoading}
              trigger={
                <Button size="lg" className="gap-2">
                  <Plus className="h-5 w-5" />
                  스트림 생성하기
                </Button>
              }
            />
          </CardContent>
        </Card>
      )}

      {endedStreams.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>최근 종료된 스트림</CardTitle>
            <CardDescription>
              최근에 종료된 스트림 기록입니다. 종료된 스트림은 다시 시작할 수
              없습니다.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {endedStreams.map(stream => (
                <StreamStatusCard
                  key={stream.id}
                  title={stream.title}
                  description={stream.description || undefined}
                  status={mapApiStatusToFlowStatus(stream.status)}
                  viewerCount={stream.viewerCount}
                  startedAt={stream.startedAt || undefined}
                  endedAt={stream.endedAt || undefined}
                  className="border-l-4 border-l-gray-300 opacity-75"
                />
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default StreamingDashboard;
