/**
 * Stream 관련 Query Keys
 * TanStack Query 캐싱을 위한 쿼리 키 정의
 */
export const streamQueryKeys = {
  all: ['streams'] as const,
  lists: () => [...streamQueryKeys.all, 'list'] as const,
  myStreams: () => [...streamQueryKeys.lists(), 'my'] as const,
  store: (storeId: number) =>
    [...streamQueryKeys.lists(), 'store', storeId] as const,
  details: () => [...streamQueryKeys.all, 'detail'] as const,
  detail: (id: number) => [...streamQueryKeys.details(), id] as const,
} as const;
