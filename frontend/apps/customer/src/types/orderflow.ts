export interface CartData {
  quantity: number;
  total: number;
  productId: number; // 일관되게 required로 통일
}

export interface PaymentData {
  paymentMethod: string;
  appliedCouponId?: string;
  discountAmount: number;
  finalAmount: number;
}

export interface OrderData {
  // Cart에서 오는 데이터
  quantity: number;
  total: number;
  productId: number; // 일관되게 required로 통일

  // Payment에서 오는 데이터
  paymentMethod?: string;
  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount?: number;

  // Complete에서 생성되는 데이터
  orderNumber?: string;
  orderDate?: Date;
}
