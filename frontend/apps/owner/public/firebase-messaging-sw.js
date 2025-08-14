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
    icon: '/vite.svg',
    badge: '/vite.svg',
    tag: 'notification',
    renotify: true,
    requireInteraction: true,
    actions: [
      {
        action: 'open',
        title: '열기',
        icon: '/vite.svg',
      },
      {
        action: 'close',
        title: '닫기',
      },
    ],
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

// Handle notification clicks
self.addEventListener('notificationclick', event => {
  console.log('[firebase-messaging-sw.js] Notification click received.');

  event.notification.close();

  if (event.action === 'open') {
    // Open the app
    event.waitUntil(clients.openWindow('/'));
  }
});
