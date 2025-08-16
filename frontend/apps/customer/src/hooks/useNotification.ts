import { useEffect, useCallback } from 'react';
import { VAPID_KEY, getToken, getMessagingInstance } from '@/firebase/config';
import { useNotificationStore } from 'common';
import { registerFCMToken } from '@/services/notificationService';

/**
 * 푸시 알림 관리 Hook
 *
 * @description
 * Firebase Cloud Messaging을 사용하여 푸시 알림을 관리합니다.
 * 알림 권한 요청과 FCM 토큰 발급 기능을 제공합니다.
 */
export const useNotification = () => {
  const {
    fcmToken,
    isTokenRegistered,
    userDisabled,
    setFcmToken,
    setTokenRegistered,
  } = useNotificationStore();

  /**
   * FCM 토큰을 서버에 자동 등록 (useCallback으로 메모이제이션)
   */
  const registerTokenToServer = useCallback(
    async (token: string) => {
      if (isTokenRegistered) {
        return true;
      }

      try {
        await registerFCMToken(token);
        setTokenRegistered(true);
        console.log('FCM Token registered to server');
        return true;
      } catch (error) {
        console.error('Failed to register FCM token to server:', error);
        return false;
      }
    },
    [isTokenRegistered, setTokenRegistered],
  );

  /**
   * 앱 로드 시 기존 권한 확인 및 토큰 자동 등록/복구
   */
  useEffect(() => {
    const initializeNotification = async () => {
      // 권한이 있는데 토큰이 없는 경우 - 토큰 복구 시도
      if (Notification.permission === 'granted' && !fcmToken) {
        try {
          const messaging = getMessagingInstance();
          if (messaging) {
            const token = await getToken(messaging, { vapidKey: VAPID_KEY });
            if (token) {
              console.log('FCM token recovered:', token);
              setFcmToken(token);
              // 사용자가 비활성화하지 않았다면 자동 등록
              if (!userDisabled) {
                await registerTokenToServer(token);
              }
            }
          }
        } catch (error) {
          console.error('Error recovering FCM token:', error);
        }
      }
      // 토큰은 있지만 등록되지 않은 경우 - 등록 시도
      else if (
        fcmToken &&
        !isTokenRegistered &&
        !userDisabled &&
        Notification.permission === 'granted'
      ) {
        console.log('Registering existing FCM token to server');
        await registerTokenToServer(fcmToken);
      }
      // 토큰 유효성 검증 (필요시에만)
      else if (
        fcmToken &&
        isTokenRegistered &&
        !userDisabled &&
        Math.random() < 0.1
      ) {
        // 10% 확률로만 토큰 유효성 검증 (성능 최적화)
        try {
          const messaging = getMessagingInstance();
          if (messaging) {
            const currentToken = await getToken(messaging, {
              vapidKey: VAPID_KEY,
            });
            if (currentToken && currentToken !== fcmToken) {
              console.log('FCM token changed, updating:', currentToken);
              setFcmToken(currentToken);
              await registerTokenToServer(currentToken);
            }
          }
        } catch (error) {
          console.error('Error validating FCM token:', error);
        }
      }
    };

    initializeNotification();
  }, [
    fcmToken,
    isTokenRegistered,
    userDisabled,
    setFcmToken,
    registerTokenToServer,
  ]);

  /**
   * 앱 포커스 시 토큰 갱신 확인 (Firebase v9+ 대응)
   */
  useEffect(() => {
    const checkTokenRefresh = async () => {
      const messaging = getMessagingInstance();
      if (!messaging) {
        return;
      }

      try {
        const newToken = await getToken(messaging, { vapidKey: VAPID_KEY });
        const currentStore = useNotificationStore.getState();

        if (newToken && newToken !== currentStore.fcmToken) {
          console.log('FCM token updated:', newToken);
          currentStore.setFcmToken(newToken);

          // 사용자가 알림을 활성화한 상태라면 새 토큰을 서버에 등록
          if (currentStore.isTokenRegistered && !currentStore.userDisabled) {
            await registerFCMToken(newToken);
            console.log('Updated FCM Token registered to server');
          }
        }
      } catch (error) {
        console.error('Error checking token refresh:', error);
      }
    };

    // 앱이 포커스될 때 토큰 확인
    const handleFocus = () => {
      checkTokenRefresh();
    };

    window.addEventListener('focus', handleFocus);
    return () => {
      window.removeEventListener('focus', handleFocus);
    };
  }, []); // dependency 없음으로 한 번만 등록

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
        const registered = await registerTokenToServer(token);
        console.log('FCM Token obtained:', token);
        return registered;
      } else {
        console.error('No FCM token received');
        return false;
      }
    } catch (error) {
      console.error('Error getting notification permission:', error);
      return false;
    }
  };

  /**
   * 알림 구독 해제
   */
  const unsubscribe = useCallback(() => {
    setTokenRegistered(false);
    // Note: FCM 토큰 자체는 유지하되, 서버 등록 상태만 해제
    // 실제 서버에서 토큰을 제거하는 API가 있다면 여기서 호출
  }, [setTokenRegistered]);

  return {
    token: fcmToken,
    isRegistered: isTokenRegistered,
    requestPermission,
    unsubscribe,
  };
};
