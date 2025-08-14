import { useMemo } from 'react';
import type {
  InventoryPrediction,
  InventoryStatus,
  HighInventoryDdipBox,
} from '@/types/analytics';

/**
 * 재고 분석 차트 데이터 처리 훅
 *
 * @description
 * 재고 예측, 재고 현황, 재고 과다 알림 등 재고 관련 차트의 비즈니스 로직을 관리합니다.
 *
 * @param predictions 재고 예측 데이터
 * @param inventoryStatus 재고 현황 데이터
 * @param highInventoryItems 재고 과다 상품 데이터
 * @returns 재고 분석에 필요한 처리된 데이터와 계산 결과
 */
export const useInventoryAnalytics = (
  predictions?: InventoryPrediction[] | null,
  inventoryStatus?: InventoryStatus,
  highInventoryItems?: HighInventoryDdipBox[],
) => {
  /*
   * 재고 예측 차트용 데이터 (날짜, 일일량, 잔여량, 재고율, 신뢰도)
   */
  const predictionChartData = useMemo(
    () =>
      predictions?.map(prediction => ({
        date: prediction.date,
        dailyQuantity: prediction.predictedDailyQuantity,
        remainingQuantity: prediction.predictedRemainingQuantity,
        inventoryRatio: prediction.inventoryRatio,
        confidence: prediction.confidence * 100,
      })) || [],
    [predictions],
  );

  /*
   * 재고 위험도 계산 및 상태 정보
   */
  const inventoryRiskLevel = useMemo(() => {
    if (predictionChartData.length === 0) {
      return null;
    }
    const avgRatio =
      predictionChartData.reduce((sum, item) => sum + item.inventoryRatio, 0) /
      predictionChartData.length;

    if (avgRatio < 20) {
      return {
        level: 'high' as const,
        color: 'destructive' as const,
        text: '높음',
      };
    }
    if (avgRatio < 50) {
      return {
        level: 'medium' as const,
        color: 'default' as const,
        text: '보통',
      };
    }
    return { level: 'low' as const, color: 'secondary' as const, text: '낮음' };
  }, [predictionChartData]);

  /*
   * 평균 재고율 계산값
   */
  const avgInventoryRatio = useMemo(() => {
    if (predictionChartData.length === 0) {
      return 0;
    }
    return (
      predictionChartData.reduce((sum, item) => sum + item.inventoryRatio, 0) /
      predictionChartData.length
    );
  }, [predictionChartData]);

  /*
   * 재고 현황 차트용 데이터 (판매완료, 남은재고)
   */
  const statusChartData = useMemo(() => {
    if (!inventoryStatus) {
      return [];
    }

    const soldQuantity =
      inventoryStatus.totalDailyCount - inventoryStatus.totalRemainingCount;

    return [
      {
        name: '판매완료',
        value: soldQuantity,
        color: '#10b981', // green-500
      },
      {
        name: '남은재고',
        value: inventoryStatus.totalRemainingCount,
        color: '#3b82f6', // blue-500
      },
    ];
  }, [inventoryStatus]);

  /*
   * 재고 상태 평가 정보
   */
  const inventoryStatusInfo = useMemo(() => {
    if (!inventoryStatus) {
      return null;
    }

    const percentage = inventoryStatus.remainingPercentage;
    if (percentage >= 70) {
      return {
        status: 'good',
        color: 'secondary',
        text: '양호',
        description: '재고가 충분합니다',
      };
    } else if (percentage >= 30) {
      return {
        status: 'warning',
        color: 'default',
        text: '주의',
        description: '재고 보충을 고려해보세요',
      };
    } else {
      return {
        status: 'critical',
        color: 'destructive',
        text: '부족',
        description: '재고 보충이 필요합니다',
      };
    }
  }, [inventoryStatus]);

  /*
   * 재고 과다 상품별 위험도 계산 함수
   */
  const getHighInventoryRisk = (item: HighInventoryDdipBox) => {
    const remainingRatio = item.remainingCount / item.dailyCount;

    if (remainingRatio >= 0.8) {
      return {
        level: 'high',
        color: 'destructive',
        text: '긴급',
        percentage: remainingRatio * 100,
      };
    } else if (remainingRatio >= 0.6) {
      return {
        level: 'medium',
        color: 'default',
        text: '주의',
        percentage: remainingRatio * 100,
      };
    } else {
      return {
        level: 'low',
        color: 'secondary',
        text: '보통',
        percentage: remainingRatio * 100,
      };
    }
  };

  /*
   * 위험도 높은 순서로 정렬된 재고 과다 상품 목록
   */
  const sortedHighInventoryItems = useMemo(() => {
    if (!highInventoryItems) {
      return [];
    }

    return [...highInventoryItems].sort((a, b) => {
      const ratioA = a.remainingCount / a.dailyCount;
      const ratioB = b.remainingCount / b.dailyCount;
      return ratioB - ratioA;
    });
  }, [highInventoryItems]);

  /*
   * 전체 재고 과다 알림 수준 계산
   */
  const overallAlertLevel = useMemo(() => {
    if (!highInventoryItems || highInventoryItems.length === 0) {
      return null;
    }

    const highRiskCount = highInventoryItems.filter(
      item => getHighInventoryRisk(item).level === 'high',
    ).length;

    if (highRiskCount > 0) {
      return {
        level: 'high',
        color: 'destructive',
        message: `${highRiskCount}개 상품이 재고 과다 상태입니다`,
      };
    } else if (highInventoryItems.length > 3) {
      return {
        level: 'medium',
        color: 'default',
        message: '재고 관리가 필요한 상품들이 있습니다',
      };
    } else {
      return {
        level: 'low',
        color: 'secondary',
        message: '재고 상태가 양호합니다',
      };
    }
  }, [highInventoryItems]);

  return {
    predictionChartData,
    inventoryRiskLevel,
    avgInventoryRatio,
    statusChartData,
    inventoryStatusInfo,
    getHighInventoryRisk,
    sortedHighInventoryItems,
    overallAlertLevel,
  };
};
