import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useFunnel } from '@/hooks/useFunnel';
import Cart from '@/pages/Cart';
import Payment from '@/pages/Payment';
import OrderComplete from '@/pages/OrderComplete';
import { ROUTE_PATH } from '@/router';

const steps = ['cart', 'payment', 'complete'] as const;

interface CartData {
  quantity: number;
  total: number;
  productId: number;
}

interface PaymentData {
  paymentMethod: string;
  appliedCouponId?: string;
  discountAmount: number;
  finalAmount: number;
}

interface OrderData {
  quantity: number;
  total: number;
  productId?: number;
  paymentMethod?: string;
  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount: number;
  orderNumber: string;
  orderDate: Date;
}

const OrderFunnelContainer = () => {
  const navigate = useNavigate();
  const { Funnel, Step, nextClickHandler, prevClickHandler } = useFunnel(
    steps,
    'cart',
  );

  const [orderData, setOrderData] = useState<OrderData>({
    quantity: 1,
    total: 0,
    productId: 0,
    finalAmount: 0,
    orderNumber: '',
    orderDate: new Date(),
  });

  const handleNextToCart = (cartData: CartData) => {
    setOrderData(prev => ({ ...prev, ...cartData }));
    nextClickHandler('payment');
  };

  const handleNextToPayment = (paymentData: PaymentData) => {
    const orderNumber = `ORDER-${Date.now()}`;
    const orderDate = new Date();
    setOrderData(prev => ({
      ...prev,
      ...paymentData,
      orderNumber,
      orderDate,
    }));
    nextClickHandler('complete');
  };

  const handleBackToCart = () => {
    prevClickHandler('cart');
  };

  const handleBackToPayment = () => {
    prevClickHandler('payment');
  };

  const handleBackToHome = () => {
    navigate(ROUTE_PATH.HOME);
  };

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
          onNext={handleNextToPayment}
          onBack={handleBackToCart}
          orderData={orderData}
        />
      </Step>

      <Step name="complete">
        <OrderComplete onBack={handleBackToPayment} orderData={orderData} />
      </Step>
    </Funnel>
  );
};

export default OrderFunnelContainer;
