import { useQuery } from '@tanstack/react-query';
import { apiClient } from 'common';

interface MyPageStat {
  totalOrder: number;
  level: string;
}

export const useMyPageStats = (customerId: number) => {
  return useQuery({
    queryKey: ['customer', customerId],
    queryFn: () => apiClient.get<MyPageStat>(`/customers/${customerId}`),
    select: data => ({
      totalOrder: data.totalOrder,
      level: data.level,
    }),
    enabled: Boolean(customerId),
  });
};
