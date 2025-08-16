import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { formatNotificationDate } from '@/utils/notificationUtils';
import NotificationTypeBadge from './NotificationTypeBadge';
import EmptyNotifications from './EmptyNotifications';
import type { Notification } from '@/types/notification';

interface NotificationTableProps {
  notifications: Notification[];
}

const NotificationTable = ({ notifications }: NotificationTableProps) => {
  if (notifications.length === 0) {
    return <EmptyNotifications />;
  }

  return (
    <div className="w-full overflow-auto">
      <div className="min-w-[320px] rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="text-center">타입</TableHead>
              <TableHead className="text-center">제목</TableHead>
              <TableHead className="hidden text-center sm:table-cell">
                내용
              </TableHead>
              <TableHead className="text-center">발송일</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {notifications.map(notification => (
              <TableRow key={notification.notificationId}>
                <TableCell className="text-center">
                  <NotificationTypeBadge type={notification.notificationType} />
                </TableCell>
                <TableCell className="text-center font-medium">
                  <div className="max-w-[150px] truncate">
                    {notification.title}
                  </div>
                </TableCell>
                <TableCell className="hidden text-center sm:table-cell">
                  <div className="max-w-[200px] truncate text-sm">
                    {notification.content}
                  </div>
                </TableCell>
                <TableCell className="text-muted-foreground text-center text-sm">
                  {formatNotificationDate(
                    notification.sentAt,
                    notification.createdAt,
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};

export default NotificationTable;
