import { apiClient } from 'common';
import type { StoreApiParams, StoreResponseBody } from '@/types/likedStore';

export const likeService = {
  getStores: async (params: StoreApiParams): Promise<StoreResponseBody> => {
    return apiClient.get('/v1/favorites', params);
  },
};
