/**
 * 상품 정보 인터페이스
 */
export interface ProductInfo {
  id: number;
  name: string;
  price: number;
  quantity: number;
}

/**
 * 매장 정보 인터페이스
 */
export interface StoreInfo {
  id: number;
  name: string;
  address: string;
  pickupTime: string;
}

/**
 * 결제 페이지 Props 인터페이스
 */
export interface PaymentPageProps {
  onBack: () => void;
  onComplete: () => void;
}

/**
 * 주문 요약 컴포넌트 Props
 */
export interface OrderSummaryProps {
  productName: string;
  quantity: number;
}

/**
 * 픽업 정보 컴포넌트 Props
 */
export interface PickupInfoProps {
  storeName: string;
  address: string;
  pickupTime: string;
}

/**
 * 쿠폰 섹션 컴포넌트 Props
 */
export interface CouponSectionProps {
  discountAmount: number;
}

/**
 * 최종 가격 컴포넌트 Props
 */
export interface FinalPriceProps {
  orderAmount: number;
  discount: number;
}

/**
 * 주문 생성 요청 데이터
 */
export interface OrderData {
  customerId: number;
  storeId: number;
  pickupTime?: string;  // 픽업 시간 추가
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

/**
 * 주문 생성 응답 데이터 (ApiClient에서 body만 추출 후 반환)
 */
export interface OrderResponse {
  orderId: string;
  customerId: number;
  storeId: number;
  originalPrice: number;
  finalPrice: number;
  orderStatus: string;
  orderDate: string;
}

/**
 * 결제 주문 ID 요청 데이터
 */
export interface PaymentOrderIdRequest {
  orderId: string | number;
}

/**
 * 결제 주문 ID 응답 데이터 (ApiClient에서 body만 추출 후 반환)
 */
export interface PaymentOrderIdResponse {
  paymentOrderId: string;
}

/**
 * 결제 승인 요청 데이터
 */
export interface PaymentConfirmRequest {
  paymentKey: string;
  orderId: string;
  amount: string;
}

/**
 * 결제 승인 응답 데이터
 */
export interface PaymentConfirmResponse {
  success: boolean;
  body: Record<string, unknown>;
  message: string;
}

/**
 * 토스페이먼츠 결제 요청 데이터
 */
export interface TossPaymentRequest {
  amount: number;
  paymentOrderId: string;
  orderName: string;
  customerName: string;
  successUrl: string;
  failUrl: string;
}

/**
 * useTossPayment 훅 매개변수
 */
export interface TossPaymentParams {
  productId?: number;
  quantity: number;
  customerId?: number;
  storeId?: number;
  baseUrl?: string;
}
