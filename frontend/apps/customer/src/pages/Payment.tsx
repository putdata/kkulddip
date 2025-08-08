import { useState } from 'react';
import OrderSummary from '@/components/pages/payment/OrderSummary/OrderSummary';
import PickupInfo from '@/components/pages/payment/PickupInfo/PickupInfo';
import CouponSection from '@/components/pages/payment/CouponSection/CouponSection';
import FinalPrice from '@/components/pages/payment/FinalPrice/FinalPrice';
import PaymentMethod from '@/components/pages/payment/PaymentMethod/PaymentMethod';
import {
  dummyProductData,
  dummyStoreData,
  dummyDiscountAmount,
} from '@/dummies/paymentDummy';

import { PAYMENT_MESSAGES } from '@/constants/payment';
import { formatPrice } from '@/utils/priceFormat';
import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import { requestTossPayment } from '@/services/tossPayments';
import { ROUTE_PATH } from '@/router/route-path';

interface OrderData {
  quantity: number;
  total: number;
  productId?: number;
  appliedCouponId?: string;
  discountAmount?: number;
  finalAmount?: number;
  orderNumber?: string;
  orderDate?: Date;
}

interface PaymentProps {
  onBack: () => void;
  orderData: OrderData;
}

const Payment = ({ onBack, orderData }: PaymentProps) => {
  const [isProcessing, setIsProcessing] = useState(false);
  const baseAmount =
    orderData.total || dummyProductData.price * orderData.quantity;
  const finalAmount = baseAmount - dummyDiscountAmount;

  const handleNext = async () => {
    if (isProcessing) {
      return;
    }

    setIsProcessing(true);

    try {
      // 주문 ID 생성 (실제로는 서버에서 생성)
      const orderId = `ORDER_${Date.now()}`;

      // 토스페이먼츠 결제 요청
      await requestTossPayment({
        amount: finalAmount,
        orderId,
        orderName: `${dummyProductData.name} x ${orderData.quantity}`,
        customerName: '고객명', // 실제 고객 정보로 변경 필요
        customerEmail: 'customer@example.com', // 실제 고객 정보로 변경 필요
        successUrl: `${window.location.origin}${ROUTE_PATH.PAYMENT_SUCCESS}`,
        failUrl: `${window.location.origin}${ROUTE_PATH.PAYMENT_FAIL}`,
      });
    } catch (error) {
      console.error('결제 요청 실패:', error);
      setIsProcessing(false);
      // 에러를 상위로 전파하여 GlobalErrorBoundary에서 처리
      throw error;
    }
  };

  const bottomButton = (
    <button
      onClick={handleNext}
      disabled={isProcessing}
      className="fixed bottom-3 w-11/12 rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors disabled:cursor-not-allowed disabled:opacity-50"
    >
      {isProcessing ? (
        <div className="flex items-center justify-center">
          <div className="mr-2 h-4 w-4 animate-spin rounded-full border-2 border-white border-t-transparent"></div>
          결제 처리 중...
        </div>
      ) : (
        `${formatPrice(finalAmount)} ${PAYMENT_MESSAGES.PAYMENT_BUTTON}`
      )}
    </button>
  );

  return (
    <OrderFlowLayout
      title="결제하기"
      currentStep="payment"
      onBack={onBack}
      bottomButton={bottomButton}
    >
      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <OrderSummary
          productName={dummyProductData.name}
          quantity={orderData.quantity}
        />
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <PickupInfo
          storeName={dummyStoreData.name}
          address={dummyStoreData.address}
          pickupTime={dummyStoreData.pickupTime}
        />
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <PaymentMethod />
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <CouponSection discountAmount={dummyDiscountAmount} />
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <FinalPrice
          orderAmount={orderData.total}
          discount={dummyDiscountAmount}
        />
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <div className="space-y-1 text-xs text-gray-500">
          <p>• {PAYMENT_MESSAGES.PICKUP_NOTICE}</p>
          <p>• {PAYMENT_MESSAGES.CANCEL_NOTICE}</p>
        </div>
      </div>
    </OrderFlowLayout>
  );
};

export default Payment;
