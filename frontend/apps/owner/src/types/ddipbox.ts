export interface DdipBox {
  ddipboxId: number;
  storeId: number;
  ddipboxName: string;
  description: string;
  originalPrice: number;
  salePrice: number;
  discountRate: number;
  remainingQuantity: number;
  totalQuantity: number;
  isActive: boolean;
  ddipboxImages: string[];
  ingredients: string[];
  allergyInfo: string | null;
  nutritionInfo: string | null;
  expirationDate: string;
  pickupStartTime: string;
  pickupEndTime: string;
  category: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

export interface DdipBoxListResponse {
  ddipboxes: DdipBox[];
  totalCount: number;
  activeCount: number;
  inactiveCount: number;
  hasNext: boolean;
  nextCursor: string | null;
}

export interface CreateDdipBoxRequest {
  ddipboxName: string;
  description: string;
  originalPrice: number;
  salePrice: number;
  totalQuantity: number;
  ingredients: string[];
  allergyInfo?: string;
  nutritionInfo?: string;
  expirationDate: string;
  pickupStartTime: string;
  pickupEndTime: string;
  category: string;
  tags?: string[];
  ddipboxImages?: string[];
}

export interface CreateDdipBoxResponse {
  ddipboxId: number;
  storeId: number;
  ddipboxName: string;
  description: string;
  originalPrice: number;
  salePrice: number;
  discountRate: number;
  remainingQuantity: number;
  totalQuantity: number;
  isActive: boolean;
  ddipboxImages: string[];
  ingredients: string[];
  allergyInfo: string | null;
  nutritionInfo: string | null;
  expirationDate: string;
  pickupStartTime: string;
  pickupEndTime: string;
  category: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

export interface UpdateDdipBoxRequest {
  ddipboxName?: string;
  description?: string;
  originalPrice?: number;
  salePrice?: number;
  totalQuantity?: number;
  ingredients?: string[];
  allergyInfo?: string;
  nutritionInfo?: string;
  expirationDate?: string;
  pickupStartTime?: string;
  pickupEndTime?: string;
  category?: string;
  tags?: string[];
  ddipboxImages?: string[];
}

export interface UpdateDdipBoxResponse {
  ddipboxId: number;
  storeId: number;
  ddipboxName: string;
  description: string;
  originalPrice: number;
  salePrice: number;
  discountRate: number;
  remainingQuantity: number;
  totalQuantity: number;
  isActive: boolean;
  ddipboxImages: string[];
  ingredients: string[];
  allergyInfo: string | null;
  nutritionInfo: string | null;
  expirationDate: string;
  pickupStartTime: string;
  pickupEndTime: string;
  category: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ToggleDdipBoxStatusRequest {
  isActive: boolean;
}

export interface ToggleDdipBoxStatusResponse {
  ddipboxId: number;
  isActive: boolean;
  updatedAt: string;
}

export interface BulkUpdateDdipBoxRequest {
  ddipboxIds: number[];
  updates: {
    isActive?: boolean;
    salePrice?: number;
    totalQuantity?: number;
    expirationDate?: string;
    pickupStartTime?: string;
    pickupEndTime?: string;
  };
}

export interface BulkUpdateDdipBoxResponse {
  updatedCount: number;
  updatedDdipboxIds: number[];
  updatedAt: string;
}
