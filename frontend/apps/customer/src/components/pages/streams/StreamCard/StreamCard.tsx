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

const StreamCard = ({ stream, onClick, disabled = false, isConnecting = false }: StreamCardProps) => {
  const formatViewerCount = (count: number) => {
    if (count >= 1000) {
      return `${(count / 1000).toFixed(1)}k`;
    }
    return count.toString();
  };

  const formatStartTime = (startedAt: string) => {
    const startTime = new Date(startedAt);
    const now = new Date();
    const diffMinutes = Math.floor((now.getTime() - startTime.getTime()) / (1000 * 60));
    
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
          ? 'opacity-60 cursor-not-allowed' 
          : 'cursor-pointer hover:shadow-lg hover:scale-105'
      } ${
        isConnecting ? 'ring-2 ring-blue-500 ring-opacity-50' : ''
      }`}
      onClick={disabled ? undefined : onClick}
    >
      <div className="relative">
        {/* 썸네일 영역 */}
        <div className="aspect-video bg-gradient-to-br from-gray-100 to-gray-200 rounded-t-lg flex items-center justify-center">
          {stream.thumbnailUrl ? (
            <img 
              src={stream.thumbnailUrl} 
              alt={stream.title}
              className="w-full h-full object-cover rounded-t-lg"
            />
          ) : (
            <div className="text-gray-400 text-center">
              <div className="w-12 h-12 rounded-full bg-gray-300 mx-auto mb-2 flex items-center justify-center">
                <Users className="w-6 h-6" />
              </div>
              <p className="text-sm">라이브 스트림</p>
            </div>
          )}
        </div>
        
        {/* LIVE 배지 / 연결 중 표시 */}
        <div className="absolute top-2 left-2">
          {isConnecting ? (
            <Badge className="bg-blue-500 text-white font-medium">
              <Loader2 className="w-3 h-3 mr-1 animate-spin" />
              연결 중...
            </Badge>
          ) : (
            <Badge className="bg-red-500 hover:bg-red-600 text-white font-medium">
              🔴 LIVE
            </Badge>
          )}
        </div>
        
        {/* 시청자 수 */}
        <div className="absolute top-2 right-2">
          <Badge variant="secondary" className="bg-black/70 text-white border-none">
            <Users className="w-3 h-3 mr-1" />
            {formatViewerCount(stream.viewerCount)}
          </Badge>
        </div>
      </div>
      
      <CardContent className="p-4">
        {/* 스트림 제목 */}
        <h3 className="font-semibold text-sm mb-2 line-clamp-2 leading-tight">
          {stream.title}
        </h3>
        
        {/* 가게 정보 */}
        <div className="space-y-1">
          <p className="text-xs text-gray-600 font-medium">
            {stream.storeName}
          </p>
          
          {/* 시작 시간 */}
          <div className="flex items-center text-xs text-gray-500">
            <Clock className="w-3 h-3 mr-1" />
            {formatStartTime(stream.startedAt)}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default StreamCard;