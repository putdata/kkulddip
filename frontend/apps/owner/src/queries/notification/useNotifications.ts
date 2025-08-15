import { useQuery } from '@tanstack/react-query';
import { getNotifications } from '@/services/notificationService';
import { notificationQueryKeys } from './notificationQueryKeys';
import type {
  NotificationListResponse,
  SubscriberType,
} from '@/types/notification';

/**
 * 알림 목록을 조회하는 쿼리 훅
 *
 * @param subscriberId - 구독자 ID (Owner ID 또는 Store ID)
 * @param subscriberType - 구독자 타입 ('OWNER' 또는  'STORE')
 * @returns 알림 목록 쿼리 객체
 */
export const useNotifications = (
  subscriberId: number,
  subscriberType: SubscriberType,
) => {
  return useQuery<NotificationListResponse>({
    queryKey: notificationQueryKeys.list(subscriberId, subscriberType),
    queryFn: () => getNotifications(subscriberId, subscriberType),
    enabled: subscriberId > 0,
  });
};
