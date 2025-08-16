import { useInfiniteQuery } from '@tanstack/react-query';
import { StoreService, type StoreListParams } from '@/services/storeService';
import type { StoreListResponse } from '@/types/store';
import useGeolocation from './useGeolocation';

export const useStores = () => {
  const location = useGeolocation();
  const latitude = location.coordinate?.latitude;
  const longitude = location.coordinate?.longitude;

  return useInfiniteQuery({
    queryKey: ['stores', latitude, longitude],
    queryFn: ({ pageParam }) => {
      const params: StoreListParams = {
        userLatitude: latitude ?? undefined,
        userLongitude: longitude ?? undefined,
        sortBy: 'rating',
        size: 10,
        cursor: pageParam as string | undefined,
      };
      return StoreService.getStoreListWithPagination(params);
    },
    getNextPageParam: (lastPage: StoreListResponse) => {
      return lastPage.hasNext ? lastPage.cursor : undefined;
    },
    initialPageParam: undefined,
    enabled: location.isLoaded,
    staleTime: 5 * 60 * 1000, // 5분
  });
};
