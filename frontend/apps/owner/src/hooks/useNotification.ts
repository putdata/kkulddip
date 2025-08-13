import { VAPID_KEY, getToken, getMessagingInstance } from '@/firebase/config';
import { useNotificationStore } from 'common';

export const useNotification = () => {
  const { fcmToken, setFcmToken } = useNotificationStore();

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
