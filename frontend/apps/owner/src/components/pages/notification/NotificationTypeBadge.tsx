import { Badge } from '@/components/ui/badge';
import {
  getNotificationTypeLabel,
  getNotificationTypeBadgeVariant,
} from '@/utils/notificationUtils';
import type { NotificationType } from '@/types/notification';

interface NotificationTypeBadgeProps {
  type: NotificationType;
}

const NotificationTypeBadge = ({ type }: NotificationTypeBadgeProps) => {
  return (
    <Badge variant={getNotificationTypeBadgeVariant(type)}>
      {getNotificationTypeLabel(type)}
    </Badge>
  );
};

export default NotificationTypeBadge;
