/**
 * 주문 상태
 */
export type OrderStatus =
  | 'CREATED'
  | 'PAYMENT_PENDING'
  | 'PAID'
  | 'AWAITING_CONFIRMATION'
  | 'CONFIRMED'
  | 'CANCELLED'
  | 'FAILED';

/**
 * 주문 확정/거절 액션
 */
export type OrderAction = 'CONFIRM' | 'REJECT';

/**
 * 주문 상품 아이템
 */
export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
}

/**
 * 주문 정보
 */
export interface Order {
  orderId: string;
  customerId: number;
  storeId: number;
  orderItems: OrderItem[];
  originalPrice: number;
  orderStatus: OrderStatus;
  orderDate: string;
}

/**
 * 대기 중인 주문 목록 응답
 */
export interface PendingOrdersResponse {
  success: boolean;
  status: number;
  body: Order[];
}

/**
 * 주문 확정/거절 요청
 */
export interface ConfirmOrderRequest {
  action: OrderAction;
  rejectionReason?: string;
  pickupTime?: string;
}

/**
 * 주문 확정/거절 응답
 */
export interface ConfirmOrderResponse {
  orderId: string;
  orderStatus: string;
  pickupTime?: string;
  confirmedAt: string;
}
