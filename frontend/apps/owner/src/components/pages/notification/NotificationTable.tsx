import { useState } from 'react';
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
import NotificationDetailModal from './NotificationDetailModal';
import EmptyNotifications from './EmptyNotifications';
import type { Notification } from '@/types/notification';

interface NotificationTableProps {
  notifications: Notification[];
}

const NotificationTable = ({ notifications }: NotificationTableProps) => {
  const [selectedNotification, setSelectedNotification] =
    useState<Notification | null>(null);
  const [modalOpen, setModalOpen] = useState(false);

  const handleNotificationClick = (notification: Notification) => {
    setSelectedNotification(notification);
    setModalOpen(true);
  };

  if (notifications.length === 0) {
    return <EmptyNotifications />;
  }

  return (
    <>
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
                <TableRow
                  key={notification.notificationId}
                  onClick={() => handleNotificationClick(notification)}
                  className="hover:bg-muted/50 cursor-pointer transition-colors"
                >
                  <TableCell className="text-center">
                    <NotificationTypeBadge
                      type={notification.notificationType}
                    />
                  </TableCell>
                  <TableCell className="text-center font-medium">
                    <div className="min-w-0 px-2">
                      <p className="break-words text-sm leading-relaxed">
                        {notification.title}
                      </p>
                    </div>
                  </TableCell>
                  <TableCell className="hidden text-center sm:table-cell">
                    <div className="min-w-0 px-2">
                      <p className="text-muted-foreground line-clamp-2 break-words text-sm leading-relaxed">
                        {notification.content}
                      </p>
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

      <NotificationDetailModal
        notification={selectedNotification}
        open={modalOpen}
        onOpenChange={setModalOpen}
      />
    </>
  );
};

export default NotificationTable;
