import { apiClient } from 'common';
import type { StoreApiParams, StoreResponseBody, DeleteLikeParams, DeleteLikeResponse } from '@/types/likedStore';
import { API_PATH } from '@/constants/api-path';

export const likeService = {
  getStores: async (params: StoreApiParams): Promise<StoreResponseBody> => {
    return apiClient.get(API_PATH.FAVORITES, params);
  },
  
  deleteFavorite: async (params: DeleteLikeParams): Promise<DeleteLikeResponse> => {
    return apiClient.delete(API_PATH.FAVORITES_DELETE, params);
  },
};
