/**
 * 할인 타입 상수
 */
export const DISCOUNT_TYPES = {
  MEMBERSHIP: 'MEMBERSHIP',
  IMMEDIATE: 'IMMEDIATE',
  COUPON: 'COUPON',
} as const;

/**
 * 할인 타입별 표시명
 */
export const DISCOUNT_DISPLAY_NAMES: { [key: string]: string } = {
  [DISCOUNT_TYPES.MEMBERSHIP]: '꿀띱클럽 할인',
  [DISCOUNT_TYPES.IMMEDIATE]: '즉시할인',
  [DISCOUNT_TYPES.COUPON]: '쿠폰할인',
};

/**
 * 할인 타입의 표시명을 반환하는 유틸리티 함수
 */
export const getDiscountDisplayName = (discountType: string): string => {
  return DISCOUNT_DISPLAY_NAMES[discountType] || discountType;
};
