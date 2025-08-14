import { ChevronRight, Gift } from 'lucide-react';

interface CouponSectionProps {
  discountAmount: number;
}

export default function CouponSection({ discountAmount }: CouponSectionProps) {
  return (
    <div className="space-y-3">
      <h3 className="text-sm font-medium text-gray-900">할인 혜택</h3>
      <button className="flex w-full items-center justify-between rounded-lg bg-gray-50 p-4">
        <div className="flex items-center space-x-3">
          <Gift className="h-5 w-5 text-orange-600" />
          <span className="text-sm text-gray-900">쿠폰 사용</span>
        </div>
        <div className="flex items-center space-x-2">
          <span className="text-xs text-green-600">
            {discountAmount.toLocaleString()}원 할인
          </span>
          <span className="text-gray-400">
            <ChevronRight />
          </span>
        </div>
      </button>
    </div>
  );
}
