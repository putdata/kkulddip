/**
 * 픽업 상태 타입 정의
 */
export type PickupStatusType =
  | 'CREATED'
  | 'PAYMENT_PENDING'
  | 'PAID'
  | 'AWAITING_CONFIRMATION'
  | 'CONFIRMED'
  | 'CANCELLED'
  | 'FAILED';

/**
 * 픽업 상태 상수
 */
export const PICKUP_STATUS = {
  CREATED: 'CREATED',
  CONFIRMED: 'CONFIRMED',
  PAID: 'PAID',
  PAYMENT_PENDING: 'PAYMENT_PENDING',
  CANCELLED: 'CANCELLED',
  FAILED: 'FAILED',
  AWAITING_CONFIRMATION: 'AWAITING_CONFIRMATION',
} as const satisfies Record<string, PickupStatusType>;

/**
 * 픽업 상태별 메시지
 */
export const PICKUP_STATUS_MESSAGES: Record<PickupStatusType, string> = {
  CREATED: '주문이 생성되었어요',
  CONFIRMED: '주문이 확정되었어요',
  PAID: '결제가 완료되었어요',
  CANCELLED: '픽업이 취소된 주문이에요',
  FAILED: '주문이 실패됐어요',
  AWAITING_CONFIRMATION: '주문 확정을 기다리고 있어요',
  PAYMENT_PENDING: '결제 대기중이에요',
};

/**
 * 픽업 상태별 텍스트 색상 클래스
 */
export const PICKUP_STATUS_COLORS = {
  [PICKUP_STATUS.CREATED]: 'text-gray-600',
  [PICKUP_STATUS.PAYMENT_PENDING]: 'text-blue-600',
  [PICKUP_STATUS.AWAITING_CONFIRMATION]: 'text-orange-600',
  [PICKUP_STATUS.CANCELLED]: 'text-red-600',
  [PICKUP_STATUS.CONFIRMED]: 'text-red-600',
  [PICKUP_STATUS.PAID]: 'text-red-600',
  [PICKUP_STATUS.FAILED]: 'text-red-600',
} as const;

/**
 * 픽업이 완료되었는지 확인하는 유틸리티 함수
 */
export const isPickupCompleted = (status: string): boolean => {
  return status === PICKUP_STATUS.CONFIRMED;
};
