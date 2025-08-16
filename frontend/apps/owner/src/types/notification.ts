/**
 * 알림 타입 정의
 */
export type NotificationType =
  | 'ORDER_COMPLETE'
  | 'DELIVERY_START'
  | 'ORDER'
  | 'REVIEW_CREATED'
  | 'SYSTEM';

/**
 * 발행자/구독자 타입
 */
export type PublisherType = 'SYSTEM' | 'OWNER' | 'CUSTOMER' | 'STORE';
export type SubscriberType = 'CUSTOMER' | 'OWNER' | 'STORE';

/**
 * 개별 알림 데이터
 */
export interface Notification {
  notificationId: number;
  title: string;
  content: string;
  publisherId: number;
  publisherType: PublisherType;
  subscriberId: number;
  subscriberType: SubscriberType;
  notificationType: NotificationType;
  actionUrl?: string;
  createdAt: string;
  sentAt?: string;
  isSent: boolean;
}

/**
 * 알림 목록 조회 API 응답
 */
export type NotificationListResponse = Notification[];

/**
 * 알림 조회 요청 파라미터
 */
export interface NotificationParams {
  subscriberId: number;
  subscriberType: SubscriberType;
  page?: number;
  size?: number;
}
