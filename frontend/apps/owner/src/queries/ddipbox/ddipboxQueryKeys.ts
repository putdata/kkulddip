/**
 * DdipBox 관련 Query Keys
 * TanStack Query 캐싱을 위한 쿼리 키 정의
 */
export const ddipboxQueryKeys = {
  all: ['ddipboxes'] as const,
  lists: () => [...ddipboxQueryKeys.all, 'list'] as const,
  list: (storeId: number, params?: { cursor?: string; limit?: number }) =>
    [...ddipboxQueryKeys.lists(), storeId, { params }] as const,
  details: () => [...ddipboxQueryKeys.all, 'detail'] as const,
  detail: (ddipboxId: number) =>
    [...ddipboxQueryKeys.details(), ddipboxId] as const,
  store: (storeId: number) =>
    [...ddipboxQueryKeys.all, 'store', storeId] as const,
} as const;
