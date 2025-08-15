/**
 * 금액을 원화 포맷으로 변환
 */
export const formatCurrency = (amount: number): string => {
  return `₩${amount.toLocaleString()}`;
};

/**
 * 퍼센티지를 텍스트로 변환
 */
export const formatPercentage = (rate: number): string => {
  const sign = rate > 0 ? '+' : '';
  return `${sign}${rate.toFixed(1)}%`;
};

/**
 * 년, 월을 정산 날짜 형식으로 변환
 */
export const formatSettlementDate = (year: number, month: number): string => {
  return `${year}년 ${month}월`;
};

/**
 * 정산 성장률 텍스트 반환
 */
export const getGrowthText = (rate: number): string => {
  if (rate > 0) {
    return `전월 대비 +${rate.toFixed(1)}% 증가`;
  } else if (rate < 0) {
    return `전월 대비 ${Math.abs(rate).toFixed(1)}% 감소`;
  } else {
    return '전월과 동일';
  }
};

/**
 * 월별 기간 텍스트 포맷팅 (예: "2024-01" → "2024년 1월")
 */
export const formatMonthPeriod = (period: string): string => {
  const [year, month] = period.split('-');
  return `${year}년 ${parseInt(month || '1')}월`;
};

/**
 * 평균 주문 금액 설명 텍스트
 */
export const getAverageOrderDescription = (): string => {
  return '주문당 평균 결제 금액';
};

/**
 * 이전 달 매출 설명 텍스트
 */
export const getPreviousMonthDescription = (): string => {
  return '이전 달 총 매출액';
};
