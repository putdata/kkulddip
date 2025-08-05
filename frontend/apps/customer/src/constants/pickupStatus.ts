/**
 * 픽업 상태 타입 정의
 */
export type PickupStatusType =
  | 'COMPLETED'
  | 'IN_PROGRESS'
  | 'PENDING'
  | 'CANCELLED';

/**
 * 픽업 상태 상수
 */
export const PICKUP_STATUS = {
  COMPLETED: 'COMPLETED',
  IN_PROGRESS: 'IN_PROGRESS',
  PENDING: 'PENDING',
  CANCELLED: 'CANCELLED',
} as const satisfies Record<string, PickupStatusType>;

/**
 * 픽업 상태별 메시지
 */
export const PICKUP_STATUS_MESSAGES = {
  [PICKUP_STATUS.COMPLETED]: '픽업이 완료된 주문이에요',
  [PICKUP_STATUS.IN_PROGRESS]: '픽업이 진행중이에요',
  [PICKUP_STATUS.PENDING]: '픽업 대기중이에요',
  [PICKUP_STATUS.CANCELLED]: '픽업이 취소된 주문이에요',
} as const;

/**
 * 픽업 상태별 텍스트 색상 클래스
 */
export const PICKUP_STATUS_COLORS = {
  [PICKUP_STATUS.COMPLETED]: 'text-gray-600',
  [PICKUP_STATUS.IN_PROGRESS]: 'text-blue-600',
  [PICKUP_STATUS.PENDING]: 'text-orange-600',
  [PICKUP_STATUS.CANCELLED]: 'text-red-600',
} as const;

/**
 * 픽업 상태 유효성 검사
 */
const isValidPickupStatus = (status: string): status is PickupStatusType => {
  return Object.values(PICKUP_STATUS).includes(status as PickupStatusType);
};

/**
 * 픽업 상태 메시지를 반환하는 유틸리티 함수
 * @throws Error 잘못된 상태값이 전달된 경우
 */
export const getPickupStatusMessage = (status: string): string => {
  if (!isValidPickupStatus(status)) {
    throw new Error(
      `Invalid pickup status: ${status}. Please check the API response or contact support.`,
    );
  }
  return PICKUP_STATUS_MESSAGES[status];
};

/**
 * 픽업 상태별 색상 클래스를 반환하는 유틸리티 함수
 */
export const getPickupStatusColor = (status: string): string => {
  return (
    PICKUP_STATUS_COLORS[status as keyof typeof PICKUP_STATUS_COLORS] ||
    'text-gray-600'
  );
};

/**
 * 픽업이 완료되었는지 확인하는 유틸리티 함수
 */
export const isPickupCompleted = (status: string): boolean => {
  return status === PICKUP_STATUS.COMPLETED;
};
