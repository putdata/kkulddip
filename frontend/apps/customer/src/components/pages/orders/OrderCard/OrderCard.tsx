import { ChevronRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { formatPrice } from '@/utils/priceFormat';
import { formatDate } from '@/utils/dateFormat';
import { type OrderCardProps } from '@/types/orderFood';
import { ROUTE_PATH } from '@/router/route-path';
import { useReviewStore } from '@/store/useReviewStore';

const OrderCard = ({ item }: OrderCardProps) => {
  const {
    orderDate,
    storeName,
    orderItems,
    originalPrice,
    finalPrice,
    orderStatus,
  } = item;

  const navigate = useNavigate();
  const { setReviewData } = useReviewStore();

  // 할인 금액 계산
  const discountAmount = originalPrice - finalPrice;

  return (
    <div className="w-full max-w-sm overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm transition-shadow">
      <div className="flex items-center justify-between bg-gray-50 px-3 py-2">
        <span className="text-xs font-medium text-gray-600">
          {formatDate(orderDate)}
        </span>
        <button
          className="rounded border border-gray-300 bg-white px-2 py-0.5 text-xs font-medium text-gray-700 transition-colors hover:bg-gray-50"
          onClick={() =>
            navigate(ROUTE_PATH.ORDER_DETAIL.replace(':orderId', item.orderId))
          }
        >
          주문상세
        </button>
      </div>

      <div className="space-y-3 p-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="flex h-10 w-10 items-center justify-center overflow-hidden rounded-lg bg-gray-100">
              <div className="text-sm font-semibold text-gray-600">
                {storeName.charAt(0)}
              </div>
            </div>
            <h3 className="max-w-[120px] truncate text-sm font-semibold text-gray-900">
              {storeName}
            </h3>
          </div>
          <div className="flex items-center gap-1">
            <ChevronRight className="h-3 w-3 text-gray-400" />
          </div>
        </div>

        <div className="space-y-1">
          {orderItems.map((orderItem, idx) => (
            <div
              key={`${orderItem.productId}-${idx}`}
              className="flex items-center justify-between text-xs"
            >
              <span className="flex-1 truncate pr-2 text-gray-700">
                {orderItem.productName}
              </span>
              <span className="font-medium text-gray-500">
                {orderItem.quantity}개
              </span>
            </div>
          ))}
        </div>

        <div className="space-y-1 border-t border-gray-100 pt-2">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-gray-800">결제금액</span>
            <div className="text-right">
              {originalPrice !== finalPrice && (
                <div className="text-xs text-gray-400 line-through">
                  {formatPrice(originalPrice)}
                </div>
              )}
              <div className="text-sm font-bold text-gray-900">
                {formatPrice(finalPrice)}
              </div>
            </div>
          </div>
          {originalPrice !== finalPrice && (
            <div className="text-right">
              <span className="text-xs font-medium text-blue-600">
                꿀띱에서만 {formatPrice(discountAmount)} 할인
              </span>
            </div>
          )}
        </div>

        {(orderStatus === 'CONFIRMED' || orderStatus === 'PICKED_UP') && (
          <button
            className="border-1 mt-2 w-full cursor-pointer rounded-lg border-gray-400 bg-white py-2 text-xs font-semibold text-gray-700 transition-colors active:bg-gray-100"
            onClick={() => {
              setReviewData({
                storeId: item.storeId,
                orderId: item.orderId,
                storeName: item.storeName,
                orderItems: item.orderItems,
              });
              navigate(ROUTE_PATH.REVIEW_CREATE);
            }}
          >
            리뷰 작성하기
          </button>
        )}
      </div>
    </div>
  );
};

export default OrderCard;
