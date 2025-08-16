import NotificationCard from '@/components/pages/notifications/NotificationCard';
import { useNotifications } from '@/hooks/useNotifications';

const Notification = () => {
  const { notifications, isLoading, error } = useNotifications();

  if (isLoading) {
    return (
      <div className="min-h-dvh bg-white pb-14 pt-14">
        <div className="flex items-center justify-center py-16">
          <div className="text-gray-500">로딩 중...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-dvh bg-white pb-14 pt-14">
        <div className="flex items-center justify-center py-16">
          <div className="text-red-500">
            알림을 불러오는 중 오류가 발생했습니다.
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-dvh bg-white pb-14 pt-14">
      <div className="divide-y divide-gray-100">
        {notifications.length > 0 ? (
          <>
            {notifications.map(notification => (
              <NotificationCard key={notification.id} {...notification} />
            ))}
          </>
        ) : (
          <div className="py-50 flex flex-col items-center justify-center px-4">
            <div className="text-4xl">🔔</div>
            <div className="text-center">
              <h3 className="mb-1 mt-2 text-lg font-semibold text-gray-800">
                알림이 없습니다
              </h3>
              <p className="text-sm text-gray-500">
                새로운 알림이 오면 여기에 표시됩니다
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Notification;
