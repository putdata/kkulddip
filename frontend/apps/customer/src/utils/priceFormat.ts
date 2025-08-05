/**
 * 가격을 한국 원화 형식으로 포맷팅합니다.
 * @param price - 포맷팅할 가격 (옵셔널)
 * @returns 포맷팅된 가격 문자열 (예: "12,000원") 또는 빈 문자열
 */
export const formatPrice = (price?: number): string => {
  return price !== undefined ? `${price.toLocaleString('ko-KR')}원` : '';
};

/**
 * 숫자를 천 단위 콤마로 구분하여 포맷팅합니다.
 * @param num - 포맷팅할 숫자
 * @returns 포맷팅된 숫자 문자열 (예: "12,000")
 */
export const formatNumber = (num: number): string => {
  return num.toLocaleString('ko-KR');
};

/**
 * 할인율을 퍼센트 형식으로 포맷팅합니다.
 * @param original - 원가
 * @param discounted - 할인가
 * @returns 할인율 문자열 (예: "20%")
 */
export const formatDiscountRate = (
  original: number,
  discounted: number,
): string => {
  const rate = Math.round(((original - discounted) / original) * 100);
  return `${rate}%`;
};

/**
 * 가격 관련 유틸리티들을 하나의 객체로 내보냅니다.
 */
export const priceUtils = {
  formatPrice,
  formatNumber,
  formatDiscountRate,
} as const;
