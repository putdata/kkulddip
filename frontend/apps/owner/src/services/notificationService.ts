import { apiClient } from 'common';
import type {
  NotificationListResponse,
  SubscriberType,
} from '@/types/notification';
import { API_PATH } from '@/constants/api-path';

interface NotificationRequest {
  title: string;
  content: string;
  publisherId: number;
  publisherType: string;
  subscriberId: number;
  subscriberType: string;
  notificationType: string;
  actionUrl: string;
}

interface FCMTokenRequest {
  fcmToken: string;
  deviceType: 'WEB';
}

export const registerFCMToken = async (fcmToken: string): Promise<void> => {
  const tokenData: FCMTokenRequest = {
    fcmToken,
    deviceType: 'WEB',
  };

  await apiClient.post(API_PATH.FCM_TOKENS.REGISTER, tokenData);
  console.log('FCM Token registered with server');
};

export const deactivateFCMToken = async (userId: number): Promise<void> => {
  await apiClient.post(API_PATH.FCM_TOKENS.DEACTIVATE(userId));
  console.log('FCM Token deactivated for user:', userId);
};

/**
 * 알림 목록 조회
 */
export const getNotifications = async (
  subscriberId: number,
  subscriberType: SubscriberType,
  page = 0,
  size = 50,
): Promise<NotificationListResponse> => {
  const params = {
    subscriberId,
    subscriberType,
    page,
    size,
  };

  return apiClient.get<NotificationListResponse>(
    API_PATH.NOTIFICATIONS.LIST,
    params,
  );
};

export const sendTestNotification = async (
  testData?: Partial<NotificationRequest>,
): Promise<void> => {
  const defaultData: NotificationRequest = {
    title: '알림 테스트',
    content: '테스트 알림 내용',
    publisherId: 1001,
    publisherType: 'SYSTEM',
    subscriberId: 5,
    subscriberType: 'OWNER',
    notificationType: 'ORDER',
    actionUrl: '/orders/12345',
  };

  const finalData = { ...defaultData, ...testData };
  await apiClient.post(API_PATH.NOTIFICATIONS.SEND, finalData);
};
