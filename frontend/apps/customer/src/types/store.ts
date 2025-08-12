export interface DdipBoxItem {
  itemId: number;
  ddipboxItemName: string;
  originalPrice: number;
  itemQuantity: number;
}

export interface DdipBoxSummaryDto {
  ddipboxId: number;
  ddipboxName: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  remainingQuantity: number;
  active: boolean;
  isRandom: boolean;
  ddipBoxItem: DdipBoxItem[]; // isRandom이 True면 약 8~10개, False 면 약 2~3개
}

export interface StoreDetailDto {
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
  ddipBoxes: DdipBoxSummaryDto[];
}
