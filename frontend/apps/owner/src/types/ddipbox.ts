export interface DdipBox {
  ddipboxId: number;
  storeId: number;
  ddipboxName: string;
  description: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  dailyQuantity: number;
  remainingQuantity: number;
  maxPerCustomer: number;
  isActive: boolean;
}

export interface CreateDdipBoxRequest {
  ddipboxName: string;
  description?: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  dailyQuantity: number;
  maxPerCustomer: number;
}

export interface UpdateDdipBoxRequest {
  ddipboxName?: string;
  description?: string;
  category?: string;
  originalPrice?: number;
  salePrice?: number;
  dailyQuantity?: number;
  maxPerCustomer?: number;
}

export interface UpdateDdipBoxQuantityRequest {
  remainingQuantity?: number;
  dailyQuantity?: number;
  resetRemaining?: boolean;
}

export type UpdateDdipBoxStatusRequest = {
  isActive: boolean;
  reason?: string;
};

export interface DdipBoxManagementResponse {
  ddipboxId: number;
  storeId: number;
  ddipboxName: string;
  description: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  dailyQuantity: number;
  remainingQuantity: number;
  maxPerCustomer: number;
  isActive: boolean;
}

export type DdipBoxListResponse = DdipBox[];
export type CreateDdipBoxResponse = DdipBoxManagementResponse;
export type UpdateDdipBoxResponse = DdipBoxManagementResponse;
export type UpdateDdipBoxQuantityResponse = DdipBoxManagementResponse;
export type UpdateDdipBoxStatusResponse = DdipBoxManagementResponse;
