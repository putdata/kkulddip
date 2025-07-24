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
      className={`inline-block rounded px-2 py-1 text-xs font-medium ${className}`}
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
    <BaseBadge className="bg-yellow-100 text-yellow-800">
      {discountPercent}% 할인
    </BaseBadge>
  );
};

// 시간 뱃지
export interface TimeBadgeProps {
  timeText: string;
}

export const TimeBadge = ({ timeText }: TimeBadgeProps) => {
  return <BaseBadge className="bg-red-500 text-white">{timeText}</BaseBadge>;
};

// 재고 뱃지
export interface StockBadgeProps {
  remainingCount: number;
}

export const StockBadge = ({ remainingCount }: StockBadgeProps) => {
  return (
    <BaseBadge className="bg-red-100 text-red-500">
      남은 수량 {remainingCount}개
    </BaseBadge>
  );
};
