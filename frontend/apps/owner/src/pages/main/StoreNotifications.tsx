import { useStoreSelection } from '@/hooks/useStoreSelection';
import { useNotifications } from '@/queries/notification';
import {
  NotificationTable,
  NotificationTableSkeleton,
} from '@/components/pages/notification';
import { Bell } from 'lucide-react';

const StoreNotifications = () => {
  const { storeId } = useStoreSelection();

  const { data: notifications = [], isLoading } = useNotifications(
    Number(storeId),
    'STORE',
  );

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex items-center justify-between px-4">
        <div>
          <h1 className="text-2xl font-bold">매장 알림</h1>
          <p className="text-muted-foreground">
            매장에 온 알림 내역을 확인하세요
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

export default StoreNotifications;
