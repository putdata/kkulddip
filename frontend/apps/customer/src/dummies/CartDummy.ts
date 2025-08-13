import type { Product, StoreInfo } from '@/types/cart';

/**
 * 더미 상품 데이터
 */
export const dummyCartProduct: Product = {
  id: 1,
  name: '[으뜸] 김치삼겹구이',
  price: 29900,
  description: '김치와 삼겹살이 어우러진 최고의 맛',
  image:
    "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 48 48'%3E%3Crect width='48' height='48' fill='%23ff6b35'/%3E%3Cpath d='M12 16h24v16c0 2-2 4-4 4H16c-2 0-4-2-4-4V16z' fill='%23fff'/%3E%3Crect x='16' y='20' width='16' height='2' fill='%23ff6b35'/%3E%3Crect x='16' y='24' width='12' height='2' fill='%23ff6b35'/%3E%3C/svg%3E",
};

/**
 * 더미 매장 정보
 */
export const dummyStoreInfo: StoreInfo = {
  name: '최고집 김치삼겹구이&김치찜',
  pickupTime: '15-20분',
  pickupType: '매장 픽업',
};
