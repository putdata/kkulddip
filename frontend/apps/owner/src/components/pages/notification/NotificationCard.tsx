import { Card, CardContent } from '@/components/ui/card';
import { formatNotificationDate } from '@/utils/notificationUtils';
import NotificationTypeBadge from './NotificationTypeBadge';
import type { Notification } from '@/types/notification';

interface NotificationCardProps {
  notification: Notification;
  onClick?: (notification: Notification) => void;
}

const NotificationCard = ({ notification, onClick }: NotificationCardProps) => {
  const handleClick = () => {
    onClick?.(notification);
  };

  return (
    <Card
      className={`w-full transition-colors ${
        onClick ? 'hover:bg-muted/50 cursor-pointer' : ''
      }`}
      onClick={handleClick}
    >
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
            <h3 className="mb-2 break-words text-sm font-medium leading-relaxed">
              {notification.title}
            </h3>
            <p className="text-muted-foreground line-clamp-3 break-words text-sm leading-relaxed">
              {notification.content}
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};

export default NotificationCard;
