export interface CartData {
  quantity: number;
  total: number;
  productId: number;
}

export interface PaymentData {
  paymentMethod: string;
  appliedCouponId?: string;
  discountAmount: number;
  finalAmount: number;
}

export interface OrderData {
  quantity: number;
  total: number;
  productId: number;

  paymentMethod?: string;
  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount: number;

  orderNumber: string;
  orderDate: Date;
}
