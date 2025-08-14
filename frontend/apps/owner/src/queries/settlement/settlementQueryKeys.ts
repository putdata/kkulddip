/**
 * Settlement 관련 Query Keys
 */
export const settlementQueryKeys = {
  all: ['settlement'] as const,
  summary: (year: number, month: number) =>
    [...settlementQueryKeys.all, 'summary', year, month] as const,
  store: (storeId: number, year: number, month: number) =>
    [...settlementQueryKeys.all, 'store', storeId, year, month] as const,
  monthly: (
    storeId: number,
    startYear: number,
    startMonth: number,
    endYear: number,
    endMonth: number,
  ) =>
    [
      ...settlementQueryKeys.all,
      'monthly',
      storeId,
      startYear,
      startMonth,
      endYear,
      endMonth,
    ] as const,
};