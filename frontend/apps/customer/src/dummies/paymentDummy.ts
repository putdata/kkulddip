import type { ProductInfo, StoreInfo } from '@/types/payments';

/**
 * 결제 페이지 더미 상품 데이터
 */
export const dummyProductData: ProductInfo = {
  id: 1,
  name: '참치마요 김밥 세트',
  price: 4000,
  quantity: 1,
};

/**
 * 결제 페이지 더미 매장 데이터
 */
export const dummyStoreData: StoreInfo = {
  id: 1,
  name: '최고집 김치삼겹구이&김치찜',
  address: '서울시 강남구 테헤란로 123',
  pickupTime: '15-20분',
};

/**
 * 더미 할인 금액
 */
export const dummyDiscountAmount = 3000;
