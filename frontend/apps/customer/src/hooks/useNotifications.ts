import { useState } from 'react';
import type { Notification } from '@/types/notification';
import { dummyNotifications } from '@/dummies/notifications';

export const useNotifications = () => {
  const [notifications, setNotifications] = useState<Notification[]>(dummyNotifications);

  const unreadCount = notifications.filter(
    notification => !notification.isRead,
  ).length;

  const markAsRead = (id: string) => {
    setNotifications(prev =>
      prev.map(notification =>
        notification.id === id
          ? { ...notification, isRead: true }
          : notification,
      ),
    );
  };

  const markAllAsRead = () => {
    setNotifications(prev =>
      prev.map(notification => ({ ...notification, isRead: true })),
    );
  };

  return {
    notifications,
    unreadCount,
    markAsRead,
    markAllAsRead,
  };
};