import { useEffect } from 'react';
import { useOrderFlowStore } from '@/store/useOrderFlowStore';
import { useOrderState } from '@/hooks/useOrderState';
import { useTossPayment } from '@/hooks/useTossPayment';
import { useCartStore } from '@/store/useCartStore';
import { useCustomerProfile } from '@/hooks/useProfile';

const OrderProcessingLoader = () => {
  const { orderData } = useOrderFlowStore();
  const { handlePendingToPayment } = useOrderState();
  const { processPayment } = useTossPayment();
  const { storeInfo } = useCartStore();
  const { data: profile } = useCustomerProfile();

  useEffect(() => {
    let isCancelled = false;

    const handlePayment = async () => {
      if (isCancelled) {
        console.log('=== 중복 실행 방지: 이미 취소됨 ===');
        return;
      }

      // profile이 로드되지 않았으면 대기
      if (!profile?.customerId) {
        console.log('사용자 정보 로딩 중...');
        return;
      }

      try {
        console.log('=== 결제 처리 시작 ===');
        console.log('isCancelled 상태:', isCancelled);
        console.log('사용자 정보:', profile);

        await processPayment({
          orderItems: orderData.orderItems,
          customerId: profile!.customerId,
          storeId: storeInfo!.storeId,
          customerName: profile?.name || `고객 ${profile?.customerId || 6}`,
        });
      } catch (error) {
        if (!isCancelled) {
          console.error('결제 처리 실패:', error);
          handlePendingToPayment();
        }
      }
    };

    // 비동기 처리로 중복 방지 강화
    const timeoutId = setTimeout(() => {
      if (!isCancelled) {
        handlePayment();
      }
    }, 0);

    return () => {
      isCancelled = true;
      clearTimeout(timeoutId);
      console.log('=== 클린업 실행: isCancelled = true ===');
    };
  }, [
    profile?.customerId,
    orderData.orderItems,
    storeInfo?.storeId,
    processPayment,
    handlePendingToPayment,
  ]);

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600"></div>
        <p className="text-lg font-medium">결제를 처리하는 중...</p>
      </div>
    </div>
  );
};

export default OrderProcessingLoader;
