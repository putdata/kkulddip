import { CheckCircle, Clock, MapPin } from 'lucide-react';
import { ROUTE_PATH } from '@/router';
import { useNavigate } from 'react-router-dom';
import OrderFlowLayout from '@/components/layout/OrderFlowLayout';
import { priceUtils } from '@/utils/priceFormat';
import type { OrderResponse } from '@/types/payments';
import { useCartStore } from '@/store/useCartStore';
import { useOrderFlowStore } from '@/store/useOrderFlowStore';

interface OrderCompleteProps {
  onBack: () => void;
  orderResponse: OrderResponse; // 서버 응답 (orderId, finalPrice 등)
}

const OrderComplete = ({ onBack, orderResponse }: OrderCompleteProps) => {
  const { storeInfo, items } = useCartStore(); // 가게/상품 정보 가져오기
  const { backupCartItems, backupStoreInfo } = useOrderFlowStore(); // 백업 데이터
  const navigate = useNavigate();

  // 백업 데이터가 있으면 백업 데이터 사용, 없으면 현재 장바구니 데이터 사용
  const actualItems =
    backupCartItems && backupCartItems.length > 0 ? backupCartItems : items;
  const actualStoreInfo = backupStoreInfo || storeInfo;

  const getProductDisplayName = () => {
    if (actualItems.length === 1) {
      return actualItems[0]?.name || '띱박스';
    }
    const uniqueNames = [...new Set(actualItems.map(item => item.name))];
    if (uniqueNames.length === 1) {
      return uniqueNames[0];
    }
    return `띱박스 ${actualItems.length}종류`;
  };

  const displayData = {
    orderNumber: orderResponse.orderId,
    productName: getProductDisplayName(),
    quantity: actualItems.reduce((sum, item) => sum + item.quantity, 0),
    totalAmount: orderResponse.finalPrice || 18000,
    storeName: actualStoreInfo?.name || '가게 정보 없음',
    storeAddress: actualStoreInfo?.address || '주소 정보 없음',
    orderStatus: orderResponse.orderStatus || 'PENDING',
  };

  const bottomButton = (
    <button
      onClick={() => {
        navigate(ROUTE_PATH.ORDER);
      }}
      className="w-full rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
    >
      주문내역 보기
    </button>
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
        </div>
      </div>

      <div
        className={`rounded-2xl p-4 shadow-sm ${
          displayData.orderStatus === 'CONFIRMED'
            ? 'bg-green-200'
            : 'bg-amber-200'
        }`}
      >
        <div className="flex items-center justify-center">
          <Clock
            className={`mr-2 h-5 w-5 ${
              displayData.orderStatus === 'CONFIRMED'
                ? 'text-green-700'
                : 'text-amber-700'
            }`}
          />
          <span
            className={`text-sm font-medium ${
              displayData.orderStatus === 'CONFIRMED'
                ? 'text-green-800'
                : 'text-amber-800'
            }`}
          >
            {displayData.orderStatus === 'CONFIRMED'
              ? '주문 확정 완료'
              : '주문 확인 중입니다'}
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
