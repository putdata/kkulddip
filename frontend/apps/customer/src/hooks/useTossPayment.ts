import { useCallback } from 'react';
import { loadTossPayments } from '@tosspayments/payment-sdk';
import { ROUTE_PATH } from '@/router';
import {
  PaymentService,
  getPaymentOrderIdWithRetry,
} from '@/services/paymentService';
import { dummyProductData, dummyDiscountAmount } from '@/dummies/paymentDummy';
import type { OrderData, TossPaymentRequest } from '@/types/payments';

interface TossPaymentParams {
  productId?: number;
  quantity: number;
  customerId?: number;
  storeId?: number;
  baseUrl?: string;
}

interface UseTossPaymentReturn {
  processPayment: (params: TossPaymentParams) => Promise<void>;
  createOrderData: (params: TossPaymentParams) => OrderData;
}

export const useTossPayment = (): UseTossPaymentReturn => {
  // 주문 데이터 생성 함수
  const createOrderData = useCallback(
    (params: TossPaymentParams): OrderData => {
      const {
        productId,
        quantity,
        customerId = 1001, // 기본값
        storeId = 2001, // 기본값
      } = params;

      return {
        customerId,
        storeId,
        orderItems: [
          {
            productId: productId || dummyProductData.id || 3001,
            quantity,
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
    },
    [],
  );

  // 토스페이먼츠 결제 요청 함수
  const requestTossPayment = useCallback(
    async (
      tossPayments: ReturnType<typeof loadTossPayments>,
      paymentData: TossPaymentRequest,
    ): Promise<void> => {
      console.log('토스페이먼츠 결제 요청:', paymentData);

      try {
        await (
          await tossPayments
        ).requestPayment('카드', {
          amount: paymentData.amount,
          orderId: paymentData.paymentOrderId,
          orderName: paymentData.orderName,
          customerName: paymentData.customerName,
          successUrl: paymentData.successUrl,
          failUrl: paymentData.failUrl,
        });
      } catch (error) {
        console.error('토스페이먼츠 결제 요청 실패:', error);
        throw error;
      }
    },
    [],
  );

  // 전체 결제 플로우 처리 함수
  const processPayment = useCallback(
    async (params: TossPaymentParams): Promise<void> => {
      const baseUrl = params.baseUrl || window.location.origin;

      try {
        // 1단계: 토스페이먼츠 SDK 초기화
        const CLIENT_KEY = import.meta.env.VITE_TOSS_PAYMENTS_CLIENT_KEY;
        const tossPayments = loadTossPayments(CLIENT_KEY);

        // 2단계: 주문 생성
        console.log('=== 1단계: 주문 생성 ===');
        const orderData = createOrderData(params);
        const orderResult = await PaymentService.createOrder(orderData);

        if (orderResult.body.finalPrice <= 0) {
          throw new Error('결제 금액이 0원 이하입니다.');
        }

        // 3단계: 결제 주문 ID 생성 (재시도 로직 포함)
        console.log('=== 2단계: 결제 주문 ID 생성 ===');
        const paymentOrderId = await getPaymentOrderIdWithRetry(
          orderResult.body.orderId,
        );

        // 4단계: 토스페이먼츠 결제 요청
        console.log('=== 3단계: 토스페이먼츠 결제 요청 ===');
        await requestTossPayment(tossPayments, {
          amount: orderResult.body.finalPrice,
          paymentOrderId,
          orderName: `주문 #${orderResult.body.orderId}`,
          customerName: `고객 ${orderResult.body.customerId}`,
          successUrl: `${baseUrl}/${ROUTE_PATH.PAYMENT_SUCCESS}`,
          failUrl: `${baseUrl}/${ROUTE_PATH.PAYMENT_FAIL}`,
        });
      } catch (error) {
        console.error('결제 처리 중 오류:', error);
        throw error;
      }
    },
    [createOrderData, requestTossPayment],
  );

  return {
    processPayment,
    createOrderData,
  };
};
