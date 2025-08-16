importScripts(
  'https://www.gstatic.com/firebasejs/12.1.0/firebase-app-compat.js',
);
importScripts(
  'https://www.gstatic.com/firebasejs/12.1.0/firebase-messaging-compat.js',
);

const firebaseConfig = {
  apiKey: 'AIzaSyBnBMaY4a48G_ukeIyTQTgeRzy-2dRz2d0',
  authDomain: 'kkulddip-7c9f9.firebaseapp.com',
  projectId: 'kkulddip-7c9f9',
  storageBucket: 'kkulddip-7c9f9.firebasestorage.app',
  messagingSenderId: '613130982810',
  appId: '1:613130982810:web:301aff5b43d0e5c117d623',
  measurementId: 'G-EHL053E2NV',
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

// Handle background messages
messaging.onBackgroundMessage(payload => {
  console.log(
    '[firebase-messaging-sw.js] Received background message ',
    payload,
  );

  const notificationTitle = payload.notification?.title || '꿀띱 알림';
  const notificationOptions = {
    body: payload.notification?.body || '',
    icon: '/favicon-196x196.png',
    badge: '/favicon-96x96.png',
    tag: 'notification',
    renotify: true,
    requireInteraction: true,
    actions: [
      {
        action: 'open',
        title: '열기',
        icon: '/favicon-96x96.png',
      },
      {
        action: 'close',
        title: '닫기',
      },
    ],
    data: {
      url: payload.data?.actionUrl || '/',
    },
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

// Handle notification clicks
self.addEventListener('notificationclick', event => {
  console.log('[firebase-messaging-sw.js] Notification click received.');

  event.notification.close();

  if (event.action === 'close') {
    return;
  }

  // Get URL from notification data or default to home
  const targetUrl = event.notification.data?.url || '/';

  event.waitUntil(
    clients
      .matchAll({ type: 'window', includeUncontrolled: true })
      .then(clientList => {
        // iOS PWA 환경 체크
        const isIOSPWA =
          self.navigator.userAgent.includes('iPhone') ||
          self.navigator.userAgent.includes('iPad');

        if (clientList.length > 0) {
          // 기존 클라이언트가 있으면 포커스하고 내부에서 라우팅
          const client = clientList[0];
          client.focus();

          // 클라이언트에게 라우팅 메시지 전송 (iOS PWA 안전)
          client.postMessage({
            type: 'NOTIFICATION_CLICK',
            url: targetUrl,
          });
        } else if (isIOSPWA) {
          // iOS PWA에서는 새 창 열기 대신 홈으로만 열기
          clients.openWindow('/');
        } else {
          // Android 등 다른 플랫폼에서는 정상적으로 특정 URL로 열기
          clients.openWindow(targetUrl);
        }
      }),
  );
});
