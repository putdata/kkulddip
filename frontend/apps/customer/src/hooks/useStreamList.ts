import { useQuery } from '@tanstack/react-query';
import { StreamService } from '@/services/streamService';

export const useStreamList = () => {
  return useQuery({
    queryKey: ['streams', 'live'],
    queryFn: StreamService.getLiveStreams,
    refetchInterval: 30000, // 30초마다 새로고침
    staleTime: 10000, // 10초 동안 캐시 유지
  });
};
