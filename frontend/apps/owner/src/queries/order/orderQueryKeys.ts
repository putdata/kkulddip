/**
 * Order 관련 Query Keys
 * TanStack Query 캐싱을 위한 쿼리 키 정의
 */
export const orderQueryKeys = {
  all: ['orders'] as const,
  pending: () => [...orderQueryKeys.all, 'pending'] as const,
  pendingByStore: (storeId: number) =>
    [...orderQueryKeys.pending(), storeId] as const,
} as const;
