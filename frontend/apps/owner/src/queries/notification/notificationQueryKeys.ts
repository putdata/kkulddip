import type { SubscriberType } from '@/types/notification';

/**
 * 알림 관련 쿼리 키 생성 유틸리티
 */
export const notificationQueryKeys = {
  all: ['notifications'] as const,

  lists: () => [...notificationQueryKeys.all, 'list'] as const,

  list: (subscriberId: number, subscriberType: SubscriberType) =>
    [...notificationQueryKeys.lists(), subscriberId, subscriberType] as const,
} as const;
