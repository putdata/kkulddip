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
