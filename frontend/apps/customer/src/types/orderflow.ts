export interface CartData {
  total: number;
  orderItems: {
    productId: number; // ddipboxId
    quantity: number;
    unitPrice: number; // price
    discountInfos?: {
      discountCode: number;
      discountAmount: number;
    }[];
  }[];
}

export interface PaymentData {
  appliedCouponId?: string;
  discountAmount: number;
  finalAmount: number;
}

export interface OrderData {
  total: number;
  orderItems: {
    productId: number;
    quantity: number;
    unitPrice: number;
    discountInfos?: {
      discountCode: number;
      discountAmount: number;
    }[];
  }[];

  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount: number;
  orderNumber: string;
  orderDate: Date;
}
