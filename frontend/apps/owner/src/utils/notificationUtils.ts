import type { NotificationType } from '@/types/notification';

/**
 * 알림 타입을 한국어 라벨로 변환
 */
export const getNotificationTypeLabel = (type: NotificationType): string => {
  switch (type) {
    case 'ORDER_COMPLETE':
      return '주문 완료';
    case 'DELIVERY_START':
      return '배송 시작';
    case 'ORDER':
      return '주문';
    case 'REVIEW_CREATED':
      return '리뷰 등록';
    case 'SYSTEM':
      return '시스템';
    default:
      return type;
  }
};

/**
 * 알림 타입에 따른 배지 색상 변형 반환
 */
export const getNotificationTypeBadgeVariant = (type: NotificationType) => {
  switch (type) {
    case 'ORDER_COMPLETE':
      return 'default';
    case 'DELIVERY_START':
      return 'secondary';
    case 'ORDER':
      return 'outline';
    case 'REVIEW_CREATED':
      return 'default';
    case 'SYSTEM':
      return 'destructive';
    default:
      return 'outline';
  }
};

/**
 * 알림 발송 상태에 따른 배지 색상 반환
 */
export const getSentStatusBadgeVariant = (isSent: boolean) => {
  return isSent ? 'default' : 'secondary';
};

/**
 * 알림 발송 상태 라벨 반환
 */
export const getSentStatusLabel = (isSent: boolean): string => {
  return isSent ? '발송됨' : '대기중';
};

/**
 * 알림용 날짜 포맷팅 (sentAt 우선, 없으면 createdAt)
 */
export const formatNotificationDate = (
  sentAt?: string,
  createdAt?: string,
): string => {
  const dateString = sentAt || createdAt;
  if (!dateString) {
    return '-';
  }

  return new Date(dateString).toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};
