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
   * @param orderId - 주문 ID
   * @returns 결제 주문 ID
   */
  static async createPaymentOrderId(
    orderId: string,
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
  orderId: string,
  maxRetries: number = 10,
  delay: number = 1000,
): Promise<string> => {
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      console.log(`결제 주문 ID 요청 시도 ${attempt}/${maxRetries}`);

      const result = await PaymentService.createPaymentOrderId(orderId);
      console.log('API 응답:', result); // 전체 응답 로그 추가

      if (result.success) {
        console.log('결제 주문 ID 생성 성공:', result.body.paymentOrderId);
        return result.body.paymentOrderId;
      }

      console.log('API 응답 실패:', {
        // 실패 원인 상세 로그
        success: result.success,
        code: result.code,
        message: result.message,
      });

      // PAYMENT_NOT_FOUND 에러인 경우 재시도
      if (result.code === 'PAYMENT_NOT_FOUND' && attempt < maxRetries) {
        console.log(
          `결제 정보를 찾을 수 없습니다. ${delay}ms 후 재시도합니다.`,
        );
        await new Promise(resolve => setTimeout(resolve, delay));
        continue;
      }

      throw new Error(result.message || '알 수 없는 오류');
    } catch (error) {
      console.error(`시도 ${attempt} 실패:`, error); // 각 시도별 에러 로그
      if (attempt === maxRetries) {
        console.error('결제 주문 ID 생성 최종 실패:', error);
        throw error;
      }
    }
  }

  throw new Error('결제 주문 ID 생성 실패');
};
