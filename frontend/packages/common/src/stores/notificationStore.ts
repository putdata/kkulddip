import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface NotificationState {
  fcmToken: string | null;
  isTokenRegistered: boolean;
  setFcmToken: (token: string | null) => void;
  setTokenRegistered: (registered: boolean) => void;
  clearNotification: () => void;
}

export const useNotificationStore = create<NotificationState>()(
  persist(
    set => ({
      fcmToken: null,
      isTokenRegistered: false,
      setFcmToken: token => set({ fcmToken: token }),
      setTokenRegistered: registered => set({ isTokenRegistered: registered }),
      clearNotification: () =>
        set({ fcmToken: null, isTokenRegistered: false }),
    }),
    {
      name: 'notification-storage',
    },
  ),
);
