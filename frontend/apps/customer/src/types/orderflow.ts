export interface CartData {
  quantity: number;
  total: number;
  productId: number;
}

export interface PaymentData {
  appliedCouponId?: string;
  discountAmount: number;
  finalAmount: number;
}

export interface OrderData {
  quantity: number;
  total: number;
  productId: number;

  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount: number;

  orderNumber: string;
  orderDate: Date;
}
