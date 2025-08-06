import { useState } from 'react';
import { useFunnel } from '@/hooks/useFunnel';
import Cart from '@/pages/Cart';
import Payment from '@/pages/Payment';
import OrderComplete from '@/pages/OrderComplete';

// 스텝 정의 - 토스 스타일로 배열과 타입 분리
const steps = ['cart', 'payment', 'complete'] as const;

// 각 스텝에서 전달하는 데이터 타입들
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

// 전체 주문 데이터 타입
interface OrderData {
  // Cart에서 오는 데이터
  quantity: number;
  total: number;
  productId?: number;

  // Payment에서 오는 데이터
  paymentMethod?: string;
  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount?: number;

  // Complete에서 생성되는 데이터
  orderNumber?: string;
  orderDate?: Date;
}

const OrderFunnelContainer = () => {
  const { Funnel, Step, nextClickHandler, prevClickHandler } = useFunnel(
    steps,
    'cart',
  );

  const [orderData, setOrderData] = useState<OrderData>({
    quantity: 1,
    total: 0,
    productId: 0,
  });

  const handleCartNext = (cartData: CartData) => {
    setOrderData(prev => ({ ...prev, ...cartData }));
    nextClickHandler('payment');
  };

  const handlePaymentNext = (paymentData: PaymentData) => {
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
    // 홈으로 이동 또는 브라우저 뒤로가기
    window.history.back();
  };

  return (
    <Funnel>
      <Step name="cart">
        <Cart
          onNext={handleCartNext}
          onBack={handleBackToHome}
          initialQuantity={orderData.quantity}
        />
      </Step>

      <Step name="payment">
        <Payment
          onNext={handlePaymentNext}
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
