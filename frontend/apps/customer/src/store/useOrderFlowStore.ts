import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';
import type { OrderData, PaymentData } from '@/types/orderflow';
import type { OrderResponse } from '@/types/payments';
import { useCartStore } from '@/store/useCartStore';
import { useUserStore } from 'common';

interface OrderFlowState {
  // 기본 주문 데이터
  orderData: OrderData;
  completedOrderData: OrderResponse | null;
  isCompleted: boolean;

  // 액션들
  updateOrderData: (data: Partial<OrderData>) => void;
  completeOrder: (paymentData: PaymentData) => void;
  createNewOrder: () => void;
  initializeFromSession: () => void;
}

const initialOrderData: OrderData = {
  total: 0,
  orderItems: [], // 빈 배열로 초기화
  appliedCouponId: undefined,
  discountAmount: undefined,
  finalAmount: 0,
  orderNumber: '',
  orderDate: new Date(),
};

export const useOrderFlowStore = create<OrderFlowState>()(
  persist(
    (set, get) => ({
      // 초기 상태
      orderData: initialOrderData,
      completedOrderData: null,
      isCompleted: false,

      // 장바구니/결제 데이터 업데이트
      updateOrderData: data =>
        set(state => ({
          orderData: { ...state.orderData, ...data },
        })),

      // 주문 완료 처리
      completeOrder: paymentData => {
        const { orderData } = get();

        // 실제 데이터 가져오기
        const cartStore = useCartStore.getState();
        const userStore = useUserStore.getState();

        const completedOrderResponse: OrderResponse = {
          orderId: `ORDER-${Date.now()}`,
          customerId: userStore.user?.userId || 0,
          storeId: cartStore.storeInfo?.storeId || 0,
          originalPrice: orderData.total,
          finalPrice: paymentData.finalAmount,
          orderStatus: 'COMPLETED',
          orderDate: new Date().toISOString(),
        };

        set({
          completedOrderData: completedOrderResponse, // OrderResponse 타입으로 저장
          isCompleted: true,
        });
        // 뒤로가기 차단
        window.history.replaceState(null, '', window.location.pathname);
      },

      // 새로운 주문 시작
      createNewOrder: () => {
        set({
          orderData: initialOrderData,
          completedOrderData: null,
          isCompleted: false,
        });
      },

      // 세션에서 초기화
      initializeFromSession: () => {
        const { isCompleted, completedOrderData } = get();

        if (isCompleted && !completedOrderData) {
          const error = new Error('주문 완료 데이터를 찾을 수 없습니다.');
          console.error('주문 데이터 누락:', error);
          throw error;
        }
      },
    }),
    {
      name: 'order-flow-storage',
      storage: createJSONStorage(() => sessionStorage),
      partialize: state => ({
        isCompleted: state.isCompleted,
        completedOrderData: state.completedOrderData,
      }),
    },
  ),
);
