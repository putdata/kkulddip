import { useMemo } from 'react';
import type {
  SalesPrediction,
  TopSellingItem,
  TopSellingProduct,
} from '@/types/analytics';

/**
 * 매출 분석 차트 데이터 처리 훅
 *
 * @description
 * 매출 예측, 인기 상품 분석 등 매출 관련 차트의 비즈니스 로직을 관리합니다.
 *
 * @param predictions 매출 예측 데이터
 * @param topItems 인기 상품 데이터
 * @param topProducts 인기 제품 데이터
 * @returns 매출 분석에 필요한 처리된 데이터와 계산 결과
 */
export const useSalesChartData = (
  predictions?: SalesPrediction[],
  topItems?: TopSellingItem[],
  topProducts?: TopSellingProduct[],
) => {
  /*
   * 매출 예측 차트용 데이터 (날짜, 예측값, 신뢰도)
   */
  const predictionChartData = useMemo(
    () =>
      predictions?.map(prediction => ({
        date: prediction.date,
        revenue: prediction.predictedRevenue,
        confidence: prediction.confidence,
      })) || [],
    [predictions],
  );

  /*
   * 신뢰도에 따른 색상 반환 함수
   */
  const getConfidenceColor = (confidence: number) => {
    if (confidence >= 80) {
      return '#10b981'; // green-500
    } else if (confidence >= 60) {
      return '#3b82f6'; // blue-500
    } else if (confidence >= 40) {
      return '#f59e0b'; // amber-500
    }
    return '#ef4444'; // red-500
  };

  /*
   * 평균 예측 매출 계산값
   */
  const avgPredictedRevenue = useMemo(() => {
    if (!predictions || predictions.length === 0) {
      return 0;
    }
    return (
      predictions.reduce((sum, p) => sum + p.predictedRevenue, 0) /
      predictions.length
    );
  }, [predictions]);

  /*
   * 인기 상품 차트용 데이터 (이름, 판매량, 비율)
   */
  const itemsChartData = useMemo(
    () =>
      topItems?.map(item => ({
        name: item.itemName,
        value: item.totalQuantity,
        percentage: item.percentage,
      })) || [],
    [topItems],
  );

  /*
   * 인기 제품 차트용 데이터 (이름, 판매량, 비율)
   */
  const productsChartData = useMemo(
    () =>
      topProducts?.map(product => ({
        name: product.productName,
        value: product.totalQuantity,
        percentage: product.percentage,
      })) || [],
    [topProducts],
  );

  return {
    predictionChartData,
    getConfidenceColor,
    avgPredictedRevenue,
    itemsChartData,
    productsChartData,
  };
};
