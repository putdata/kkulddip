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
  storeAddress: string;
  description: string;
  operatingHours: string;
  phone: string;
  businessNumber: string;
  latitude: number;
  longitude: number;
}

export interface CreateStoreResponse {
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
  storeProfileImage: string | null;
  latitude: number;
  longitude: number;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateStoreRequest {
  storeName?: string;
  storeAddress?: string;
  description?: string;
  operatingHours?: string;
  phone?: string;
  businessNumber?: string;
  latitude?: number;
  longitude?: number;
}

export interface UpdateStoreResponse {
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
  storeProfileImage: string | null;
  latitude: number;
  longitude: number;
  createdAt: string;
  updatedAt: string;
}

export interface ToggleStoreStatusRequest {
  isActive: boolean;
}

export interface ToggleStoreStatusResponse {
  storeId: number;
  isActive: boolean;
  updatedAt: string;
}
