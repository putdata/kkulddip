import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  Store,
  StoreListResponse,
  CreateStoreRequest,
  CreateStoreResponse,
} from '@/types/store';

/**
 * Store 관련 API 서비스
 */
export const storeService = {
  /**
   * 내 가게 목록 조회
   * 현재 로그인한 Owner가 소유한 가게 목록을 조회합니다.
   */
  getMyStores: (): Promise<StoreListResponse> => {
    return apiClient.get<StoreListResponse>(API_PATH.OWNER_STORES);
  },

  /**
   * 특정 가게 상세 조회
   * Owner가 소유한 특정 가게의 상세 정보를 조회합니다.
   */
  getStore: (storeId: number): Promise<Store> => {
    return apiClient.get<Store>(API_PATH.OWNER_STORE_DETAIL(storeId));
  },

  /**
   * 가게 등록
   * 새로운 가게를 등록합니다. 사장님만 사용할 수 있습니다.
   */
  createStore: (data: CreateStoreRequest): Promise<CreateStoreResponse> => {
    return apiClient.post<CreateStoreResponse>(API_PATH.CREATE_STORE, data);
  },
};
