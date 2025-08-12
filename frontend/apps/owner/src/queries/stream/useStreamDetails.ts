import { useQuery } from '@tanstack/react-query';
import type { Stream } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 특정 스트림 상세 정보 조회
 */
export const useStreamDetails = (streamId: number | null) => {
  return useQuery<Stream>({
    queryKey: streamQueryKeys.detail(streamId!),
    queryFn: () => streamService.getStreamDetails(streamId!),
    enabled: streamId !== null && streamId > 0,
  });
};
