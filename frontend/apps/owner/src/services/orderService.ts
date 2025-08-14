import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  PendingOrdersResponse,
  StoreOrderHistoryResponse,
  ConfirmOrderRequest,
  ConfirmOrderResponse,
  PickupOrderResponse,
} from '@/types/order';

/**
 * Order 관련 API 서비스
 */
export const orderService = {
  /**
   * 대기 중인 주문 조회
   * 사장님이 확인해야 할 주문들을 조회합니다 (AWAITING_CONFIRMATION)
   */
  getPendingOrders: (storeId: number): Promise<PendingOrdersResponse> => {
    return apiClient.get<PendingOrdersResponse>(API_PATH.ORDERS.PENDING, {
      storeId,
    });
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

  /**
   * 가게 주문 내역 조회
   * 사장님이 본인 가게의 모든 주문 내역을 조회합니다
   */
  getStoreOrderHistory: (
    storeId: number,
  ): Promise<StoreOrderHistoryResponse> => {
    return apiClient.get<StoreOrderHistoryResponse>(
      API_PATH.ORDERS.STORE_HISTORY,
      {
        storeId,
      },
    );
  },

  /**
   * 주문 픽업 완료
   * 사장님이 주문의 픽업이 완료되었음을 처리합니다
   */
  pickupOrder: (orderId: string): Promise<PickupOrderResponse> => {
    return apiClient.post<PickupOrderResponse>(
      API_PATH.ORDERS.PICKUP(orderId),
      {},
    );
  },
};
