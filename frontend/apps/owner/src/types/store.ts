/**
 * 스토어 정보
 */
export interface Store {
  storeId: number;
  storeName: string;
  phone: string;
  description: string;
  operatingHours: string;
  isActive: boolean;
  ratingAverage: number;
  reviewCount: number;
  businessNumber: string;
  storeAddress: string;
  storeProfileImage: string | null;
  latitude: number;
  longitude: number;
  createdAt: string;
  updatedAt: string;
  totalOrderCount: number;
  totalRevenue: number;
}

/**
 * 페이지네이션 응답
 */
export interface PaginatedResponse {
  totalCount: number;
  activeCount: number;
  inactiveCount: number;
  hasNext: boolean;
  nextCursor: string | null;
}

/**
 * 스토어 목록 응답
 */
export interface StoreListResponse extends PaginatedResponse {
  stores: Store[];
}

/**
 * 스토어 생성 요청
 */
export interface CreateStoreRequest {
  storeName: string;
  phone: string;
  description?: string;
  operatingHours?: string;
  businessNumber: string;
  storeAddress: string;
  latitude: number;
  longitude: number;
}

/**
 * 스토어 관리 응답
 */
export interface StoreManagementResponse {
  storeId: number;
  ownerId: number;
  storeName: string;
  phone: string;
  description: string;
  operatingHours: string;
  isActive: boolean;
  ratingAverage: number;
  reviewCount: number;
  businessNumber: string;
  storeAddress: string;
  storeProfileImage: string;
  latitude: number;
  longitude: number;
  createdAt: string;
  updatedAt: string;
}

/**
 * 스토어 수정 요청
 */
export interface UpdateStoreRequest {
  storeName?: string;
  phone?: string;
  description?: string;
  operatingHours?: string;
  storeAddress?: string;
  latitude?: number;
  longitude?: number;
}

/**
 * 스토어 상태 변경 요청
 */
export interface UpdateStoreStatusRequest {
  isActive: boolean;
  reason?: string;
}

/**
 * 스토어 생성 응답
 */
export type CreateStoreResponse = StoreManagementResponse;

/**
 * 스토어 수정 응답
 */
export type UpdateStoreResponse = StoreManagementResponse;

/**
 * 스토어 상태 변경 응답
 */
export type UpdateStoreStatusResponse = StoreManagementResponse;
