import { useQuery } from '@tanstack/react-query';
import { StoreService } from '@/services/storeService';

export const useStoreDetail = (storeId: string) => {
  return useQuery({
    queryKey: ['store', storeId],
    queryFn: () => StoreService.getStoreDetail(storeId),
  });
};
