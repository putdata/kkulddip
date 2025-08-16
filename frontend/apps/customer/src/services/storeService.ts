import { apiClient } from 'common';
import type { DdipBox, StoreDetail, StoreListResponse } from '@/types/store'; // 타입은 나중에 만들어야 함
import { API_PATH } from '@/constants/api-path';

export interface StoreSearchParams {
  keyword: string;
  userLatitude?: number;
  userLongitude?: number;
  sortBy?: 'id' | 'created_at' | 'rating' | 'distance';
  size?: number;
  cursor?: string;
}

export interface StoreListParams {
  userLatitude?: number;
  userLongitude?: number;
  sortBy?: 'id' | 'created_at' | 'rating' | 'distance';
  size?: number;
  cursor?: string;
}

export class StoreService {
  static async getStoreListResponse(
    latitude?: number,
    longitude?: number,
  ): Promise<StoreListResponse> {
    const searchParams = new URLSearchParams();

    if (latitude !== undefined) {
      searchParams.append('userLatitude', latitude.toString());
    }

    if (longitude !== undefined) {
      searchParams.append('userLongitude', longitude.toString());
    }

    const url = searchParams.toString()
      ? `${API_PATH.STORES}?${searchParams.toString()}`
      : API_PATH.STORES;

    return apiClient.get(url);
  }

  static async getStoreListWithPagination(
    params: StoreListParams,
  ): Promise<StoreListResponse> {
    const searchParams = new URLSearchParams();

    if (params.userLatitude !== undefined) {
      searchParams.append('userLatitude', params.userLatitude.toString());
    }

    if (params.userLongitude !== undefined) {
      searchParams.append('userLongitude', params.userLongitude.toString());
    }

    if (params.sortBy) {
      searchParams.append('sortBy', params.sortBy);
    }

    if (params.size) {
      searchParams.append('size', params.size.toString());
    }

    if (params.cursor) {
      searchParams.append('cursor', params.cursor);
    }

    const url = searchParams.toString()
      ? `${API_PATH.STORES}?${searchParams.toString()}`
      : API_PATH.STORES;

    return apiClient.get(url);
  }

  static async getStoreDetail(storeId: string): Promise<StoreDetail> {
    return apiClient.get(API_PATH.STORE_DETAIL(storeId));
  }

  static async getDdipBoxes(storeId: string): Promise<DdipBox[]> {
    return apiClient.get(API_PATH.STORE_DDIPBOXES(storeId));
  }

  static async searchStores(
    params: StoreSearchParams,
  ): Promise<StoreListResponse> {
    const searchParams = new URLSearchParams();

    searchParams.append('keyword', params.keyword);

    if (params.userLatitude !== undefined) {
      searchParams.append('userLatitude', params.userLatitude.toString());
    }

    if (params.userLongitude !== undefined) {
      searchParams.append('userLongitude', params.userLongitude.toString());
    }

    if (params.sortBy) {
      searchParams.append('sortBy', params.sortBy);
    }

    if (params.size) {
      searchParams.append('size', params.size.toString());
    }

    if (params.cursor) {
      searchParams.append('cursor', params.cursor);
    }

    return apiClient.get(
      `${API_PATH.STORES_SEARCH}?${searchParams.toString()}`,
    );
  }
}
