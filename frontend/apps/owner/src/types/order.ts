/**
 * 주문 상태
 */
export type OrderStatus =
  | 'CREATED'
  | 'PAYMENT_PENDING'
  | 'PAID'
  | 'AWAITING_CONFIRMATION'
  | 'CONFIRMED'
  | 'PICKED_UP'
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
  customerName?: string;
  storeId: number;
  orderItems: OrderItem[];
  originalPrice: number;
  orderStatus: OrderStatus;
  orderDate: string;
  pickupTime?: string;
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

/**
 * 주문 픽업 완료 응답
 */
export interface PickupOrderResponse {
  orderId: string;
  orderStatus: string;
  pickedUpAt: string;
}

/**
 * 주문 정보
 */
export type PendingOrdersResponse = Order[];

/**
 * 가게 주문 내역 조회 응답
 */
export type StoreOrderHistoryResponse = Order[];
