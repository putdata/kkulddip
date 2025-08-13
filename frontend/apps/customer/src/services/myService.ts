import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';

// API 응답 타입들
export interface CustomerProfile {
  customerId: number;
  email: string;
  name: string;
  profileImageUrl: string;
  address: string;
  latitude: number;
  longitude: number;
  level: string;
  createdAt: string;
  lastActiveAt: string;
}

export interface CustomerStats {
  customerId: number;
  level: string;
  totalOrder: number;
  totalMoneySaved: number;
  totalCo2Saved: number;
  ordersUntilNextLevel: number;
  nextLevel: string;
}

export const myService = {
  // 프로필 정보 가져오기
  getProfile: () => apiClient.get<CustomerProfile>(API_PATH.PROFILE),

  // 고객 통계 정보 가져오기
  getStats: () => apiClient.get<CustomerStats>(API_PATH.STATS),
};
