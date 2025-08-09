// orderAPI.ts - 주문 관련 API 함수들
import { ROUTE_PATH } from '@/router';
import { loadTossPayments } from '@tosspayments/payment-sdk';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// 토큰 가져오는 헬퍼 함수
const getAccessToken = (): string | null => {
  const authStorage = localStorage.getItem('auth-storage');
  if (!authStorage) {
    return null;
  }

  try {
    const authData = JSON.parse(authStorage);
    return authData.state?.accessToken || null;
  } catch {
    return null;
  }
};

// 주문 생성 데이터 인터페이스
export interface OrderData {
  customerId: number;
  storeId: number;
  orderItems: Array<{
    productId: number;
    quantity: number;
    unitPrice: number;
    discountInfos: {
      discountCode: number;
      discountAmount: number;
    }[];
  }>;
}

// 주문 생성 응답 인터페이스
export interface OrderResponse {
  success: boolean;
  body: {
    orderId: number;
    customerId: number;
    finalPrice: number;
  };
  message: string;
}

// 결제 주문 ID 응답 인터페이스
export interface PaymentOrderIdResponse {
  success: boolean;
  body: {
    paymentOrderId: string;
  };
  code: string;
  message: string;
}

/**
 * 1단계: 주문 생성 API
 */
export const createOrder = async (
  orderData: OrderData,
): Promise<OrderResponse> => {
  const accessToken = getAccessToken();

  console.log('주문 생성 요청:', orderData);

  try {
    const response = await fetch(`${API_BASE_URL}/v1/orders`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
      },
      body: JSON.stringify(orderData),
    });

    const result = await response.json();

    console.log('주문 생성 응답:', result);

    if (!result.success) {
      throw new Error(result.message || '주문 생성 실패');
    }

    return result;
  } catch (error) {
    console.error('주문 생성 오류:', error);
    throw error;
  }
};

/**
 * 2단계: 결제 주문 ID 생성 API (재시도 로직 포함)
 */
export const getPaymentOrderId = async (
  orderId: number,
  maxRetries: number = 10,
  delay: number = 1000,
): Promise<string> => {
  const accessToken = getAccessToken();

  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      console.log(`결제 주문 ID 요청 시도 ${attempt}/${maxRetries}`);

      const response = await fetch(
        `${API_BASE_URL}/v1/payments/payment-order-id`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
          },
          body: JSON.stringify({ orderId }),
        },
      );

      const result: PaymentOrderIdResponse = await response.json();

      // 404 상태코드와 PAYMENT_NOT_FOUND 에러인 경우 재시도
      if (response.status === 404 && result.code === 'PAYMENT_NOT_FOUND') {
        if (attempt < maxRetries) {
          console.log(
            `결제 정보를 찾을 수 없습니다. ${delay}ms 후 재시도합니다.`,
          );
          await new Promise(resolve => setTimeout(resolve, delay));
          continue;
        } else {
          throw new Error(
            '결제 정보를 찾을 수 없습니다. 잠시 후 다시 시도해주세요.',
          );
        }
      }

      // 다른 에러인 경우 즉시 실패
      if (!result.success) {
        throw new Error(result.message || '알 수 없는 오류');
      }

      console.log('결제 주문 ID 생성 성공:', result.body.paymentOrderId);
      return result.body.paymentOrderId;
    } catch (error) {
      if (attempt === maxRetries) {
        console.error('결제 주문 ID 생성 최종 실패:', error);
        throw error;
      }
    }
  }

  throw new Error('결제 주문 ID 생성 실패');
};

/**
 * 3단계: 토스페이먼츠 결제 요청
 */
export interface PaymentRequestData {
  amount: number;
  paymentOrderId: string;
  orderName: string;
  customerName: string;
  successUrl: string;
  failUrl: string;
}

export const requestTossPayment = async (
  tossPayments: ReturnType<typeof loadTossPayments>,
  paymentData: PaymentRequestData,
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
};

/**
 * 4단계: 결제 승인 API (기존 confirmPayment 수정)
 */
export const confirmPayment = async (
  paymentKey: string,
  orderId: string,
  amount: string | number,
): Promise<{
  success: boolean;
  body: Record<string, unknown>;
  message: string;
}> => {
  const accessToken = getAccessToken();

  console.log('=== 결제 승인 요청 시작 ===');
  console.log('결제 승인 요청 데이터:', {
    paymentKey,
    orderId,
    amount,
    accessToken: accessToken ? '토큰 있음' : '토큰 없음',
    API_BASE_URL,
  });

  try {
    const requestBody = {
      paymentKey,
      orderId,
      amount: typeof amount === 'string' ? amount : amount.toString(),
    };

    console.log('전송할 Request Body:', requestBody);

    const response = await fetch(`${API_BASE_URL}/v1/payments/confirm`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(accessToken && { Authorization: `Bearer ${accessToken}` }),
      },
      body: JSON.stringify(requestBody),
    });

    console.log('응답 상태:', response.status);

    if (!response.ok) {
      const errorText = await response.text();
      console.log('🚨 오류 응답 내용:', errorText);

      try {
        const errorData = JSON.parse(errorText);
        console.log('파싱된 오류 데이터:', errorData);

        if (response.status === 500) {
          throw new Error(
            '결제 처리 중 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.',
          );
        }

        throw new Error(errorData.message || '결제 승인 요청 실패');
      } catch {
        throw new Error(`서버 오류 (${response.status}): ${errorText}`);
      }
    }

    const result = await response.json();
    console.log('✅ 결제 승인 성공:', result);
    return result;
  } catch (error) {
    console.error('결제 승인 실패:', error);
    throw error;
  }
};

/**
 * 전체 주문 및 결제 플로우 (통합 함수)
 */
export const processCompleteOrder = async (
  tossPayments: ReturnType<typeof loadTossPayments>,
  orderData: OrderData,
  baseUrl: string = window.location.origin,
): Promise<void> => {
  try {
    // 1단계: 주문 생성
    console.log('=== 1단계: 주문 생성 ===');
    const orderResult = await createOrder(orderData);

    if (orderResult.body.finalPrice <= 0) {
      throw new Error('결제 금액이 0원 이하입니다.');
    }

    // 2단계: 결제 주문 ID 생성
    console.log('=== 2단계: 결제 주문 ID 생성 ===');
    const paymentOrderId = await getPaymentOrderId(orderResult.body.orderId);

    // 3단계: 토스페이먼츠 결제 요청
    console.log('=== 3단계: 토스페이먼츠 결제 요청 ===');
    await requestTossPayment(tossPayments, {
      amount: orderResult.body.finalPrice,
      paymentOrderId,
      orderName: `주문 #${orderResult.body.orderId}`,
      customerName: `고객 ${orderResult.body.customerId}`,
      successUrl: `${baseUrl}/${ROUTE_PATH.PAYMENT_SUCCESS}`, // React Router 경로
      failUrl: `${baseUrl}/${ROUTE_PATH.PAYMENT_FAIL}`,
    });
  } catch (error) {
    console.error('주문 처리 중 오류:', error);
    throw error;
  }
};
