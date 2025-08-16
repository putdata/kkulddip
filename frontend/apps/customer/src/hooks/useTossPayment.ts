import { useCallback } from 'react';
import { loadTossPayments } from '@tosspayments/payment-sdk';
import { useUserStore } from 'common';
import { ROUTE_PATH } from '@/router';
import {
  PaymentService,
  getPaymentOrderIdWithRetry,
} from '@/services/paymentService';
import type {
  OrderData,
  TossPaymentParams,
  TossPaymentRequest,
} from '@/types/payments';
import { useCartStore } from '@/store/useCartStore';

interface UseTossPaymentReturn {
  processPayment: (params: TossPaymentParams) => Promise<void>;
  createOrderData: (params: TossPaymentParams) => OrderData;
}

export const useTossPayment = (): UseTossPaymentReturn => {
  const { user } = useUserStore();
  const { items } = useCartStore();

  // 주문 데이터 생성 함수
  const createOrderData = useCallback(
    (params: TossPaymentParams): OrderData => {
      // userStore 디버깅 로그
      console.log('=== userStore 디버깅 ===');
      console.log('전체 user 객체:', user);
      console.log('user?.userid:', user?.userId);
      console.log('user?.email:', user?.email);
      console.log('user?.name:', user?.name);
      console.log('user가 null인가?', user === null);
      console.log('user가 undefined인가?', user === undefined);

      const { orderItems, customerId, storeId } = params;

      // userStore에서 userId 가져오기, 없으면 에러 발생
      const actualCustomerId = customerId || user?.userId || 4;
      console.log('파라미터 customerId:', customerId);
      console.log('최종 actualCustomerId:', actualCustomerId);

      if (!actualCustomerId) {
        throw new Error('사용자 정보를 찾을 수 없습니다. 로그인이 필요합니다.');
      }

      return {
        customerId: actualCustomerId,
        storeId,
        orderItems,
      };
    },
    [user?.userId],
  );

  // 토스페이먼츠 결제 요청 함수
  const requestTossPayment = useCallback(
    async (
      tossPayments: ReturnType<typeof loadTossPayments>,
      paymentData: TossPaymentRequest,
    ): Promise<void> => {
      console.log('토스페이먼츠 결제 요청:', paymentData);

      try {
        console.log('=== 토스페이먼츠 위젯 호출 시작 ===');

        const tossPaymentsInstance = await tossPayments;
        const result = await tossPaymentsInstance.requestPayment('카드', {
          amount: paymentData.amount,
          orderId: paymentData.paymentOrderId,
          orderName: paymentData.orderName,
          customerName: paymentData.customerName,
          successUrl: paymentData.successUrl,
          failUrl: paymentData.failUrl,
        });

        console.log('=== 토스페이먼츠 결제 완료 응답 ===');
        console.log('result 전체:', result);
        console.log('result 타입:', typeof result);
        console.log('result JSON:', JSON.stringify(result, null, 2));

        return result;
      } catch (error) {
        console.error('=== 토스페이먼츠 결제 요청 실패 ===');
        console.error('error 전체:', error);
        console.error('error 타입:', typeof error);
        if (error && typeof error === 'object' && 'message' in error) {
          console.error('error.message:', error.message);
        }
        if (error && typeof error === 'object' && 'code' in error) {
          console.error('error.code:', error.code);
        }
        console.error('error JSON:', JSON.stringify(error, null, 2));
        throw error;
      }
    },
    [],
  );

  const processPayment = useCallback(
    async (params: TossPaymentParams): Promise<void> => {
      const baseUrl = params.baseUrl || window.location.origin;
      let orderResult; // ← 밖으로 빼기

      try {
        // 1단계: 토스페이먼츠 SDK 초기화
        const CLIENT_KEY = import.meta.env.VITE_TOSS_PAYMENTS_CLIENT_KEY;
        console.log(
          'CLIENT_KEY 확인:',
          CLIENT_KEY ? `${CLIENT_KEY.substring(0, 10)}...` : 'undefined',
        );

        if (!CLIENT_KEY || typeof CLIENT_KEY !== 'string') {
          throw new Error(
            'VITE_TOSS_PAYMENTS_CLIENT_KEY가 설정되지 않았거나 올바르지 않습니다.',
          );
        }

        if (
          !CLIENT_KEY.startsWith('test_ck_') &&
          !CLIENT_KEY.startsWith('live_ck_')
        ) {
          throw new Error(
            `올바르지 않은 CLIENT_KEY 형식입니다. 현재: ${CLIENT_KEY.substring(0, 10)}...`,
          );
        }

        console.log('=== SDK 초기화 ===');
        const tossPayments = loadTossPayments(CLIENT_KEY);

        // 2단계: 주문 생성
        console.log('=== 2단계: 주문 생성 ===');
        const orderData = createOrderData(params);
        console.log('주문 데이터:', orderData);

        orderResult = await PaymentService.createOrder(orderData);
        console.log('주문 생성 결과:', orderResult);

        if (!orderResult) {
          throw new Error('서버 응답이 올바르지 않습니다.');
        }

        if ((orderResult.finalPrice || 0) <= 0) {
          throw new Error('결제 금액이 0원 이하입니다.');
        }

        // 3단계: 결제 주문 ID 생성 (Spring Boot 서버 요청)
        console.log('=== 3단계: 결제 주문 ID 생성 ===');
        const paymentOrderId = await getPaymentOrderIdWithRetry(
          orderResult.orderId,
        );
        console.log('생성된 paymentOrderId:', paymentOrderId);

        // 상품명 생성 (장바구니 아이템 기반)
        let orderName: string;
        if (items.length === 0) {
          orderName = '띱박스';
        } else if (items.length === 1 && items[0]) {
          orderName = items[0].name;
        } else if (items[0]) {
          // 여러 개인 경우: "첫번째상품 외 N개"
          orderName = `${items[0].name} 외 ${items.length - 1}개`;
        } else {
          orderName = '띱박스';
        }
        console.log('주문명:', orderName);

        // 4단계: 토스페이먼츠 결제 요청
        console.log('=== 4단계: 토스페이먼츠 결제 요청 ===');
        await requestTossPayment(tossPayments, {
          amount: orderResult.finalPrice,
          paymentOrderId,
          orderName,
          customerName: params.customerName,
          successUrl: `${baseUrl}/${ROUTE_PATH.PAYMENT_SUCCESS}`,
          failUrl: `${baseUrl}/${ROUTE_PATH.PAYMENT_FAIL}`,
        });
      } catch (error) {
        console.error('결제 처리 중 오류:', error);
        throw error;
      }
    },
    [createOrderData, requestTossPayment, items],
  );

  return {
    processPayment,
    createOrderData,
  };
};
