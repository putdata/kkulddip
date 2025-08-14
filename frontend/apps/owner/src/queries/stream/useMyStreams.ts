import { useQuery } from '@tanstack/react-query';
import type { Stream } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 내 스트림 목록을 조회하는 쿼리 훅
 */
export const useMyStreams = () => {
  return useQuery<Stream[]>({
    queryKey: streamQueryKeys.myStreams(),
    queryFn: streamService.getMyStreams,
  });
};
