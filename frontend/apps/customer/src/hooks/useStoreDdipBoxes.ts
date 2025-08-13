import { useQuery } from '@tanstack/react-query';
import { StoreService } from '@/services/storeService';

export const useStoreDdipBoxes = (storeId: string) => {
  return useQuery({
    queryKey: ['store', storeId, 'ddipboxes'],
    queryFn: () => StoreService.getDdipBoxes(storeId),
  });
};
