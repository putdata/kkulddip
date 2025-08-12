importScripts(
  'https://www.gstatic.com/firebasejs/12.1.0/firebase-app-compat.js',
);
importScripts(
  'https://www.gstatic.com/firebasejs/12.1.0/firebase-messaging-compat.js',
);

const firebaseConfig = {
  apiKey: "AIzaSyCYm9D-lJ9NqYLwjhXI5gf8EzQIBAIjjgQ",
  authDomain: "kkulddip.firebaseapp.com",
  projectId: "kkulddip",
  storageBucket: "kkulddip.firebasestorage.app",
  messagingSenderId: "1042547093901",
  appId: "1:1042547093901:web:04df7f2a4ce9fe6a03d568",
  measurementId: "G-RZT83D9CPS",
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