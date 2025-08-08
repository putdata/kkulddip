/**
 * Store 관련 Query Keys
 * TanStack Query 캐싱을 위한 쿼리 키 정의
 */
export const storeQueryKeys = {
  all: ['stores'] as const,
  lists: () => [...storeQueryKeys.all, 'list'] as const,
  list: (filters?: Record<string, unknown>) =>
    [...storeQueryKeys.lists(), { filters }] as const,
  details: () => [...storeQueryKeys.all, 'detail'] as const,
  detail: (id: string) => [...storeQueryKeys.details(), id] as const,
} as const;
