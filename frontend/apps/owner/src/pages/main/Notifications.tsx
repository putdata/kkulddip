import { useNotifications } from '@/queries/notification';
import {
  NotificationTable,
  NotificationTableSkeleton,
} from '@/components/pages/notification';
import { Bell } from 'lucide-react';

const Notifications = () => {
  // TODO: 사장용 전체 알림 API 연결 필요
  // 현재는 임시로 첫 번째 매장 데이터 사용 (Owner ID 필요)
  const defaultOwnerId = 1;

  const { data: notifications = [], isLoading } = useNotifications(
    defaultOwnerId,
    'OWNER',
  );

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold">사장 알림</h1>
          <p className="text-muted-foreground">
            사장님께 온 알림 내역을 확인하세요
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Bell className="text-muted-foreground h-5 w-5" />
          <span className="text-muted-foreground text-sm">
            총 {notifications.length}개
          </span>
        </div>
      </div>

      {/* 알림 목록 */}
      {isLoading ? (
        <NotificationTableSkeleton />
      ) : (
        <NotificationTable notifications={notifications} />
      )}
    </div>
  );
};

export default Notifications;
