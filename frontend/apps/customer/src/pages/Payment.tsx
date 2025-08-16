import { useState } from 'react';
import OrderSummary from '@/components/pages/payment/OrderSummary/OrderSummary';
import PickupInfo from '@/components/pages/payment/PickupInfo/PickupInfo';
import CouponSection from '@/components/pages/payment/CouponSection/CouponSection';
import FinalPrice from '@/components/pages/payment/FinalPrice/FinalPrice';
import PaymentMethod from '@/components/pages/payment/PaymentMethod/PaymentMethod';
import { dummyDiscountAmount } from '@/dummies/paymentDummy';
import type { OrderData } from '@/types/orderflow';

import { PAYMENT_MESSAGES } from '@/constants/payment';
import { formatPrice } from '@/utils/priceFormat';
import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import { useCartStore } from '@/store/useCartStore';

// Props용 주문 데이터 인터페이스 (기존)
// interface OrderData {
//   quantity: number;
//   total: number;
//   productId?: number;
//   customerId?: number;
//   storeId?: number;
//   appliedCouponId?: string;
//   discountAmount?: number;
//   finalAmount?: number;
//   orderNumber?: string;
//   orderDate?: Date;
// }

interface PaymentProps {
  onNext: () => void;
  onBack: () => void;
  orderData: OrderData;
}

const Payment = ({ onNext, onBack, orderData }: PaymentProps) => {
  const [isProcessing, setIsProcessing] = useState(false);

  const { storeInfo } = useCartStore(); // 가게 정보 가져오기

  const baseAmount = orderData.total;
  const finalAmount = baseAmount - dummyDiscountAmount;

  const handleNext = async () => {
    if (isProcessing) {
      return;
    }

    setIsProcessing(true);

    try {
      // 주문 데이터 준비하고 pending 단계로 이동
      // 실제 토스 결제는 pending 단계에서 처리
      onNext();
    } catch (error) {
      console.error('결제 요청 실패:', error);
      setIsProcessing(false);
      throw error;
    }
  };

  const bottomButton = (
    <button
      onClick={handleNext}
      disabled={isProcessing}
      className="w-full rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors disabled:cursor-not-allowed disabled:opacity-50"
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
          orderItems={orderData.orderItems} // 여러 상품 전달
        />
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <PickupInfo
          storeName={storeInfo?.name || ''}
          address={storeInfo?.address || '주소 정보 없음'} // 기본값 제공
          pickupTime={storeInfo?.pickupTime || ''}
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
