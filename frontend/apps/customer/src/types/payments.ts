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
