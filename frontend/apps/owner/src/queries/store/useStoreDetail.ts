import { useQuery } from '@tanstack/react-query';
import type { Store } from '@/types/store';
import { storeService } from '@/services/storeService';
import { storeQueryKeys } from './storeQueryKeys';

/**
 * 특정 가게의 상세 정보를 조회하는 쿼리 훅
 */
export const useStoreDetail = (storeId: number) => {
  return useQuery<Store>({
    queryKey: storeQueryKeys.detail(storeId.toString()),
    queryFn: () => storeService.getStore(storeId),
    enabled: storeId !== null && storeId > 0,
  });
};
