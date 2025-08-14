import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import type { StreamFlowStatus } from '@/types/stream';
import { getStatusColor, getStatusText, formatDate } from '@/utils/streamUtils';
import {
  Radio,
  CheckCircle,
  AlertCircle,
  Clock,
  StopCircle,
  Loader2,
} from 'lucide-react';

interface StreamStatusCardProps {
  title: string;
  description?: string;
  status: StreamFlowStatus;
  error?: string;
  viewerCount?: number;
  startedAt?: string;
  endedAt?: string;
  className?: string;
}

const getStatusIcon = (status: StreamFlowStatus) => {
  const iconProps = { className: 'h-4 w-4' };

  switch (status) {
    case 'IDLE':
      return <Clock {...iconProps} />;
    case 'CREATING':
    case 'CONNECTING':
    case 'PUBLISHING':
    case 'ENDING':
      return <Loader2 {...iconProps} className="animate-spin" />;
    case 'READY':
    case 'CONNECTED':
      return <CheckCircle {...iconProps} />;
    case 'LIVE':
      return <Radio {...iconProps} className="text-red-500" />;
    case 'ENDED':
      return <StopCircle {...iconProps} />;
    case 'ERROR':
      return <AlertCircle {...iconProps} className="text-red-500" />;
    default:
      return <Clock {...iconProps} />;
  }
};

const getStatusDescription = (status: StreamFlowStatus): string => {
  switch (status) {
    case 'IDLE':
      return '스트림을 생성해주세요';
    case 'CREATING':
      return '스트림을 생성하는 중입니다...';
    case 'READY':
      return '스트림이 준비되었습니다';
    case 'CONNECTING':
      return 'OpenVidu 세션에 연결하는 중...';
    case 'CONNECTED':
      return '세션에 연결되었습니다. 방송을 시작할 수 있습니다';
    case 'PUBLISHING':
      return '방송을 시작하는 중입니다...';
    case 'LIVE':
      return '라이브 방송 중입니다';
    case 'ENDING':
      return '방송을 종료하는 중입니다...';
    case 'ENDED':
      return '방송이 종료되었습니다';
    case 'ERROR':
      return '오류가 발생했습니다';
    default:
      return '';
  }
};

export const StreamStatusCard = ({
  title,
  description,
  status,
  error,
  viewerCount,
  startedAt,
  endedAt,
  className,
}: StreamStatusCardProps) => {
  const statusText = getStatusText(status);
  const statusColor = getStatusColor(status);
  const statusIcon = getStatusIcon(status);
  const statusDescription = getStatusDescription(status);

  return (
    <Card className={className}>
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg">
            {title || '스트림 제목 없음'}
          </CardTitle>
          <Badge className={`gap-1 ${statusColor}`}>
            {statusIcon}
            {statusText}
          </Badge>
        </div>
      </CardHeader>
      <CardContent className="space-y-3">
        {description && (
          <p className="text-muted-foreground text-sm">{description}</p>
        )}

        <div className="space-y-2">
          <p className="text-sm">{statusDescription}</p>

          {error && (
            <div className="rounded-md border border-red-200 bg-red-50 p-3 dark:border-red-800 dark:bg-red-900/20">
              <div className="flex items-start gap-2">
                <AlertCircle className="mt-0.5 h-4 w-4 flex-shrink-0 text-red-500" />
                <div>
                  <h4 className="text-sm font-medium text-red-700 dark:text-red-400">
                    오류 발생
                  </h4>
                  <p className="mt-1 text-sm text-red-600 dark:text-red-300">
                    {error}
                  </p>
                </div>
              </div>
            </div>
          )}
        </div>

        {(status === 'LIVE' || status === 'ENDED') && (
          <div className="grid grid-cols-2 gap-4 border-t pt-2">
            {typeof viewerCount === 'number' && (
              <div>
                <p className="text-muted-foreground text-xs">시청자 수</p>
                <p className="text-sm font-medium">
                  {viewerCount.toLocaleString()}명
                </p>
              </div>
            )}

            {startedAt && (
              <div>
                <p className="text-muted-foreground text-xs">시작 시간</p>
                <p className="text-sm font-medium">{formatDate(startedAt)}</p>
              </div>
            )}

            {endedAt && (
              <div className="col-span-2">
                <p className="text-muted-foreground text-xs">종료 시간</p>
                <p className="text-sm font-medium">{formatDate(endedAt)}</p>
              </div>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
};
