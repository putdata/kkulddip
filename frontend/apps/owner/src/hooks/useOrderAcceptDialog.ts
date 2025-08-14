import { useState } from 'react';
import type { Order } from '@/types/order';
import { useConfirmOrder } from '@/queries/order';
import { addMinutes, createKoreanTime, getTimeOptions } from '@/utils/dateUtils';

interface UseOrderAcceptDialogProps {
  order: Order;
  storeId: number;
  onOpenChange: (open: boolean) => void;
}

/**
 * OrderAcceptDialog 비즈니스 로직을 관리하는 커스텀 훅
 * 
 * @param order - 확정할 주문 정보
 * @param storeId - 매장 ID
 * @param onOpenChange - 다이얼로그 열림/닫힘 상태 변경 콜백
 * @returns 픽업 시간 선택, 주문 확정 관련 상태와 핸들러들
 */
export const useOrderAcceptDialog = ({
  order,
  storeId,
  onOpenChange,
}: UseOrderAcceptDialogProps) => {
  // 기본값: 현재 시간 + 30분
  const defaultTime = addMinutes(30);
  const [pickupTime, setPickupTime] = useState(
    createKoreanTime(defaultTime.getHours(), defaultTime.getMinutes()),
  );
  const [manualHour, setManualHour] = useState(
    defaultTime.getHours().toString().padStart(2, '0'),
  );
  const [manualMinute, setManualMinute] = useState(
    defaultTime.getMinutes().toString().padStart(2, '0'),
  );

  const confirmOrderMutation = useConfirmOrder({
    orderId: order.orderId,
    storeId,
  });

  const handleConfirm = () => {
    confirmOrderMutation.mutate(
      {
        action: 'CONFIRM',
        pickupTime,
      },
      {
        onSuccess: () => {
          onOpenChange(false);
          resetToDefault();
        },
      },
    );
  };

  const resetToDefault = () => {
    const newDefaultTime = addMinutes(30);
    setPickupTime(
      createKoreanTime(
        newDefaultTime.getHours(),
        newDefaultTime.getMinutes(),
      ),
    );
    setManualHour(newDefaultTime.getHours().toString().padStart(2, '0'));
    setManualMinute(newDefaultTime.getMinutes().toString().padStart(2, '0'));
  };

  const handleManualTimeChange = (hour: string, minute: string) => {
    const h = parseInt(hour, 10);
    const m = parseInt(minute, 10);

    if (!isNaN(h) && !isNaN(m) && h >= 0 && h < 24 && m >= 0 && m < 60) {
      const now = new Date();
      const newTime = new Date();
      newTime.setHours(h);
      newTime.setMinutes(m);
      newTime.setSeconds(0);
      newTime.setMilliseconds(0);

      // 현재 시간보다 이후인지 확인
      if (newTime > now) {
        setPickupTime(createKoreanTime(h, m));
      }
    }
  };

  const timeOptions = getTimeOptions();

  return {
    pickupTime,
    setPickupTime,
    manualHour,
    setManualHour,
    manualMinute,
    setManualMinute,
    handleConfirm,
    handleManualTimeChange,
    timeOptions,
    confirmOrderMutation,
  };
};