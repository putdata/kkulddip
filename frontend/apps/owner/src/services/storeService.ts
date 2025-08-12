// import { apiClient } from 'common';
// import { generatePath } from 'react-router-dom';
// import { API_PATH } from '@/constants/api-path';
import type { Store, StoreListResponse } from '@/types/store';

// Mock 데이터
const mockStores: Store[] = [
  {
    storeId: 1,
    ownerId: 101,
    storeName: '강남점',
    storeAddress: '서울시 강남구 역삼동 123-45',
    description: '강남역 2번 출구 앞 딥박스 전문점',
    operatingHours: '09:00-21:00',
    phoneNumber: '02-1234-5678',
    ratingAverage: 4.5,
    reviewNum: 128,
    distanceFromUser: 0.5,
    representativeDdipboxName: '치킨마요 딥박스',
    representativeOriginalPrice: 15000,
    representativeSalePrice: 9900,
    storeProfileImage: '/images/stores/gangnam.jpg',
    active: true,
  },
  {
    storeId: 2,
    ownerId: 102,
    storeName: '홍대점',
    storeAddress: '서울시 마포구 홍익로 67-89',
    description: '홍대입구역 9번 출구 근처',
    operatingHours: '10:00-22:00',
    phoneNumber: '02-2345-6789',
    ratingAverage: 4.2,
    reviewNum: 89,
    distanceFromUser: 1.2,
    representativeDdipboxName: '불고기 딥박스',
    representativeOriginalPrice: 13000,
    representativeSalePrice: 8500,
    storeProfileImage: '/images/stores/hongdae.jpg',
    active: true,
  },
  {
    storeId: 3,
    ownerId: 103,
    storeName: '신촌점',
    storeAddress: '서울시 서대문구 신촌로 101-23',
    description: '신촌역 3번 출구 도보 2분',
    operatingHours: '08:30-20:30',
    phoneNumber: '02-3456-7890',
    ratingAverage: 4.7,
    reviewNum: 156,
    distanceFromUser: 0.8,
    representativeDdipboxName: '새우튀김 딥박스',
    representativeOriginalPrice: 12000,
    representativeSalePrice: 7800,
    storeProfileImage: '/images/stores/sinchon.jpg',
    active: true,
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
    const activeStores = mockStores.filter(store => store.active);
    return Promise.resolve({
      stores: activeStores,
      totalCount: activeStores.length,
    });
  },

  /**
   * 특정 가게 정보 조회
   */
  getStore: (storeId: number): Promise<Store> => {
    // const path = generatePath(API_PATH.STORE_DETAIL, { storeId });
    // return apiClient.get<Store>(path);
    const store = mockStores.find(s => s.storeId === storeId);
    if (store === undefined) {
      return Promise.reject(new Error('Store not found'));
    }
    return Promise.resolve(store);
  },
};
