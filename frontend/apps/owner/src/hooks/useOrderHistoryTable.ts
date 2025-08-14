import { useState } from 'react';
import type { Order } from '@/types/order';

/**
 * OrderHistoryTable 비즈니스 로직을 관리하는 커스텀 훅
 *
 * @param onPickupClick - 픽업 완료 버튼 클릭 시 호출할 콜백 함수
 * @returns 주문 상태 관리, 주문 정보 포맷팅 관련 상태와 핸들러들
 */
export const useOrderHistoryTable = (
  onPickupClick?: (order: Order) => void,
) => {
  const [processingIds, setProcessingIds] = useState<Set<string>>(new Set());
  const [completedIds, setCompletedIds] = useState<Set<string>>(new Set());

  const handlePickupClick = (order: Order) => {
    if (!onPickupClick) {
      return;
    }

    // 처리 시작
    setProcessingIds(prev => new Set(prev).add(order.orderId));

    // 실제 API 호출은 부모 컴포넌트에서 처리
    onPickupClick(order);

    // 2초 후 완료 상태로 변경 (API 응답 시간 예상)
    setTimeout(() => {
      setProcessingIds(prev => {
        const newSet = new Set(prev);
        newSet.delete(order.orderId);
        return newSet;
      });
      setCompletedIds(prev => new Set(prev).add(order.orderId));
    }, 2000);
  };

  const getOrderItemsText = (orderItems: Order['orderItems']) => {
    if (!orderItems || orderItems.length === 0) {
      return '상품 없음';
    }
    const firstItem = orderItems[0];
    if (!firstItem) {
      return '상품 없음';
    }
    if (orderItems.length === 1) {
      return `${firstItem.productName} x${firstItem.quantity}`;
    }
    return `${firstItem.productName} 외 ${orderItems.length - 1}건`;
  };

  const getTotalQuantity = (orderItems: Order['orderItems']) => {
    if (!orderItems || orderItems.length === 0) {
      return 0;
    }
    return orderItems.reduce((sum, item) => sum + item.quantity, 0);
  };

  return {
    processingIds,
    completedIds,
    handlePickupClick,
    getOrderItemsText,
    getTotalQuantity,
  };
};
