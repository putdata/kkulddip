export interface StoreApiParams {
  customerId: number;
  userLatitude?: number;
  userLongitude?: number;
  sortBy?:
    | 'NAME_ASC'
    | 'CREATED_DESC'
    | 'DISTANCE_ASC'
    | 'RATING_DESC'
    | 'CATEGORY';
  size?: number; // 1-100
  cursor?: string;
}

// 매장 정보
export interface Store {
  favoriteId: number;
  storeId: number;
  storeName: string;
  storeProfileImage: string;
  representationDdipboxName: string;
  representationDdipboxProfileImage: string;
  reviewRating: number;
  distanceFromCustomer: number;
  category: string;
  createdAt: string; // ISO 8601 날짜 문자열
}

// 메타데이터
export interface StoreMetadata {
  sortBy: string;
  sortDirection: string;
  totalEstimate: number;
  searchKeyword: string;
  category: string;
}

// 페이지네이션 정보를 포함한 응답 body
export interface StoreResponseBody {
  content: Store[];
  hasNext: boolean;
  size: number;
  actualSize: number;
  isFirst: boolean;
  isLast: boolean;
  cursor: string;
  metadata: StoreMetadata;
}

// 전체 API 응답
export interface StoreApiResponse {
  success: boolean;
  status: number;
  body: StoreResponseBody;
}

// 좋아요 삭제 요청 파라미터
export interface DeleteLikeParams {
  customerId: number;
  storeId: number;
}

// 좋아요 삭제 응답
export interface DeleteLikeResponse {
  success: boolean;
  message?: string;
}

// 좋아요 요청
export interface AddLikeParams {
  customerId: number;
  storeId: number;
}

// 좋아요 응답
export interface AddFavoriteResponse {
  success: boolean;
  status: number;
  body: {
    favoriteId: number;
    customerId: number;
    storeId: number;
    storeName: string;
    addedAt: string;
  };
}
