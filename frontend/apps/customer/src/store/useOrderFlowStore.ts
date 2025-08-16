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
  // 장바구니 백업 데이터
  backupCartItems: Array<{
    name: string;
    price: number;
    description: string;
    quantity: number;
    ddipboxId: number;
    discountRate: number;
    storeId: number;
  }> | null;
  backupStoreInfo: {
    storeId: number;
    name: string;
    address: string;
    pickupTime: string;
  } | null;

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
      backupCartItems: null,
      backupStoreInfo: null,

      // 장바구니/결제 데이터 업데이트
      updateOrderData: data =>
        set(state => ({
          orderData: { ...state.orderData, ...data },
        })),

      // 주문 완료 처리
      completeOrder: paymentData => {
        const { orderData } = get();

        // 장바구니 데이터 백업 (장바구니가 비워지기 전에)
        const cartStore = useCartStore.getState();
        const userStore = useUserStore.getState();

        // 실제 서버 응답 데이터가 있으면 사용, 없으면 더미 데이터 생성
        let completedOrderResponse: OrderResponse;
        
        if (paymentData.orderResponse) {
          // 실제 서버 응답 데이터 사용
          completedOrderResponse = paymentData.orderResponse;
        } else {
          // 더미 데이터 (fallback)
          completedOrderResponse = {
            orderId: `ORDER-${Date.now()}`,
            customerId: userStore.user?.userId || 0,
            storeId: cartStore.storeInfo?.storeId || 0,
            originalPrice: orderData.total,
            finalPrice: paymentData.finalAmount,
            orderStatus: 'PENDING',
            orderDate: new Date().toISOString(),
          };
        }

        set({
          completedOrderData: completedOrderResponse,
          isCompleted: true,
          // 장바구니 데이터 백업
          backupCartItems: cartStore.items || [],
          backupStoreInfo: cartStore.storeInfo || null,
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
          backupCartItems: null,
          backupStoreInfo: null,
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
        backupCartItems: state.backupCartItems,
        backupStoreInfo: state.backupStoreInfo,
      }),
    },
  ),
);
