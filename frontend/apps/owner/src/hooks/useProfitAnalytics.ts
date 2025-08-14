import { useMemo } from 'react';
import type { ProfitMarginAnalysis } from '@/types/analytics';

/**
 * 수익률 분석 차트 데이터 처리 훅
 *
 * @description
 * 수익률 구간별 분석, 수익 상태 평가 등 수익률 관련 차트의 비즈니스 로직을 관리합니다.
 *
 * @param profitAnalysis 수익률 분석 데이터
 * @returns 수익률 분석에 필요한 처리된 데이터와 계산 결과
 */
export const useProfitAnalytics = (profitAnalysis?: ProfitMarginAnalysis) => {
  /*
   * 차트 색상 배열 (높은 수익 -> 손실 순서)
   */
  const COLORS = useMemo(
    () => [
      '#10b981', // emerald-500 - 높은 수익
      '#3b82f6', // blue-500 - 보통 수익
      '#f59e0b', // amber-500 - 낮은 수익
      '#ef4444', // red-500 - 손실
    ],
    [],
  );

  /*
   * 수익률 구간별 도넛 차트용 데이터
   */
  const chartData = useMemo(() => {
    if (!profitAnalysis) {
      return [];
    }

    return profitAnalysis.profitByMarginRanges.map((range, index) => ({
      name: range.marginRange,
      value: range.salesAmount,
      productCount: range.productCount,
      color: COLORS[index % COLORS.length],
    }));
  }, [profitAnalysis, COLORS]);

  /*
   * 수익률 상태 평가 정보 (우수/양호/보통/개선필요)
   */
  const profitStatus = useMemo(() => {
    if (!profitAnalysis) {
      return null;
    }

    const percentage = profitAnalysis.profitMarginPercentage;
    if (percentage >= 30) {
      return {
        status: 'excellent',
        color: 'default',
        text: '우수',
      };
    } else if (percentage >= 20) {
      return {
        status: 'good',
        color: 'secondary',
        text: '양호',
      };
    } else if (percentage >= 10) {
      return {
        status: 'fair',
        color: 'outline',
        text: '보통',
      };
    } else {
      return {
        status: 'poor',
        color: 'destructive',
        text: '개선필요',
      };
    }
  }, [profitAnalysis]);

  /*
   * 툴팁용 매출 비율 계산 함수
   */
  const calculateSalesPercentage = (value: number) => {
    if (!profitAnalysis) {
      return 0;
    }
    return (value / profitAnalysis.totalRevenue) * 100;
  };

  return {
    chartData,
    profitStatus,
    calculateSalesPercentage,
    COLORS,
  };
};
