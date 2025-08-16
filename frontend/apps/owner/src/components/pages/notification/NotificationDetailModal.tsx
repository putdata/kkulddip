import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { ExternalLink, User, Tag } from 'lucide-react';
import { formatNotificationDate } from '@/utils/notificationUtils';
import NotificationTypeBadge from './NotificationTypeBadge';
import type { Notification } from '@/types/notification';

interface NotificationDetailModalProps {
  notification: Notification | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const getPublisherTypeText = (type: string) => {
  const typeMap = {
    SYSTEM: '시스템',
    OWNER: '사장',
    CUSTOMER: '고객',
    STORE: '매장',
  };
  return typeMap[type as keyof typeof typeMap] || type;
};

const NotificationDetailModal = ({
  notification,
  open,
  onOpenChange,
}: NotificationDetailModalProps) => {
  if (!notification) {
    return null;
  }

  const handleActionClick = () => {
    if (notification.actionUrl) {
      window.open(notification.actionUrl, '_blank');
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[90vh] max-w-2xl overflow-y-auto">
        <DialogHeader className="space-y-4">
          <div className="w-full">
            <DialogTitle className="text-left text-lg leading-tight">
              {notification.title}
            </DialogTitle>
            <DialogDescription className="mt-2 text-left">
              {formatNotificationDate(
                notification.sentAt,
                notification.createdAt,
              )}
            </DialogDescription>
          </div>
        </DialogHeader>

        <Separator />

        {/* 알림 메타 정보 */}
        <div className="flex text-sm">
          <div className="flex flex-1 items-center gap-2">
            <User className="text-muted-foreground h-4 w-4" />
            <span className="text-muted-foreground">발신자:</span>
            <Badge variant="outline" className="text-xs">
              {getPublisherTypeText(notification.publisherType)}
            </Badge>
          </div>
          <div className="flex flex-1 items-center gap-2">
            <Tag className="text-muted-foreground h-4 w-4" />
            <span className="text-muted-foreground">타입:</span>
            <NotificationTypeBadge type={notification.notificationType} />
          </div>
        </div>

        <Separator />

        {/* 알림 내용 */}
        <div className="space-y-3">
          <h3 className="text-sm font-medium">알림 내용</h3>
          <div className="bg-muted/50 rounded-lg p-4">
            <p className="whitespace-pre-wrap break-words text-sm leading-relaxed">
              {notification.content}
            </p>
          </div>
        </div>

        {/* 액션 버튼 */}
        <div className="flex items-center justify-between gap-3 pt-2">
          <div className="flex-1">
            {notification.actionUrl && (
              <Button
                onClick={handleActionClick}
                className="gap-2 shadow-sm"
                variant="default"
              >
                <ExternalLink className="h-4 w-4" />
                관련 페이지로 이동
              </Button>
            )}
          </div>
          <Button
            onClick={() => onOpenChange(false)}
            variant="outline"
            className="shadow-sm"
          >
            닫기
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default NotificationDetailModal;
