import { CheckCircle, Clock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import type { Order } from '@/types/order';

interface PickupStatusButtonProps {
  order: Order;
  isProcessing: boolean;
  isCompleted: boolean;
  onPickupClick?: (order: Order) => void;
}

const PickupStatusButton = ({
  order,
  isProcessing,
  isCompleted,
  onPickupClick,
}: PickupStatusButtonProps) => {
  const isConfirmed = order.orderStatus === 'CONFIRMED';
  const isPickedUp = order.orderStatus === 'PICKED_UP';

  // 서버에서 이미 픽업완료 상태이거나 로컬에서 완료 처리된 경우
  if (isPickedUp || isCompleted) {
    return (
      <div className="flex h-8 w-20 items-center justify-center gap-1 text-green-600">
        <CheckCircle className="h-4 w-4" />
        <span className="text-sm font-medium">완료됨</span>
      </div>
    );
  }

  // 처리 중인 경우
  if (isProcessing) {
    return (
      <div className="flex h-8 w-20 items-center justify-center gap-1 text-green-600">
        <Clock className="h-4 w-4 animate-spin" />
        <span className="text-sm font-medium">처리중</span>
      </div>
    );
  }

  // 픽업 가능한 상태
  if (isConfirmed && onPickupClick) {
    return (
      <Button
        size="sm"
        onClick={() => onPickupClick(order)}
        disabled={isProcessing}
        className="h-8 w-20"
      >
        픽업완료
      </Button>
    );
  }

  return (
    <div className="flex h-8 w-20 items-center justify-center">
      <span className="text-muted-foreground text-sm">-</span>
    </div>
  );
};

export default PickupStatusButton;
