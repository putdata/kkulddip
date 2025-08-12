import { apiClient } from 'common';

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

  await apiClient.post('/v1/fcm-tokens', tokenData);
  console.log('FCM Token registered with server');
};

export const sendTestNotification = async (): Promise<void> => {
  const testData: NotificationRequest = {
    title: '알림 테스트',
    content: '테스트 알림 내용',
    publisherId: 1001,
    publisherType: 'SYSTEM',
    subscriberId: 1,
    subscriberType: 'OWNER',
    notificationType: 'ORDER',
    actionUrl: '/orders/12345',
  };

  await apiClient.post('/v1/notifications', testData);
};
