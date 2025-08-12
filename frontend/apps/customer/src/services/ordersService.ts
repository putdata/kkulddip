import { apiClient } from 'common';
import { type OrderFoodItem } from '@/types/orderFood';
import { API_PATH } from '@/constants/api-path';

export const getOrders = async (): Promise<OrderFoodItem[]> => {
  const response = await apiClient.get<OrderFoodItem[]>(
    API_PATH.ORDERS_MY_HISTORY,
  );
  return response;
};
