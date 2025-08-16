import { apiClient } from 'common';
import type { Notification } from '@/types/notification';
import { API_PATH } from '@/constants/api-path';

export interface NotificationResponse {
  notificationId: number;
  title: string;
  content: string;
  notificationType: string;
  createdAt: string;
}

const transformNotification = (
  apiNotification: NotificationResponse,
): Notification => ({
  id: apiNotification.notificationId,
  title: apiNotification.title,
  message: apiNotification.content,
  type: parseType(apiNotification.notificationType),
  createdAt: apiNotification.createdAt,
});

const parseType = (apiType: string): Notification['type'] => {
  switch (apiType.toLowerCase()) {
    case 'order':
      return 'order';
    case 'event':
      return 'event';
    case 'marketing':
      return 'marketing';
    case 'system':
      return 'system';
    case 'review':
      return 'review';
    case 'pickup':
      return 'pickup';
    default:
      return 'system';
  }
};

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

export const getNotifications = async (
  userId: number,
): Promise<Notification[]> => {
  const response = await apiClient.get<NotificationResponse[]>(
    API_PATH.NOTIFICATIONS,
    {
      subscriberId: userId,
      subscriberType: 'CUSTOMER',
    },
  );

  if (!Array.isArray(response)) {
    return [];
  }

  return response.map(transformNotification);
};
