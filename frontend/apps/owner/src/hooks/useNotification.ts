import { VAPID_KEY, getToken, getMessagingInstance } from '@/firebase/config';
import { useNotificationStore } from 'common';

/**
 * 푸시 알림 관리 Hook
 *
 * @description
 * Firebase Cloud Messaging을 사용하여 푸시 알림을 관리합니다.
 * 알림 권한 요청과 FCM 토큰 발급 기능을 제공합니다.
 */
export const useNotification = () => {
  const { fcmToken, setFcmToken } = useNotificationStore();

  /**
   * 알림 권한 요청 및 FCM 토큰 발급
   */
  const requestPermission = async (): Promise<boolean> => {
    try {
      const permission = await Notification.requestPermission();

      if (permission !== 'granted') {
        console.log('Notification permission denied');
        return false;
      }

      // Get messaging instance
      const messaging = getMessagingInstance();
      if (!messaging) {
        console.error('Firebase Messaging not available');
        return false;
      }

      // Get FCM token
      const token = await getToken(messaging, {
        vapidKey: VAPID_KEY,
      });

      if (token) {
        setFcmToken(token);
        console.log('FCM Token obtained:', token);
        return true;
      } else {
        console.error('No FCM token received');
        return false;
      }
    } catch (error) {
      console.error('Error getting notification permission:', error);
      return false;
    }
  };

  return {
    token: fcmToken,
    requestPermission,
  };
};
