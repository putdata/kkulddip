import { Clock, Users, MapPin } from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent } from '@/components/ui/card';
import type { StreamDetail } from '@/types/stream';

interface StreamInfoProps {
  stream: StreamDetail;
}

const StreamInfo = ({ stream }: StreamInfoProps) => {
  const formatStartTime = (startedAt: string | null) => {
    if (!startedAt) {
      return '시작 시간 정보 없음';
    }
    
    const startTime = new Date(startedAt);
    const now = new Date();
    const diffMinutes = Math.floor((now.getTime() - startTime.getTime()) / (1000 * 60));
    
    if (diffMinutes < 1) {
      return '방금 시작됨';
    } else if (diffMinutes < 60) {
      return `${diffMinutes}분 전 시작`;
    } else {
      const diffHours = Math.floor(diffMinutes / 60);
      const remainingMinutes = diffMinutes % 60;
      return `${diffHours}시간 ${remainingMinutes}분 전 시작`;
    }
  };

  const formatViewerCount = (count: number) => {
    if (count >= 1000) {
      return `${(count / 1000).toFixed(1)}k명`;
    }
    return `${count}명`;
  };

  const getStatusBadge = (status: StreamDetail['status']) => {
    switch (status) {
      case 'LIVE':
        return (
          <Badge className="bg-red-500 hover:bg-red-600 text-white animate-pulse">
            🔴 라이브
          </Badge>
        );
      case 'READY':
        return (
          <Badge className="bg-yellow-500 hover:bg-yellow-600 text-white">
            ⏸️ 준비 중
          </Badge>
        );
      case 'ENDED':
        return (
          <Badge variant="secondary" className="bg-gray-500 text-white">
            ⏹️ 종료됨
          </Badge>
        );
      default:
        return null;
    }
  };

  return (
    <Card>
      <CardContent className="p-4 space-y-4">
        {/* 스트림 제목 및 상태 */}
        <div className="space-y-2">
          <div className="flex items-start justify-between">
            <h1 className="text-xl font-bold leading-tight pr-2">
              {stream.title}
            </h1>
            {getStatusBadge(stream.status)}
          </div>
          
          {stream.description && (
            <p className="text-sm text-gray-600 leading-relaxed">
              {stream.description}
            </p>
          )}
        </div>

        {/* 가게 정보 */}
        <div className="flex items-center space-x-2 text-sm">
          <MapPin className="w-4 h-4 text-gray-500" />
          <span className="font-medium text-gray-900">{stream.storeName}</span>
        </div>

        {/* 스트림 통계 */}
        <div className="flex items-center space-x-6 text-sm">
          {/* 시청자 수 */}
          <div className="flex items-center space-x-1 text-gray-600">
            <Users className="w-4 h-4" />
            <span>{formatViewerCount(stream.viewerCount)} 시청 중</span>
          </div>

          {/* 시작 시간 */}
          {stream.startedAt && (
            <div className="flex items-center space-x-1 text-gray-600">
              <Clock className="w-4 h-4" />
              <span>{formatStartTime(stream.startedAt)}</span>
            </div>
          )}
        </div>

      </CardContent>
    </Card>
  );
};

export default StreamInfo;