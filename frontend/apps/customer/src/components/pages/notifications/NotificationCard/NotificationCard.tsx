import { cn } from '@/lib/utils';

export interface NotificationCardProps {
  id: string;
  title: string;
  message: string;
  type: 'order' | 'promotion' | 'system' | 'review' | 'pickup';
  isRead: boolean;
  createdAt: string;
  onClick?: () => void;
}

const NotificationCard = ({
  title,
  message,
  type,
  isRead,
  createdAt,
  onClick,
}: NotificationCardProps) => {
  const getTypeColor = (type: string) => {
    switch (type) {
      case 'order':
        return 'bg-blue-100 text-blue-800';
      case 'promotion':
        return 'bg-green-100 text-green-800';
      case 'system':
        return 'bg-gray-100 text-gray-800';
      case 'review':
        return 'bg-purple-100 text-purple-800';
      case 'pickup':
        return 'bg-yellow-100 text-yellow-800';
    }
  };

  const getTypeText = (type: string) => {
    switch (type) {
      case 'order':
        return '주문';
      case 'promotion':
        return '프로모션';
      case 'system':
        return '시스템';
      case 'review':
        return '리뷰';
      case 'pickup':
        return '픽업';
    }
  };

  return (
    <div
      className={cn(
        'cursor-pointer border-b border-gray-100 px-4 py-3 transition-colors hover:bg-gray-50',
        !isRead && 'bg-blue-50/50',
      )}
      onClick={onClick}
    >
      <div className="flex items-start gap-3">
        <div className="min-w-0 flex-1">
          <div className="mb-1 flex items-center gap-2">
            <span
              className={cn(
                'rounded-full px-1.5 py-0.5 text-xs font-medium',
                getTypeColor(type),
              )}
            >
              {getTypeText(type)}
            </span>
            {!isRead && (
              <div className="h-1.5 w-1.5 flex-shrink-0 rounded-full bg-blue-500" />
            )}
          </div>
          <h4
            className={cn(
              'mb-0.5 text-sm font-medium text-gray-900',
              !isRead && 'font-semibold',
            )}
          >
            {title}
          </h4>
          <p className="text-xs leading-normal text-gray-600">{message}</p>
          <p className="mt-1 text-xs text-gray-400">{createdAt}</p>
        </div>
      </div>
    </div>
  );
};

export default NotificationCard;
