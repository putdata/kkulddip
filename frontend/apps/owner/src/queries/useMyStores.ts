import { useQuery } from '@tanstack/react-query';
import type { StoreListResponse } from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 내 스토어 목록을 조회
 */
export const useMyStores = () => {
  return useQuery<StoreListResponse>({
    queryKey: storeQueryKeys.list(),
    queryFn: storeService.getMyStores,
  });
};
