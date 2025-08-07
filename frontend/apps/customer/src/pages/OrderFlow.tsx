import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useOrderState } from '@/hooks/useOrderState';
import OrderProcessingLoader from '@/components/pages/payment/OrderProcessingLoader/OrderProcessingLoader';
import Cart from '@/pages/Cart';
import Payment from '@/pages/Payment';
import OrderComplete from '@/pages/OrderComplete';
import { ROUTE_PATH } from '@/router';

const OrderFunnelContainer = () => {
  const navigate = useNavigate();
  const {
    Funnel,
    Step,
    currentStep,
    orderData,
    completedOrderData,
    isCompleted,
    handleNextToPayment,
    handleNextToPending,
    handleBackToCart,
    handleBackToHome,
    handleNewOrder,
    initializeFromSession,
  } = useOrderState();

  // 세션에서 완료된 주문 복원
  useEffect(() => {
    try {
      initializeFromSession();

      // 완료된 주문 + Cart 스텝이면 재진입으로 판단
      if (isCompleted && currentStep === 'cart') {
        navigate(ROUTE_PATH.HOME, { replace: true });
      }
    } catch {
      navigate(ROUTE_PATH.HOME, { replace: true });
    }
  }, [initializeFromSession, isCompleted, currentStep, navigate]);

  return (
    <Funnel>
      <Step name="cart">
        <Cart
          onNext={handleNextToPayment}
          onBack={handleBackToHome}
          initialQuantity={orderData.quantity}
        />
      </Step>

      <Step name="payment">
        <Payment
          onNext={handleNextToPending}
          onBack={handleBackToCart}
          orderData={orderData}
        />
      </Step>

      <Step name="pending">
        <OrderProcessingLoader />
      </Step>

      <Step name="complete">
        <OrderComplete
          orderData={completedOrderData || orderData}
          onBack={handleNewOrder}
        />
      </Step>
    </Funnel>
  );
};

export default OrderFunnelContainer;
