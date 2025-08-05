/**
 * 결제 페이지 관련 상수
 */
export const PAYMENT_MESSAGES = {
  PICKUP_NOTICE: '주문 완료 후 15-20분 후 픽업 가능합니다.',
  CANCEL_NOTICE: '결제 완료 후 취소/변경이 어려울 수 있습니다.',
  PAYMENT_BUTTON: '결제하기',
} as const;

/**
 * 결제 수단 정보
 */
export const PAYMENT_METHOD = {
  TOSS_PAY: {
    name: '토스페이',
    icon: '₩',
  },
} as const;
