import { ArrowLeft, CheckCircle, Clock, MapPin } from 'lucide-react';
import { ROUTE_PATH } from '@/router';
import { useNavigate } from 'react-router-dom';

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

  return (
    <div className="flex min-h-screen flex-col items-center justify-center bg-white pb-20">
      {/* 헤더 */}
      <div className="relative flex w-full flex-row items-center p-3">
        <button onClick={onBack} className="rounded-full p-2">
          <ArrowLeft className="h-6 w-6 text-gray-700" />
        </button>
        <h1 className="absolute left-1/2 -translate-x-1/2 text-lg font-semibold text-gray-700">
          주문 완료
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
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-gray-200">
                <span className="pb-2 text-2xl text-gray-400">💳</span>
              </div>
              <span className="text-xs text-gray-400">결제</span>
            </div>

            {/* 연결선 */}
            <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

            {/* 완료 단계 */}
            <div className="flex flex-col items-center">
              <div className="mb-2 flex h-12 w-12 items-center justify-center rounded-2xl bg-green-500">
                <span className="text-lg text-white">✓</span>
              </div>
              <span className="text-xs font-medium text-gray-600">완료</span>
            </div>
          </div>
        </div>

        {/* 메인 콘텐츠 스크롤 영역 */}
        <div className="min-h-[60vh] rounded-t-[2rem] bg-amber-100 p-6">
          <div className="space-y-3">
            {/* 완료 메시지 카드 */}
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

            {/* 주문 정보 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <p className="mb-3 text-sm font-semibold text-gray-800">
                주문 정보
              </p>
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
                    {displayData.totalAmount.toLocaleString()}원
                  </span>
                </div>
              </div>
            </div>

            {/* 픽업 정보 카드 */}
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
                  <p className="text-xs text-gray-600">
                    {displayData.storeAddress}
                  </p>
                </div>
                <div className="flex items-center border-t pt-2 text-xs text-gray-600">
                  <Clock className="mr-2 h-4 w-4 text-amber-500" />
                  <span>픽업 시간: {displayData.pickupTime}</span>
                </div>
              </div>
            </div>

            {/* 예상 시간 카드 */}
            <div className="rounded-2xl bg-amber-200 p-4 shadow-sm">
              <div className="flex items-center justify-center">
                <Clock className="mr-2 h-5 w-5 text-amber-700" />
                <span className="text-sm font-medium text-amber-800">
                  예상 준비 시간: {displayData.estimatedTime}
                </span>
              </div>
            </div>

            {/* 주문 요약 카드 (추가) */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <h3 className="mb-3 font-semibold text-gray-800">주문 요약</h3>
              <div className="space-y-1 text-xs text-gray-600">
                <p>
                  • 주문 일시:{' '}
                  {orderData.orderDate?.toLocaleString() || '방금 전'}
                </p>
                <p>
                  • 할인 금액: {orderData.discountAmount?.toLocaleString() || 0}
                  원
                </p>
                <p>• 쿠폰: {orderData.appliedCouponId || '사용 안함'}</p>
              </div>
            </div>

            {/* 안내사항 카드 */}
            <div className="rounded-2xl bg-white p-4 shadow-sm">
              <h3 className="mb-3 font-semibold text-gray-800">안내사항</h3>
              <div className="space-y-1 text-xs text-gray-500">
                <p>• 픽업 시간에 맞춰 매장에 방문해 주세요</p>
                <p>• 주문번호를 매장에서 말씀해 주세요</p>
                <p>• 픽업 시간이 지나면 주문이 자동 취소될 수 있습니다</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 하단 버튼들 */}
      <div className="fixed bottom-3 w-11/12 space-y-2">
        <button
          onClick={() => {
            navigate(ROUTE_PATH.ORDER);
          }}
          className="w-full rounded-2xl bg-amber-500 py-4 font-semibold text-white shadow-sm transition-colors"
        >
          주문내역 보기
        </button>
      </div>
    </div>
  );
};

export default OrderComplete;
