import { useQuery } from '@tanstack/react-query';
import type { PendingOrdersResponse } from '@/types/order';
import { orderService } from '@/services/orderService';
import { orderQueryKeys } from './orderQueryKeys';

/**
 * 대기 중인 주문 목록을 조회하는 쿼리 훅
 * 
 * @param storeId - 조회할 매장 ID
 * @returns 대기 주문 목록 쿼리 객체 (3초마다 자동 갱신)
 */
export const usePendingOrders = (storeId: number) => {
  return useQuery<PendingOrdersResponse>({
    queryKey: orderQueryKeys.pendingByStore(storeId),
    queryFn: () => orderService.getPendingOrders(storeId),
    enabled: storeId !== null && storeId > 0,
    refetchInterval: 3000,
  });
};
