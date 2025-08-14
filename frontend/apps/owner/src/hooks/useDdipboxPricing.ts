import { useMemo } from 'react';
import type { DdipBox } from '@/types/ddipbox';

/**
 * 재고 상태를 나타내는 타입
 */
export interface StockStatus {
  status: 'out' | 'low' | 'medium' | 'high';
  color: 'destructive' | 'outline' | 'secondary';
  text: string;
}

/**
 * 띱박스 가격 계산 및 포맷팅을 위한 커스텀 훅
 *
 * 가격 포맷팅, 할인율 계산, 재고 상태 계산 등
 * 여러 컴포넌트에서 중복 사용되는 로직을 통합 관리합니다.
 */
export const useDdipboxPricing = () => {
  /**
   * 가격을 한국 통화 형식으로 포맷팅
   */
  const formatPrice = useMemo(
    () =>
      (price: number): string => {
        return new Intl.NumberFormat('ko-KR').format(price);
      },
    [],
  );

  /**
   * 할인율 계산
   */
  const getDiscountRate = useMemo(
    () =>
      (originalPrice: number, salePrice: number): number => {
        if (originalPrice === 0) {
          return 0;
        }

        return Math.round(((originalPrice - salePrice) / originalPrice) * 100);
      },
    [],
  );

  /**
   * 재고 상태 계산
   */
  const getStockStatus = useMemo(
    () =>
      (remainingQuantity: number, dailyQuantity: number): StockStatus => {
        const percentage = (remainingQuantity / dailyQuantity) * 100;

        if (percentage === 0) {
          return { status: 'out', color: 'destructive', text: '품절' };
        }
        if (percentage <= 20) {
          return { status: 'low', color: 'destructive', text: '부족' };
        }
        if (percentage <= 50) {
          return { status: 'medium', color: 'outline', text: '보통' };
        }
        return { status: 'high', color: 'secondary', text: '충분' };
      },
    [],
  );

  /**
   * 띱박스 객체를 받아서 계산된 정보를 반환
   */
  const getDdipboxCalculatedInfo = useMemo(
    () => (ddipbox: DdipBox) => {
      const discountRate = getDiscountRate(
        ddipbox.originalPrice,
        ddipbox.salePrice,
      );
      const stockStatus = getStockStatus(
        ddipbox.remainingQuantity,
        ddipbox.dailyQuantity,
      );
      const formattedOriginalPrice = formatPrice(ddipbox.originalPrice);
      const formattedSalePrice = formatPrice(ddipbox.salePrice);

      return {
        discountRate,
        stockStatus,
        formattedOriginalPrice,
        formattedSalePrice,
        hasDiscount: discountRate > 0,
        isOutOfStock: stockStatus.status === 'out',
        isLowStock: stockStatus.status === 'low',
      };
    },
    [formatPrice, getDiscountRate, getStockStatus],
  );

  return {
    formatPrice,
    getDiscountRate,
    getStockStatus,
    getDdipboxCalculatedInfo,
  };
};
