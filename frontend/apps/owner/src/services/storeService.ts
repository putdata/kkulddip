// import { apiClient } from 'common';
// import { generatePath } from 'react-router-dom';
// import { API_PATH } from '@/constants/api-path';
import type { Store, StoreListResponse } from '@/types/store';

// Mock 데이터
const mockStores: Store[] = [
  {
    id: '1',
    name: '강남점',
    address: '서울시 강남구 역삼동 123-45',
    description: '강남역 2번 출구 앞',
    isActive: true,
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z',
  },
  {
    id: '2',
    name: '홍대점',
    address: '서울시 마포구 홍익로 67-89',
    description: '홍대입구역 9번 출구',
    isActive: true,
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z',
  },
  {
    id: '3',
    name: '신촌점',
    address: '서울시 서대문구 신촌로 101-23',
    description: '신촌역 3번 출구',
    isActive: true,
    createdAt: '2024-01-01T00:00:00.000Z',
    updatedAt: '2024-01-01T00:00:00.000Z',
  },
];

/**
 * Store 관련 API 서비스 (Mock 데이터 사용)
 * TODO : 개발용 Mock 데이터를 반환하는 서비스 레이어
 */
export const storeService = {
  /**
   * 내 가게 목록 조회
   */
  getMyStores: (): Promise<StoreListResponse> => {
    // return apiClient.get<StoreListResponse>(API_PATH.STORE_LIST);
    const activeStores = mockStores.filter(store => store.isActive);
    return Promise.resolve({
      stores: activeStores,
      totalCount: activeStores.length,
    });
  },

  /**
   * 특정 가게 정보 조회
   */
  getStore: (storeId: string): Promise<Store> => {
    // const path = generatePath(API_PATH.STORE_DETAIL, { storeId });
    // return apiClient.get<Store>(path);
    const store = mockStores.find(s => s.id === storeId);
    if (store === undefined) {
      return Promise.reject(new Error('Store not found'));
    }
    return Promise.resolve(store);
  },
};
