import { useState } from 'react';
import { useSettlementSummary } from '@/queries/settlement';

/**
 * Owner 전체 매장 통합 정산 데이터 관리를 위한 커스텀 훅
 *
 * @returns 전체 매장 통합 정산 데이터 및 날짜 선택 상태
 */
export const useOwnerSettlementData = () => {
  const currentDate = new Date();
  const [selectedYear, setSelectedYear] = useState(currentDate.getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(
    currentDate.getMonth() + 1,
  );

  const { data: settlementSummary, isLoading } = useSettlementSummary(
    selectedYear,
    selectedMonth,
  );

  return {
    selectedYear,
    selectedMonth,
    setSelectedYear,
    setSelectedMonth,
    settlementSummary,
    isLoading,
  };
};
