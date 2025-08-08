import { loadTossPayments } from '@tosspayments/payment-sdk';

// 토스페이먼츠 클라이언트 키
const CLIENT_KEY = import.meta.env.VITE_TOSS_PAYMENTS_CLIENT_KEY;

/**
 * 토스페이먼츠 결제 요청 데이터 인터페이스
 */
export interface PaymentRequest {
  amount: number;
  orderId: string;
  orderName: string;
  customerName?: string;
  customerEmail?: string;
  successUrl: string;
  failUrl: string;
}

/**
 * 토스페이먼츠 SDK 초기화 및 결제 요청
 */
export const requestTossPayment = async (paymentData: PaymentRequest) => {
  try {
    const tossPayments = await loadTossPayments(CLIENT_KEY);

    tossPayments.requestPayment('카드', {
      amount: paymentData.amount,
      orderId: paymentData.orderId,
      orderName: paymentData.orderName,
      customerName: paymentData.customerName,
      customerEmail: paymentData.customerEmail,
      successUrl: paymentData.successUrl,
      failUrl: paymentData.failUrl,
    });
  } catch (error) {
    console.error('토스페이먼츠 결제 요청 실패:', error);
    throw error;
  }
};

/**
 * 결제 승인 API 호출 (백엔드 API 연동)
 */
export const confirmPayment = async (
  paymentKey: string,
  orderId: string,
  amount: number,
) => {
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

  try {
    const response = await fetch(`${API_BASE_URL}/payments/confirm`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        paymentKey,
        orderId,
        amount,
      }),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || '결제 승인 요청 실패');
    }

    return await response.json();
  } catch (error) {
    console.error('결제 승인 실패:', error);
    throw error;
  }
};
