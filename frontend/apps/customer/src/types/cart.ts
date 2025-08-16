/**
 * 상품 정보 인터페이스
 */
export interface CartItem {
  ddipboxId: number;
  name: string;
  price: number;
  description: string;
  quantity: number;
  // DdipBox에서 추가로 필요한 정보들
  discountRate: number;
  storeId: number;
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
  storeId: number;
  name: string;
  pickupTime: string;
  address: string;
  storeImageUrl: string;
}

/**
 * 장바구니 추가 결과 타입
 */
export type AddToCartResult =
  | { success: true; message: string; action: 'added' | 'updated' }
  | {
      success: false;
      message: string;
      requiresConfirmation: boolean;
      conflictStoreInfo?: StoreInfo;
    };
