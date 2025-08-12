import { initializeApp } from 'firebase/app';
import {
  getMessaging,
  getToken,
  onMessage,
  type Messaging,
} from 'firebase/messaging';
import { getAnalytics } from 'firebase/analytics';

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
const VAPID_KEY = "BOMYKHkQFGEj9pTa5oXTBX39K2HrOYJbx0QSPB3qiWp42Q-q8rAU7o8fK7lHF7EfwfTEBAOEXH1PK5qGBUc2rME";

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
