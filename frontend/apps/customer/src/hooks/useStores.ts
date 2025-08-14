import { useQuery } from '@tanstack/react-query';
import { StoreService } from '@/services/storeService';

export const useStores = () => {
  return useQuery({
    queryKey: ['store'],
    queryFn: () => StoreService.getStoreListResponse(),
  });
};
