import { useEffect } from 'react';
import { useOrderFlowStore } from '@/store/useOrderFlowStore';
import { useOrderState } from '@/hooks/useOrderState';
import { useTossPayment } from '@/hooks/useTossPayment';

const OrderProcessingLoader = () => {
  const { orderData } = useOrderFlowStore();
  const { handlePendingToPayment } = useOrderState();
  const { processPayment } = useTossPayment();

  useEffect(() => {
    const handlePayment = async () => {
      try {
        // 토스 결제 처리
        await processPayment({
          productId: orderData.productId,
          quantity: orderData.quantity,
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
