/**
 * 주문 음식 아이템 인터페이스
 */
export interface OrderFoodItem {
  orderId: string;
  storeId: number;
  storeName: string;
  orderItems: Array<{
    productId: number;
    productName: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
  }>;
  originalPrice: number;
  finalPrice: number;
  orderStatus: string;
  orderDate: string;
  pickupTime: string;
  hasReview: boolean; // 추가
}

/**
 * 주문 카드 컴포넌트 Props 인터페이스
 */
export interface OrderCardProps {
  item: OrderFoodItem;
}
