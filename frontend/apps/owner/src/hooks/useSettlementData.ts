import { useState } from 'react';
import { useStoreSettlement, useMonthlySettlement } from '@/queries/settlement';

/**
 * 정산 데이터 관리를 위한 커스텀 훅
 *
 * @param storeId - 매장 ID
 * @returns 정산 데이터 및 날짜 선택 상태
 */
export const useSettlementData = (storeId: number) => {
  const currentDate = new Date();
  const [selectedYear, setSelectedYear] = useState(currentDate.getFullYear());
  const [selectedMonth, setSelectedMonth] = useState(
    currentDate.getMonth() + 1,
  );

  // 현재 선택된 월 정산 데이터
  const { data: currentSettlement, isLoading: isCurrentLoading } =
    useStoreSettlement(storeId, selectedYear, selectedMonth);

  // 최근 6개월 정산 데이터
  const endYear = selectedYear;
  const endMonth = selectedMonth;
  const startYear = selectedMonth <= 6 ? selectedYear - 1 : selectedYear;
  const startMonth = selectedMonth <= 6 ? selectedMonth + 6 : selectedMonth - 6;

  const { data: monthlySettlement, isLoading: isMonthlyLoading } =
    useMonthlySettlement(storeId, startYear, startMonth, endYear, endMonth);

  return {
    selectedYear,
    selectedMonth,
    setSelectedYear,
    setSelectedMonth,
    currentSettlement,
    monthlySettlement,
    isCurrentLoading,
    isMonthlyLoading,
  };
};
