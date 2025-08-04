import { ChevronUp } from 'lucide-react';

export interface PaymentInfo {
  totalOriginalPrice: number;
  totalDiscount: number;
  discounts: {
    discountHistoryId: number;
    discountAmount: number;
    discountType: string;
  }[];
  totalItems: number; // 주문수량
  paymentMethod: string;
}

export interface PaymentInfoCardProps {
  paymentInfo: PaymentInfo;
}

const formatPrice = (price: number) => `${price.toLocaleString()}원`;

const getDiscountDisplayName = (discountType: string) => {
  const displayNames: { [key: string]: string } = {
    MEMBERSHIP: '꿀띱클럽 할인',
    IMMEDIATE: '즉시할인',
    COUPON: '쿠폰할인',
    // 필요에 따라 더 추가 가능
  };
  return displayNames[discountType] || discountType;
};

const PaymentInfoCard = ({ paymentInfo }: PaymentInfoCardProps) => {
  const {
    totalOriginalPrice,
    totalDiscount,
    discounts,
    totalItems,
    paymentMethod,
  } = paymentInfo;

  const finalAmount = totalOriginalPrice - totalDiscount;

  return (
    <div className="space-y-4">
      {/* 결제 정보 카드 */}
      <div className="rounded-2xl border border-gray-200 bg-white p-4 shadow-sm">
        <div className="space-y-3">
          {/* 매뉴금액 */}
          <div className="flex items-center justify-between">
            <span className="text-sm text-gray-700">매뉴금액</span>
            <span className="text-sm font-medium text-gray-900">
              {formatPrice(totalOriginalPrice)}
            </span>
          </div>

          {/* 점선 구분선 */}
          <div className="my-3 border-t border-dashed border-gray-300"></div>

          {/* 총 할인받은 금액 */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <span className="text-sm font-medium text-blue-600">
                총 할인받은 금액
              </span>
              <ChevronUp className="h-4 w-4 text-blue-600" />
            </div>
            <span className="text-sm font-bold text-blue-600">
              -{formatPrice(totalDiscount)}
            </span>
          </div>

          {/* 할인 내역들 */}
          {discounts.map(discount => (
            <div
              key={discount.discountHistoryId}
              className="flex items-center justify-between pl-4"
            >
              <div className="flex items-center gap-2">
                <span className="text-xs text-gray-600">
                  {getDiscountDisplayName(discount.discountType)}
                </span>
                {discount.discountType === '꿀띱 MEMBERSHIP' && (
                  <span className="rounded bg-green-100 px-1.5 py-0.5 text-xs text-green-700">
                    🏷️
                  </span>
                )}
              </div>
              <span className="text-xs font-medium text-blue-600">
                -{formatPrice(discount.discountAmount)}
              </span>
            </div>
          ))}

          {/* 굵은 구분선 */}
          <div className="my-4 border-t-2 border-gray-300"></div>

          {/* 결제금액 */}
          <div className="flex items-center justify-between">
            <span className="text-md text-base font-bold text-gray-900">
              결제금액
            </span>
            <span className="text-xl font-bold text-gray-900">
              {formatPrice(finalAmount)}
            </span>
          </div>

          {/* 주문수량 */}
          <div className="flex items-center justify-between pt-2">
            <span className="text-xs text-gray-600">주문수량</span>
            <span className="text-xs font-medium text-gray-900">
              {totalItems}개
            </span>
          </div>

          {/* 결제방법 */}
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-gray-700">결제방법</span>
            <span className="text-xs font-medium text-gray-900">
              {paymentMethod}
            </span>
          </div>
        </div>
      </div>

      {/* 문의 버튼 */}
      <button className="w-full rounded-2xl border border-gray-200 bg-white py-4 text-center text-sm font-bold text-gray-700 shadow-sm transition-colors hover:bg-gray-50">
        주문에 문제가 있나요?
        <div className="text-xs text-gray-400">고객센터에 문의하기</div>
      </button>
    </div>
  );
};

export default PaymentInfoCard;
