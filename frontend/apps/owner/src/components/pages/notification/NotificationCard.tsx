import { Card, CardContent } from '@/components/ui/card';
import { formatNotificationDate } from '@/utils/notificationUtils';
import NotificationTypeBadge from './NotificationTypeBadge';
import type { Notification } from '@/types/notification';

interface NotificationCardProps {
  notification: Notification;
}

const NotificationCard = ({ notification }: NotificationCardProps) => {
  return (
    <Card className="w-full">
      <CardContent className="p-4">
        <div className="flex items-start justify-between gap-4">
          <div className="min-w-0 flex-1">
            <div className="mb-2 flex items-center gap-2">
              <NotificationTypeBadge type={notification.notificationType} />
              <span className="text-muted-foreground text-xs">
                {formatNotificationDate(
                  notification.sentAt,
                  notification.createdAt,
                )}
              </span>
            </div>
            <h3 className="mb-1 truncate text-sm font-medium">
              {notification.title}
            </h3>
            <p className="text-muted-foreground line-clamp-2 text-sm">
              {notification.content}
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default NotificationCard;
