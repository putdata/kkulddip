import React from 'react';

// 공통 베이스 컴포넌트 (외부에서 사용하지 않음)
const BaseBadge = ({
  children,
  className,
}: {
  children: React.ReactNode;
  className: string;
}) => {
  return (
    <div
      className={`inline-flex items-center justify-center rounded-full px-2.5 py-1 text-xs font-semibold ${className}`}
    >
      {children}
    </div>
  );
};

// 할인율 뱃지
export interface DiscountBadgeProps {
  originalPrice: number;
  discountPrice: number;
}

export const DiscountBadge = ({
  originalPrice,
  discountPrice,
}: DiscountBadgeProps) => {
  const discountPercent = Math.round(
    ((originalPrice - discountPrice) / originalPrice) * 100,
  );

  return (
    <BaseBadge className="bg-gradient-to-r from-red-500 to-orange-500 text-white shadow-sm">
      {discountPercent}% 할인
    </BaseBadge>
  );
};

// 시간 뱃지
export interface TimeBadgeProps {
  timeText: string;
}

export const TimeBadge = ({ timeText }: TimeBadgeProps) => {
  return (
    <BaseBadge className="bg-red-500 text-white shadow-sm">
      {timeText}
    </BaseBadge>
  );
};

// 재고 뱃지
export interface StockBadgeProps {
  remainingCount: number;
}

export const StockBadge = ({ remainingCount }: StockBadgeProps) => {
  return (
    <BaseBadge className="border border-red-200 bg-red-50 text-red-600">
      남은 수량 {remainingCount}개
    </BaseBadge>
  );
};
