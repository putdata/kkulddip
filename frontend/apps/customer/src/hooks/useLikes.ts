import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { likeService } from '@/services/likeService';
import type { DeleteLikeParams } from '@/types/likedStore';
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

export const useDeleteFavorite = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (params: DeleteLikeParams) =>
      likeService.deleteFavorite(params),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['likes'] });
    },
  });
};
