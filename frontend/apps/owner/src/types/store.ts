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

export interface PaginatedResponse {
  totalCount: number;
  activeCount: number;
  inactiveCount: number;
  hasNext: boolean;
  nextCursor: string | null;
}

export interface StoreListResponse extends PaginatedResponse {
  stores: Store[];
}

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

export interface UpdateStoreRequest {
  storeName?: string;
  phone?: string;
  description?: string;
  operatingHours?: string;
  storeAddress?: string;
  latitude?: number;
  longitude?: number;
}

export interface UpdateStoreStatusRequest {
  isActive: boolean;
  reason?: string;
}

// 하위 호환성을 위한 별칭
export type CreateStoreResponse = StoreManagementResponse;
export type UpdateStoreResponse = StoreManagementResponse;
export type UpdateStoreStatusResponse = StoreManagementResponse;
