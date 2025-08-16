import { useQuery } from '@tanstack/react-query';
import type { StoreListResponse } from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 사용자가 소유한 매장 목록을 조회하는 쿼리 훅
 *
 * @returns 내 매장 목록 쿼리 객체
 */
export const useMyStores = () => {
  return useQuery<StoreListResponse>({
    queryKey: storeQueryKeys.lists(),
    queryFn: storeService.getMyStores,
  });
};
