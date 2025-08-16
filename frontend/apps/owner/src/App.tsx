import { RouterProvider } from 'react-router-dom';
import { router } from '@/router/router';
import { useEffect, useState } from 'react';
import { getMessagingInstance, onMessage } from '@/firebase/config';
import SimpleBar from 'simplebar-react';
import InstallPrompt from '@/components/common/InstallPrompt';
import OfflineIndicator from '@/components/common/OfflineIndicator';
import { useInstallPrompt } from '@/hooks/useInstallPrompt';

function App() {
  const { isInstalled } = useInstallPrompt();
  const [showInstallPrompt, setShowInstallPrompt] = useState(false);

  useEffect(() => {
    // Register service worker
    if ('serviceWorker' in navigator) {
      navigator.serviceWorker
        .register('/firebase-messaging-sw.js')
        .then(registration => {
          console.log('SW registered: ', registration);
        })
        .catch(registrationError => {
          console.log('SW registration failed: ', registrationError);
        });

      // iOS PWA 알림 클릭 메시지 리스너
      navigator.serviceWorker.addEventListener('message', event => {
        if (event.data && event.data.type === 'NOTIFICATION_CLICK') {
          const targetUrl = event.data.url;
          console.log('iOS PWA notification click routing to:', targetUrl);

          // React Router를 통한 안전한 라우팅
          if (targetUrl && targetUrl !== '/') {
            window.location.href = targetUrl;
          }
        }
      });
    }

    // Handle foreground messages
    const messaging = getMessagingInstance();
    if (messaging) {
      const unsubscribe = onMessage(messaging, payload => {
        console.log('Message received in foreground: ', payload);

        // Show notification manually for foreground messages
        if (Notification.permission === 'granted') {
          new Notification(payload.notification?.title || '꿀띱 알림', {
            body: payload.notification?.body || '',
            icon: '/favicon-196x196.png',
            badge: '/favicon-96x96.png',
            data: {
              url: payload.data?.actionUrl || '/',
            },
          });
        }
      });

      return () => {
        unsubscribe();
      };
    }
  }, []);

  // PWA 설치 프롬프트 표시 타이밍 (앱 로드 후 5초)
  useEffect(() => {
    if (!isInstalled) {
      const timer = setTimeout(() => {
        setShowInstallPrompt(true);
      }, 5000);

      return () => clearTimeout(timer);
    }
  }, [isInstalled]);

  return (
    <SimpleBar className="h-dvh" autoHide={true}>
      <RouterProvider router={router} />

      {/* PWA 설치 프롬프트 */}
      {showInstallPrompt && !isInstalled && (
        <InstallPrompt onDismiss={() => setShowInstallPrompt(false)} />
      )}

      {/* 오프라인 상태 표시 */}
      <OfflineIndicator />
    </SimpleBar>
  );
}

export default App;
