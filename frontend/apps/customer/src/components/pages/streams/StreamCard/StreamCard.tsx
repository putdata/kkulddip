import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import { Users, Clock, Loader2 } from 'lucide-react';
import type { StreamListItem } from '@/types/stream';

interface StreamCardProps {
  stream: StreamListItem;
  onClick: () => void;
  disabled?: boolean;
  isConnecting?: boolean;
}

const StreamCard = ({
  stream,
  onClick,
  disabled = false,
  isConnecting = false,
}: StreamCardProps) => {
  const formatViewerCount = (count: number) => {
    if (count >= 1000) {
      return `${(count / 1000).toFixed(1)}k`;
    }
    return count.toString();
  };

  const formatStartTime = (startedAt: string) => {
    const startTime = new Date(startedAt);
    const now = new Date();
    const diffMinutes = Math.floor(
      (now.getTime() - startTime.getTime()) / (1000 * 60),
    );

    if (diffMinutes < 60) {
      return `${diffMinutes}분 전 시작`;
    }

    const diffHours = Math.floor(diffMinutes / 60);
    return `${diffHours}시간 전 시작`;
  };

  return (
    <Card
      className={`transition-all duration-200 ${
        disabled
          ? 'cursor-not-allowed opacity-60'
          : 'cursor-pointer hover:scale-105 hover:shadow-lg'
      } ${isConnecting ? 'ring-2 ring-blue-500 ring-opacity-50' : ''}`}
      onClick={disabled ? undefined : onClick}
    >
      <div className="relative">
        {/* 썸네일 영역 */}
        <div className="flex aspect-video items-center justify-center rounded-t-lg bg-gradient-to-br from-gray-100 to-gray-200">
          {stream.thumbnailUrl ? (
            <img
              src={stream.thumbnailUrl}
              alt={stream.title}
              className="h-full w-full rounded-t-lg object-cover"
            />
          ) : (
            <div className="text-center text-gray-400">
              <div className="mx-auto mb-2 flex h-12 w-12 items-center justify-center rounded-full bg-gray-300">
                <Users className="h-6 w-6" />
              </div>
              <p className="text-sm">라이브 스트림</p>
            </div>
          )}
        </div>

        {/* LIVE 배지 / 연결 중 표시 */}
        <div className="absolute left-2 top-2">
          {isConnecting ? (
            <Badge className="bg-blue-500 font-medium text-white">
              <Loader2 className="mr-1 h-3 w-3 animate-spin" />
              연결 중...
            </Badge>
          ) : (
            <Badge className="bg-red-500 font-medium text-white hover:bg-red-600">
              🔴 LIVE
            </Badge>
          )}
        </div>

        {/* 시청자 수 */}
        <div className="absolute right-2 top-2">
          <Badge
            variant="secondary"
            className="border-none bg-black/70 text-white"
          >
            <Users className="mr-1 h-3 w-3" />
            {formatViewerCount(stream.viewerCount)}
          </Badge>
        </div>
      </div>

      <CardContent className="p-4">
        {/* 스트림 제목 */}
        <h3 className="mb-2 line-clamp-2 text-sm font-semibold leading-tight">
          {stream.title}
        </h3>

        {/* 가게 정보 */}
        <div className="space-y-1">
          <p className="text-xs font-medium text-gray-600">
            {stream.storeName}
          </p>

          {/* 시작 시간 */}
          <div className="flex items-center text-xs text-gray-500">
            <Clock className="mr-1 h-3 w-3" />
            {formatStartTime(stream.startedAt)}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default StreamCard;
