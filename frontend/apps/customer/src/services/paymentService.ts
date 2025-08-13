import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  OrderData,
  OrderResponse,
  PaymentOrderIdRequest,
  PaymentOrderIdResponse,
  PaymentConfirmRequest,
  PaymentConfirmResponse,
} from '@/types/payments';

/**
 * 결제 관련 API 서비스 클래스
 */
export class PaymentService {
  /**
   * 주문 생성 API 호출
   *
   * @param orderData - 생성할 주문 정보
   * @returns 생성된 주문 정보
   */
  static async createOrder(orderData: OrderData): Promise<OrderResponse> {
    return apiClient.post<OrderResponse>(API_PATH.ORDERS, orderData);
  }

  /**
   * 결제 주문 ID 생성 API 호출
   *
   * @param orderId - 주문 ID (문자열 또는 숫자)
   * @returns 결제 주문 ID
   */
  static async createPaymentOrderId(
    orderId: string | number,
  ): Promise<PaymentOrderIdResponse> {
    const requestData: PaymentOrderIdRequest = { orderId };
    return apiClient.post<PaymentOrderIdResponse>(
      API_PATH.PAYMENT_ORDER_ID,
      requestData,
    );
  }

  /**
   * 결제 승인 API 호출
   *
   * @param paymentKey - 결제 키
   * @param orderId - 주문 ID
   * @param amount - 결제 금액
   * @returns 결제 승인 결과
   */
  static async confirmPayment(
    paymentKey: string,
    orderId: string,
    amount: string | number,
  ): Promise<PaymentConfirmResponse> {
    const requestData: PaymentConfirmRequest = {
      paymentKey,
      orderId,
      amount: typeof amount === 'string' ? amount : amount.toString(),
    };

    return apiClient.post<PaymentConfirmResponse>(
      API_PATH.PAYMENT_CONFIRM,
      requestData,
    );
  }
}

/**
 * 결제 주문 ID 생성 (재시도 로직 포함)
 *
 * @param orderId - 주문 ID
 * @param maxRetries - 최대 재시도 횟수
 * @param delay - 재시도 간격 (ms)
 * @returns 결제 주문 ID
 */
export const getPaymentOrderIdWithRetry = async (
  orderId: string | number,
  maxRetries: number = 10,
  delay: number = 1000,
): Promise<string> => {
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      console.log(`결제 주문 ID 요청 시도 ${attempt}/${maxRetries}`);

      const result = await PaymentService.createPaymentOrderId(orderId);
      console.log('createPaymentOrderId 응답:', result);

      // ApiClient가 body만 반환하므로 직접 접근
      if (result && result.paymentOrderId) {
        console.log('결제 주문 ID 생성 성공:', result.paymentOrderId);
        return result.paymentOrderId;
      }

      // 응답이 없거나 paymentOrderId가 없는 경우 재시도
      if (attempt < maxRetries) {
        console.log(
          `결제 주문 ID를 찾을 수 없습니다. ${delay}ms 후 재시도합니다.`,
        );
        await new Promise(resolve => setTimeout(resolve, delay));
        continue;
      }

      throw new Error('결제 주문 ID 생성 실패');
    } catch (error) {
      if (attempt === maxRetries) {
        console.error('결제 주문 ID 생성 최종 실패:', error);
        throw error;
      }
    }
  }

  throw new Error('결제 주문 ID 생성 실패');
};
