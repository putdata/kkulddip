import { useNavigate } from 'react-router-dom';
import { useFunnel } from '@/hooks/useFunnel';
import { useOrderState } from '@/hooks/useOrderState';
import OrderProcessingLoader from '@/components/pages/payment/OrderProcessingLoader/OrderProcessingLoader';
import Cart from '@/pages/Cart';
import Payment from '@/pages/Payment';
import OrderComplete from '@/pages/OrderComplete';
import { ROUTE_PATH } from '@/router';
import type { CartData } from '@/types/orderflow';

const steps = ['cart', 'payment'] as const;

const OrderFunnelContainer = () => {
  const navigate = useNavigate();

  const { Funnel, Step, nextClickHandler, prevClickHandler } = useFunnel(
    steps,
    'cart',
  );

  const {
    funnelState,
    orderData,
    completedOrderData,
    handleOrderComplete,
    handleNewOrder,
    updateOrderData,
  } = useOrderState();

  const handleNextToCart = (cartData: CartData) => {
    updateOrderData(cartData);
    nextClickHandler('payment');
  };

  const handleBackToCart = () => {
    prevClickHandler('cart');
  };

  const handleBackToHome = () => {
    navigate(ROUTE_PATH.HOME);
  };

  if (funnelState === 'active') {
    return (
      <Funnel>
        <Step name="cart">
          <Cart
            onNext={handleNextToCart}
            onBack={handleBackToHome}
            initialQuantity={orderData.quantity}
          />
        </Step>

        <Step name="payment">
          <Payment
            onNext={handleOrderComplete}
            onBack={handleBackToCart}
            orderData={orderData}
          />
        </Step>
      </Funnel>
    );
  }

  if (funnelState === 'completed') {
    return <OrderProcessingLoader />;
  }

  return (
    <OrderComplete orderData={completedOrderData!} onBack={handleNewOrder} />
  );
};

export default OrderFunnelContainer;
