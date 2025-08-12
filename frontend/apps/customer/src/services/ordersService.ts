import { apiClient } from 'common';
import { type OrderFoodItem } from '@/types/orderFood';

export const getOrders = async (): Promise<OrderFoodItem[]> => {
  const response = await apiClient.get<OrderFoodItem[]>('v1/orders/my-history');
  return response;
};
