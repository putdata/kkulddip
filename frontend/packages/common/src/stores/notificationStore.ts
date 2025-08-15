import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface NotificationState {
  fcmToken: string | null;
  isTokenRegistered: boolean;
  userDisabled: boolean;
  setFcmToken: (token: string | null) => void;
  setTokenRegistered: (registered: boolean) => void;
  setUserDisabled: (disabled: boolean) => void;
  enableNotifications: () => void;
  disableNotifications: () => void;
  clearNotification: () => void;
}

export const useNotificationStore = create<NotificationState>()(
  persist(
    set => ({
      fcmToken: null,
      isTokenRegistered: false,
      userDisabled: false,
      setFcmToken: token => set({ fcmToken: token }),
      setTokenRegistered: registered => set({ isTokenRegistered: registered }),
      setUserDisabled: disabled => set({ userDisabled: disabled }),
      enableNotifications: () =>
        set({ isTokenRegistered: true, userDisabled: false }),
      disableNotifications: () =>
        set({ isTokenRegistered: false, userDisabled: true }),
      clearNotification: () =>
        set({ fcmToken: null, isTokenRegistered: false, userDisabled: false }),
    }),
    {
      name: 'notification-storage',
    },
  ),
);
