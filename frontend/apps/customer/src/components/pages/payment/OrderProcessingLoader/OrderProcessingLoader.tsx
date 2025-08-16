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
    const handlePayment = async () => {
      // TODO: 프로필 정보 store 에 저장 또는 직접 전달
      // profile이 로드되지 않았으면 대기
      if (!profile?.customerId) {
        console.log('사용자 정보 로딩 중...');
        return;
      }

      try {
        console.log('사용자 정보:', profile);

        // 토스 결제 처리
        await processPayment({
          orderItems: orderData.orderItems,
          // TODO: 실제 사용자 ID 가져와야 함
          customerId: profile!.customerId,
          storeId: storeInfo!.storeId,
        });
      } catch (error) {
        console.error('결제 처리 실패:', error);
        // 실패시 결제 페이지로 복귀
        handlePendingToPayment();
      }
    };

    // 컴포넌트 마운트 후 바로 결제 처리
    handlePayment();
  }, [orderData, processPayment, handlePendingToPayment]);

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
