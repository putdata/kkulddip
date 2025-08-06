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
  finalAmount?: number;
  orderNumber?: string;
  orderDate?: Date;
}

interface PaymentProps {
  onNext: (paymentData: PaymentData) => void;
  onBack: () => void;
  orderData: OrderData;
}

const Payment = ({ onBack, onNext, orderData }: PaymentProps) => {
  const baseAmount =
    orderData.total || dummyProductData.price * orderData.quantity;
  const finalAmount = baseAmount - dummyDiscountAmount;

  const handleNext = () => {
    const paymentData: PaymentData = {
      paymentMethod: '카드결제',
      appliedCouponId: 'COUPON123',
      discountAmount: dummyDiscountAmount,
      finalAmount: finalAmount,
    };
    onNext(paymentData);
  };

  const bottomButton = (
    <button
      onClick={handleNext}
      className="fixed bottom-3 w-11/12 rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
    >
      {formatPrice(finalAmount)} {PAYMENT_MESSAGES.PAYMENT_BUTTON}
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
