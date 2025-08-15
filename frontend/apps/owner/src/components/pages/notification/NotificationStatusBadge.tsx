import { Badge } from '@/components/ui/badge';
import {
  getSentStatusLabel,
  getSentStatusBadgeVariant,
} from '@/utils/notificationUtils';

interface NotificationStatusBadgeProps {
  isSent: boolean;
}

const NotificationStatusBadge = ({ isSent }: NotificationStatusBadgeProps) => {
  return (
    <Badge variant={getSentStatusBadgeVariant(isSent)}>
      {getSentStatusLabel(isSent)}
    </Badge>
  );
};

export default NotificationStatusBadge;
