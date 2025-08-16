import { apiClient } from 'common';
import type {
  StoreApiParams,
  StoreResponseBody,
  LikeParams,
  DeleteLikeResponse,
  AddLikeResponse,
} from '@/types/likedStore';
import { API_PATH } from '@/constants/api-path';

export const likeService = {
  getStores: async (params: StoreApiParams): Promise<StoreResponseBody> => {
    return apiClient.get(API_PATH.FAVORITES, params);
  },

  deleteFavorite: async (params: LikeParams): Promise<DeleteLikeResponse> => {
    return apiClient.delete(API_PATH.FAVORITES_DELETE, params);
  },

  addFavorite: async (params: LikeParams): Promise<AddLikeResponse> => {
    return apiClient.post(API_PATH.FAVORITES, params);
  },

  checkIsFavorite: async (
    params: LikeParams,
  ): Promise<{ isFavorite: boolean }> => {
    return apiClient.get(API_PATH.FAVORITES_CHECK, params);
  },
};
