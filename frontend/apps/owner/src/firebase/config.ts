import { initializeApp } from 'firebase/app';
import {
  getMessaging,
  getToken,
  onMessage,
  type Messaging,
} from 'firebase/messaging';
import { getAnalytics } from 'firebase/analytics';

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
const app = initializeApp(firebaseConfig);

// Initialize Analytics (only in browser)
let analytics = null;
if (typeof window !== 'undefined') {
  try {
    analytics = getAnalytics(app);
  } catch (error) {
    console.warn('Analytics not available:', error);
  }
}

// VAPID Key
const VAPID_KEY =
  'BOMYKHkQFGEj9pTa5oXTBX39K2HrOYJbx0QSPB3qiWp42Q-q8rAU7o8fK7lHF7EfwfTEBAOEXH1PK5qGBUc2rME';

// Get messaging instance
const getMessagingInstance = (): Messaging | null => {
  if (typeof window !== 'undefined') {
    try {
      return getMessaging(app);
    } catch (error) {
      console.error('Error getting messaging instance:', error);
      return null;
    }
  }
  return null;
};

export { app, analytics, VAPID_KEY, getToken, onMessage, getMessagingInstance };
