import { useQuery } from '@tanstack/react-query';
import type { Stream } from 'common';
import { streamService } from '@/services/streamService';
import { streamQueryKeys } from './streamQueryKeys';

/**
 * 특정 가게의 스트림 목록을 조회하는 쿼리 훅
 */
export const useStoreStreams = (storeId: number | null) => {
  return useQuery<Stream[]>({
    queryKey: streamQueryKeys.store(storeId!),
    queryFn: () => streamService.getStoreStreams(storeId!),
    enabled: storeId !== null && storeId > 0,
  });
};
