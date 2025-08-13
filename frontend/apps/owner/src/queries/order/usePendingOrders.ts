import { useQuery } from '@tanstack/react-query';
import type { Order } from '@/types/order';
import { orderService } from '@/services/orderService';
import { orderQueryKeys } from './orderQueryKeys';

/**
 * 대기 중인 주문 목록을 조회
 * 사장님이 확인해야 할 주문들을 조회합니다 (AWAITING_CONFIRMATION)
 */
export const usePendingOrders = (storeId: number) => {
  return useQuery<Order[]>({
    queryKey: orderQueryKeys.pendingByStore(storeId),
    queryFn: () => orderService.getPendingOrders(storeId),
    enabled: Boolean(storeId),
  });
};