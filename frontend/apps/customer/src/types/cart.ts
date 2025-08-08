/**
 * 상품 정보 인터페이스
 */
export interface Product {
  id: number;
  name: string;
  price: number;
  description: string;
  image: string;
}

/**
 * 장바구니 페이지 Props 인터페이스
 */
export interface CartPageProps {
  onNext: () => void;
}

/**
 * 매장 정보 인터페이스
 */
export interface StoreInfo {
  name: string;
  pickupTime: string;
  pickupType: string;
}
