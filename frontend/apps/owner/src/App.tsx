import { RouterProvider } from 'react-router-dom';
import { router } from '@/router/router';
import { useEffect } from 'react';
import { getMessagingInstance, onMessage } from '@/firebase/config';

function App() {
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
            icon: '/vite.svg',
          });
        }
      });

      return () => {
        unsubscribe();
      };
    }
  }, []);

  return <RouterProvider router={router} />;
}

export default App;
