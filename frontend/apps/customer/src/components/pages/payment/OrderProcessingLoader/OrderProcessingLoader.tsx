import { useEffect } from 'react';
import { loadTossPayments } from '@tosspayments/payment-sdk';
import {
  processCompleteOrder,
  type OrderData as APIOrderData,
} from '@/services/paymentService';
import { useOrderFlowStore } from '@/store/useOrderFlowStore';
import { useOrderState } from '@/hooks/useOrderState';
import { dummyProductData, dummyDiscountAmount } from '@/dummies/paymentDummy';

const OrderProcessingLoader = () => {
  const { orderData } = useOrderFlowStore();
  const { handlePendingToComplete, handlePendingToPayment } = useOrderState();

  useEffect(() => {
    const processPayment = async () => {
      try {
        // 토스페이먼츠 SDK 초기화
        const CLIENT_KEY = import.meta.env.VITE_TOSS_PAYMENTS_CLIENT_KEY;
        const tossPayments = loadTossPayments(CLIENT_KEY);

        // API 호출용 주문 데이터 준비
        const apiOrderData: APIOrderData = {
          customerId: 1001, // 기본값 사용
          storeId: 2001, // 기본값 사용
          orderItems: [
            {
              productId: orderData.productId || dummyProductData.id || 3001,
              quantity: orderData.quantity,
              unitPrice: dummyProductData.price,
              discountInfos:
                dummyDiscountAmount > 0
                  ? [
                      {
                        discountCode: 5001,
                        discountAmount: dummyDiscountAmount,
                      },
                    ]
                  : [],
            },
          ],
        };

        // 토스 결제 처리 (리다이렉트 발생)
        await processCompleteOrder(tossPayments, apiOrderData);
      } catch (error) {
        console.error('결제 처리 실패:', error);
        // 실패시 결제 페이지로 복귀
        handlePendingToPayment();
      }
    };

    // 컴포넌트 마운트 후 바로 결제 처리
    processPayment();
  }, [orderData, handlePendingToComplete, handlePendingToPayment]);

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600"></div>
        <p className="text-lg font-medium">결제를 처리하는 중...</p>
      </div>
    </div>
  );
};

export default OrderProcessingLoader;
