import { ArrowLeft } from 'lucide-react';
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

// Payment에서 전달하는 데이터 타입 - 퍼넬 컨테이너와 동일해야 함
interface PaymentData {
  paymentMethod: string;
  appliedCouponId?: string;
  discountAmount: number;
  finalAmount: number;
}

// 전체 주문 데이터 타입 - 퍼넬 컨테이너와 동일해야 함
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
  onNext: (paymentData: PaymentData) => void; // ✅ PaymentData를 전달하도록 수정
  onBack: () => void;
  orderData: OrderData; // ✅ 퍼넬에서 전달받은 주문 데이터
}

const Payment = ({ onBack, onNext, orderData }: PaymentProps) => {
  // ✅ 퍼넬에서 전달받은 데이터를 사용하여 최종 금액 계산
  const baseAmount =
    orderData.total || dummyProductData.price * orderData.quantity;
  const finalAmount = baseAmount - dummyDiscountAmount;

  // ✅ 데이터를 퍼넬에 전달하는 핸들러 함수 추가
  const handleNext = () => {
    const paymentData: PaymentData = {
      paymentMethod: '카드결제', // 실제로는 사용자가 선택한 결제 방법
      appliedCouponId: 'COUPON123', // 실제로는 사용자가 적용한 쿠폰 ID
      discountAmount: dummyDiscountAmount,
      finalAmount: finalAmount,
    };
    onNext(paymentData); // 퍼넬 컨테이너로 데이터 전달
  };

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-white pb-20">
      {/* 헤더 */}
      <div className="relative flex w-full flex-row items-center p-3">
        <button onClick={onBack} className="rounded-full p-2">
          <ArrowLeft className="h-6 w-6 text-gray-700" />
        </button>
        <h1 className="absolute left-1/2 -translate-x-1/2 text-lg font-semibold text-gray-700">
          결제하기
        </h1>
      </div>

      {/* 전체 카드 */}
      <div className="w-full overflow-hidden rounded-[2.5rem] bg-amber-50 pt-8 shadow-lg">
        {/* 진행 상태 표시 */}
        <div className="px-6 pb-6">
          <div className="flex items-center justify-between">
            {/* 장바구니 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-gray-200">
                <span className="text-lg text-gray-400">🛒</span>
              </div>
              <span className="text-xs text-gray-400">장바구니</span>
            </div>

            {/* 연결선 */}
            <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

            {/* 결제 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-orange-300">
                <span className="pb-2 text-2xl text-white">💳</span>
              </div>
              <span className="text-xs text-gray-500">결제</span>
            </div>

            {/* 연결선 */}
            <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

            {/* 완료 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-gray-200">
                <span className="text-lg text-gray-400">✓</span>
              </div>
              <span className="text-xs text-gray-400">완료</span>
            </div>
          </div>
        </div>

        {/* 메인 콘텐츠 스크롤 영역 */}
        <div className="min-h-[60vh] rounded-t-[2rem] bg-amber-100 p-6">
          <div className="space-y-3">
            {/* 주문 요약 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <OrderSummary
                productName={dummyProductData.name}
                quantity={orderData.quantity}
              />
            </div>

            {/* 픽업 정보 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <PickupInfo
                storeName={dummyStoreData.name}
                address={dummyStoreData.address}
                pickupTime={dummyStoreData.pickupTime}
              />
            </div>

            {/* 결제 방법 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <PaymentMethod />
            </div>

            {/* 쿠폰 섹션 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <CouponSection discountAmount={dummyDiscountAmount} />
            </div>

            {/* 최종 가격 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <FinalPrice
                orderAmount={orderData.total}
                discount={dummyDiscountAmount}
              />
            </div>

            {/* 안내사항 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <div className="space-y-1 text-xs text-gray-500">
                <p>• {PAYMENT_MESSAGES.PICKUP_NOTICE}</p>
                <p>• {PAYMENT_MESSAGES.CANCEL_NOTICE}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 하단 결제 버튼 */}
      <button
        onClick={handleNext}
        className="fixed bottom-3 w-11/12 rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
      >
        {formatPrice(finalAmount)} {PAYMENT_MESSAGES.PAYMENT_BUTTON}
      </button>
    </div>
  );
};

export default Payment;
