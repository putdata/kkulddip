import { useQuery } from '@tanstack/react-query';
import { toast } from 'sonner';
import { orderService } from '@/services/orderService';
import { orderQueryKeys } from './orderQueryKeys';
import type { StoreOrderHistoryResponse } from '@/types/order';

/**
 * 매장의 주문 내역을 조회하는 쿼리 훅
 *
 * @param storeId - 조회할 매장 ID
 * @param options - React Query 옵션 (enabled, refetchInterval)
 * @returns 매장 주문 내역 쿼리 객체
 */
export const useStoreOrderHistory = (
  storeId: number,
  options?: {
    enabled?: boolean;
    refetchInterval?: number;
  },
) => {
  const query = useQuery<StoreOrderHistoryResponse, Error>({
    queryKey: orderQueryKeys.storeHistoryByStore(storeId),
    queryFn: () => orderService.getStoreOrderHistory(storeId),
    enabled: options?.enabled ?? Boolean(storeId),
    refetchInterval: options?.refetchInterval,
    staleTime: 60000,
  });

  // 에러 처리
  if (query.error) {
    console.error('주문 내역 조회 실패:', query.error);
    toast.error('주문 내역을 불러오는데 실패했습니다.');
  }

  return query;
};
