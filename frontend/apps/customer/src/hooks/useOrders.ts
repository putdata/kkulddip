import { getOrders } from '@/services/ordersService';
import { useQuery } from '@tanstack/react-query';

export const useOrders = () => {
  return useQuery({
    queryKey: ['orders'],
    queryFn: getOrders,
  });
};
