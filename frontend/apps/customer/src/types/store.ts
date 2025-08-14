/**
 * 가게 정보 데이터 타입
 */
export type Store = {
  storeId: number;
  ownerId: number;
  storeName: string;
  storeAddress: string;
  description: string;
  operatingHours: string;
  phone: string;
  ratingAverage: number;
  reviewCount: number;
  distanceFromUser: number;
  representativeDdipboxName: string;
  representativeOriginalPrice: number;
  representativeSalePrice: number;
  storeProfileImage: string;
  isActive: boolean;
};

/**
 * 가게 목록 응답 데이터 타입 (페이지네이션 포함)
 */
export type StoreListResponse = {
  content: Store[];
  hasNext: boolean;
  nextCursor: string | null;
  actualSize: number;
};

/**
 * 가게 목록 조회 파라미터 타입
 */
export type StoreListParams = {
  page?: number;
  size?: number;
  sort?: string;
  category?: string;
  searchKeyword?: string;
  latitude?: number;
  longitude?: number;
  radius?: number;
};

export interface DdipBoxItem {
  itemId: number;
  ddipboxItemName: string;
  originalPrice: number;
  itemQuantity: number;
  weight: number;
}

export interface DdipBox {
  ddipboxId: number;
  storeId: number;
  ddipboxName: string;
  description: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  discountRate: number;
  dailyQuantity: number;
  remainingQuantity: number;
  maxPerCustomer: number;
  isActive: boolean;
  soldOut: boolean;
  items: DdipBoxItem[];
}

export interface StoreDetail {
  storeId: number;
  ownerId: number;
  storeName: string;
  storeAddress: string;
  description: string;
  operatingHours: string;
  phoneNumber: string;
  ratingAverage: number;
  reviewCount: number;
  businessNumber: string;
  storeProfileImage: string;
  latitude: number;
  longitude: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
  ddipBoxes: DdipBox[];
}
