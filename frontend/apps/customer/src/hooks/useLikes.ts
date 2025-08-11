import { useQuery } from '@tanstack/react-query';
import { likeService } from '@/services/likeService';
import useGeolocation from './useGeolocation';

export const useLikes = (customerId: number) => {
  const location = useGeolocation();

  return useQuery({
    queryKey: ['likes', customerId, location.coordinate],
    queryFn: async () => {
      const data = await likeService.getStores({
        customerId,
        userLatitude: location.coordinate?.latitude,
        userLongitude: location.coordinate?.longitude,
      });
      return data.content;
    },
    enabled:
      location.isLoaded && Boolean(location.coordinate) && !location.error,
  });
};
