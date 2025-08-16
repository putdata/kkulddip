import NotificationCard from './NotificationCard';
import EmptyNotifications from './EmptyNotifications';
import type { Notification } from '@/types/notification';

interface NotificationListProps {
  notifications: Notification[];
}

const NotificationList = ({ notifications }: NotificationListProps) => {
  if (notifications.length === 0) {
    return <EmptyNotifications />;
  }

  return (
    <div className="space-y-3">
      {notifications.map(notification => (
        <NotificationCard
          key={notification.notificationId}
          notification={notification}
        />
      ))}
    </div>
  );
};

export default NotificationList;
