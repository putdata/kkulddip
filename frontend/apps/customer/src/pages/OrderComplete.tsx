import { CheckCircle, Clock, MapPin } from 'lucide-react';
import { ROUTE_PATH } from '@/router';
import { useNavigate } from 'react-router-dom';
import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import { priceUtils } from '@/utils/priceFormat';

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

interface OrderCompleteProps {
  onBack: () => void;
  orderData: OrderData;
}

const OrderComplete = ({ onBack, orderData }: OrderCompleteProps) => {
  const navigate = useNavigate();

  const displayData = {
    orderNumber: orderData.orderNumber || 'ORDER-2025-001234',
    productName: '치킨 버거 세트',
    quantity: orderData.quantity,
    totalAmount: orderData.finalAmount || orderData.total || 18000,
    storeName: '맥도날드 강남점',
    storeAddress: '서울시 강남구 테헤란로 123',
    pickupTime: '오후 2:30 - 2:40',
    estimatedTime: '15분',
    paymentMethod: orderData.paymentMethod || '카드결제',
  };

  const bottomButton = (
    <div className="fixed bottom-3 w-11/12">
      <button
        onClick={() => {
          navigate(ROUTE_PATH.ORDER);
        }}
        className="w-full rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
      >
        주문내역 보기
      </button>
    </div>
  );

  return (
    <OrderFlowLayout
      title="주문 완료"
      currentStep="complete"
      onBack={onBack}
      bottomButton={bottomButton}
    >
      <div className="rounded-2xl bg-white p-6 text-center shadow-sm">
        <div className="mb-4">
          <CheckCircle className="mx-auto h-16 w-16 text-green-500" />
        </div>
        <p className="mb-2 text-sm font-bold text-gray-800">
          주문이 완료되었습니다!
        </p>
        <p className="text-xs text-gray-600">
          주문번호: {displayData.orderNumber}
        </p>
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <p className="mb-3 text-sm font-semibold text-gray-800">주문 정보</p>
        <div className="space-y-2">
          <div className="flex justify-between">
            <span className="text-xs text-gray-600">
              {displayData.productName}
            </span>
            <span className="text-xs text-gray-800">
              {displayData.quantity}개
            </span>
          </div>
          <div className="flex justify-between">
            <span className="text-xs text-gray-600">결제 방법</span>
            <span className="text-xs text-gray-800">
              {displayData.paymentMethod}
            </span>
          </div>
          <div className="flex justify-between border-t pt-2">
            <span className="text-sm font-medium text-gray-800">
              총 결제 금액
            </span>
            <span className="text-xs font-bold text-amber-600">
              {priceUtils.formatPrice(displayData.totalAmount)}
            </span>
          </div>
        </div>
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <h3 className="mb-3 flex items-center font-semibold text-gray-800">
          <MapPin className="mr-2 h-5 w-5 text-amber-500" />
          픽업 정보
        </h3>
        <div className="space-y-2">
          <div>
            <p className="text-xs font-medium text-gray-800">
              {displayData.storeName}
            </p>
            <p className="text-xs text-gray-600">{displayData.storeAddress}</p>
          </div>
          <div className="flex items-center border-t pt-2 text-xs text-gray-600">
            <Clock className="mr-2 h-4 w-4 text-amber-500" />
            <span>픽업 시간: {displayData.pickupTime}</span>
          </div>
        </div>
      </div>

      <div className="rounded-2xl bg-amber-200 p-4 shadow-sm">
        <div className="flex items-center justify-center">
          <Clock className="mr-2 h-5 w-5 text-amber-700" />
          <span className="text-sm font-medium text-amber-800">
            예상 준비 시간: {displayData.estimatedTime}
          </span>
        </div>
      </div>

      <div className="rounded-2xl bg-white p-4 shadow-sm">
        <h3 className="mb-3 font-semibold text-gray-800">안내사항</h3>
        <div className="space-y-1 text-xs text-gray-500">
          <p>• 픽업 시간에 맞춰 매장에 방문해 주세요</p>
          <p>• 주문번호를 매장에서 말씀해 주세요</p>
          <p>• 픽업 시간이 지나면 주문이 자동 취소될 수 있습니다</p>
        </div>
      </div>
    </OrderFlowLayout>
  );
};

export default OrderComplete;
