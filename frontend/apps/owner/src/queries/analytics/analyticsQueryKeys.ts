/**
 * Analytics 관련 Query Keys
 */
export const analyticsQueryKeys = {
  all: ['analytics'] as const,
  sales: (storeId: number, startDate?: string, endDate?: string) =>
    [...analyticsQueryKeys.all, 'sales', storeId, startDate, endDate] as const,
  daily: (storeId: number, targetDate?: string) =>
    [...analyticsQueryKeys.all, 'daily', storeId, targetDate] as const,
};