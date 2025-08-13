export interface Store {
  storeId: number;
  ownerId: number;
  storeName: string;
  storeAddress: string;
  description: string;
  operatingHours: string;
  phoneNumber: string;
  ratingAverage: number;
  reviewNum: number;
  distanceFromUser: number;
  representativeDdipboxName: string;
  representativeOriginalPrice: number;
  representativeSalePrice: number;
  storeProfileImage: string;
  active: boolean;
}

export interface StoreListResponse {
  stores: Store[];
  totalCount: number;
}
