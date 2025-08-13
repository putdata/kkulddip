import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  Order,
  ConfirmOrderRequest,
  ConfirmOrderResponse,
} from '@/types/order';

/**
 * Order 관련 API 서비스
 */
export const orderService = {
  /**
   * 대기 중인 주문 조회
   * 사장님이 확인해야 할 주문들을 조회합니다 (AWAITING_CONFIRMATION)
   */
  getPendingOrders: (storeId: number): Promise<Order[]> => {
    return apiClient.get<Order[]>(
      API_PATH.ORDERS.PENDING,
      { storeId },
    );
  },

  /**
   * 주문 확정/거절
   * 사장님이 주문을 확정하거나 거절합니다
   */
  confirmOrder: (
    orderId: string,
    data: ConfirmOrderRequest,
  ): Promise<ConfirmOrderResponse> => {
    return apiClient.post<ConfirmOrderResponse>(
      API_PATH.ORDERS.CONFIRM(orderId),
      data,
    );
  },
};