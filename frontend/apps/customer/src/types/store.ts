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
