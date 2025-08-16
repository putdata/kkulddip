import { CheckCircle, Clock, MapPin } from 'lucide-react';
import { ROUTE_PATH } from '@/router';
import { useNavigate } from 'react-router-dom';
import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import { priceUtils } from '@/utils/priceFormat';
import type { OrderResponse } from '@/types/payments';
import { useCartStore } from '@/store/useCartStore';

interface OrderCompleteProps {
  onBack: () => void;
  orderResponse: OrderResponse; // 서버 응답 (orderId, finalPrice 등)
}

const OrderComplete = ({ onBack, orderResponse }: OrderCompleteProps) => {
  const { storeInfo, items } = useCartStore(); // 가게/상품 정보 가져오기
  const navigate = useNavigate();

  const displayData = {
    orderNumber: orderResponse.orderId || 'ORDER-2025-001234',
    productName:
      items.length > 1
        ? `띱박스 ${items.length}개`
        : items[0]?.name || '띱박스',
    quantity: items.reduce((sum, item) => sum + item.quantity, 0), // 전체 수량
    totalAmount: orderResponse.finalPrice || 18000,
    storeName: storeInfo?.name || '가게 정보 없음',
    storeAddress: storeInfo?.address || '주소 정보 없음',
    pickupTime: storeInfo?.pickupTime || '픽업 시간 정보 없음',
    // TODO: 이게 뭔지? 석규님께 확인 필요
    estimatedTime: '15분', // 기본값 유지
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
            <span className="text-xs text-gray-800">토스페이</span>
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
