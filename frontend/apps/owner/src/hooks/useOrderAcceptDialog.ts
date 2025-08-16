import { useState } from 'react';
import type { Order } from '@/types/order';
import { useConfirmOrder } from '@/queries/order';
import {
  addMinutes,
  createKoreanTime,
  getTimeOptions,
} from '@/utils/dateUtils';

interface UseOrderAcceptDialogProps {
  order: Order;
  storeId: number;
  onOpenChange: (open: boolean) => void;
}

/**
 * OrderAcceptDialog 비즈니스 로직을 관리하는 커스텀 훅
 *
 * @description
 * 주문 확정 다이얼로그의 상태와 로직을 관리합니다.
 * 픽업 시간 설정 및 주문 확정 처리를 담당합니다.
 * @param onOpenChange - 다이얼로그 열림/닫힘 상태 변경 콜백
 * @returns 픽업 시간 선택, 주문 확정 관련 상태와 핸들러들
 */
export const useOrderAcceptDialog = ({
  order,
  storeId,
  onOpenChange,
}: UseOrderAcceptDialogProps) => {
  const timeOptions = getTimeOptions();

  // 기본값: 첫 번째 옵션을 사용하거나 현재 시간 + 30분
  const getDefaultTime = () => {
    if (timeOptions.length > 0) {
      return timeOptions[0]!.value;
    }
    const defaultTime = addMinutes(30);
    return createKoreanTime(defaultTime.getHours(), defaultTime.getMinutes());
  };

  const defaultTimeValue = getDefaultTime();
  const defaultDate = new Date(defaultTimeValue);

  const [pickupTime, setPickupTime] = useState(defaultTimeValue);
  const [manualHour, setManualHour] = useState(
    defaultDate.getHours().toString().padStart(2, '0'),
  );
  const [manualMinute, setManualMinute] = useState(
    defaultDate.getMinutes().toString().padStart(2, '0'),
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
    const newTimeOptions = getTimeOptions();
    const newDefaultTimeValue =
      newTimeOptions.length > 0
        ? newTimeOptions[0]!.value
        : createKoreanTime(
            addMinutes(30).getHours(),
            addMinutes(30).getMinutes(),
          );

    const newDefaultDate = new Date(newDefaultTimeValue);
    setPickupTime(newDefaultTimeValue);
    setManualHour(newDefaultDate.getHours().toString().padStart(2, '0'));
    setManualMinute(newDefaultDate.getMinutes().toString().padStart(2, '0'));
  };

  const handleManualTimeChange = (hour: string, minute: string) => {
    // 빈 값이거나 유효하지 않은 값이면 그냥 반환
    if (!hour || !minute) {
      return;
    }

    const h = parseInt(hour, 10);
    const m = parseInt(minute, 10);

    // 유효한 시간 범위인지 확인
    if (!isNaN(h) && !isNaN(m) && h >= 0 && h < 24 && m >= 0 && m < 60) {
      // 픽업 시간을 즉시 업데이트 (현재 시간 체크 제거)
      setPickupTime(createKoreanTime(h, m));
    }
  };

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
