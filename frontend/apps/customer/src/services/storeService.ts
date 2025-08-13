import { apiClient } from 'common';
import type { DdipBox, StoreDetail } from '@/types/store'; // 타입은 나중에 만들어야 함
import { API_PATH } from '@/constants/api-path';

export class StoreService {
  static async getStoreDetail(storeId: string): Promise<StoreDetail> {
    return apiClient.get(API_PATH.STORE_DETAIL(storeId));
  }

  static async getDdipBoxes(storeId: string): Promise<DdipBox[]> {
    return apiClient.get(API_PATH.STORE_DDIPBOXES(storeId));
  }
}
