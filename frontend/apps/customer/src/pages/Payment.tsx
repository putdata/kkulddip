import OrderSummary from '@/components/pages/payment/OrderSummary/OrderSummary';
import PickupInfo from '@/components/pages/payment/PickupInfo/PickupInfo';
import CouponSection from '@/components/pages/payment/CouponSection/CouponSection';
import FinalPrice from '@/components/pages/payment/FinalPrice/FinalPrice';
import PaymentMethod from '@/components/pages/payment/PaymentMethod/PaymentMethod';
import type { PaymentPageProps } from '@/types/payments';
import {
  dummyProductData,
  dummyStoreData,
  dummyDiscountAmount,
} from '@/dummies/paymentDummy';
import { PAYMENT_MESSAGES } from '@/constants/payment';
import { formatPrice } from '@/utils/priceFormat';

const Payment = ({ onComplete }: PaymentPageProps) => {
  const finalAmount =
    dummyProductData.price * dummyProductData.quantity - dummyDiscountAmount;

  return (
    <div className="mx-auto min-h-screen max-w-md bg-white pb-16 pt-16">
      <div className="space-y-6 px-4 py-4">
        <OrderSummary
          productName={dummyProductData.name}
          quantity={dummyProductData.quantity}
        />
        <PickupInfo
          storeName={dummyStoreData.name}
          address={dummyStoreData.address}
          pickupTime={dummyStoreData.pickupTime}
        />
        <PaymentMethod />
        <CouponSection discountAmount={dummyDiscountAmount} />
        <FinalPrice
          orderAmount={dummyProductData.price * dummyProductData.quantity}
          discount={dummyDiscountAmount}
        />

        <div className="space-y-1 text-xs text-gray-500">
          <p>• {PAYMENT_MESSAGES.PICKUP_NOTICE}</p>
          <p>• {PAYMENT_MESSAGES.CANCEL_NOTICE}</p>
        </div>
      </div>

      {/* Bottom Button */}
      <div className="sticky bottom-0 border-t bg-white p-4">
        <button
          onClick={onComplete}
          className="w-full rounded-lg bg-blue-600 py-4 font-medium text-white"
        >
          {formatPrice(finalAmount)} {PAYMENT_MESSAGES.PAYMENT_BUTTON}
        </button>
      </div>
    </div>
  );
};

export default Payment;
