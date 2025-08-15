import type { Notification } from '@/types/notification';

/**
 * NotificationTable 비즈니스 로직을 관리하는 커스텀 훅
 *
 * @param onActionClick - 알림 액션 버튼 클릭 시 호출할 콜백 함수
 * @returns 알림 처리 관련 상태와 핸들러들
 */
interface UseNotificationTableProps {
  onActionClick?: (notification: Notification) => void;
}

export const useNotificationTable = ({
  onActionClick,
}: UseNotificationTableProps = {}) => {
  const handleActionClick = (notification: Notification) => {
    if (!notification.actionUrl || !onActionClick) {
      return;
    }

    onActionClick(notification);
  };

  return {
    handleActionClick,
  };
};
