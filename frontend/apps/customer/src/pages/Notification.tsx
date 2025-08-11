import NotificationCard from '@/components/pages/notifications/NotificationCard';
import { useNotifications } from '@/hooks/useNotifications';

const Notification = () => {
  const { notifications, markAsRead } = useNotifications();

  return (
    <div className="min-h-screen bg-white pb-14 pt-14">
      <div className="divide-y divide-gray-100">
        {notifications.length > 0 ? (
          <>
            {notifications.map(notification => (
              <NotificationCard
                key={notification.id}
                {...notification}
                onClick={() => markAsRead(notification.id)}
              />
            ))}
          </>
        ) : (
          <div className="flex flex-col items-center justify-center px-4 py-16">
            <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-gray-100">
              <svg
                className="h-8 w-8 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={1.5}
                  d="M15 17h5l-5 5v-5zM9 17h5l-5 5v-5zM21 3.6v16.8a1.8 1.8 0 01-1.8 1.8H4.8A1.8 1.8 0 013 20.4V3.6A1.8 1.8 0 014.8 1.8h14.4A1.8 1.8 0 0121 3.6z"
                />
              </svg>
            </div>
            <h3 className="mb-1 text-lg font-medium text-gray-900">
              알림이 없습니다
            </h3>
            <p className="text-center text-sm text-gray-500">
              새로운 알림이 오면 여기에 표시됩니다
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Notification;
