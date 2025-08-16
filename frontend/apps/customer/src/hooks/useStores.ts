import { useQuery } from '@tanstack/react-query';
import { StoreService } from '@/services/storeService';
import useGeolocation from './useGeolocation';

export const useStores = () => {
  const location = useGeolocation();

  return useQuery({
    queryKey: ['store', location.coordinate],
    queryFn: () =>
      StoreService.getStoreListResponse(
        location.coordinate?.latitude,
        location.coordinate?.longitude,
      ),
    enabled: location.isLoaded,
  });
};
