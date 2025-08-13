/**
 * 딥박스 정보
 */
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

/**
 * 딥박스 생성 요청
 */
export interface CreateDdipBoxRequest {
  ddipboxName: string;
  description?: string;
  category: string;
  originalPrice: number;
  salePrice: number;
  dailyQuantity: number;
  maxPerCustomer: number;
}

/**
 * 딥박스 수정 요청
 */
export interface UpdateDdipBoxRequest {
  ddipboxName?: string;
  description?: string;
  category?: string;
  originalPrice?: number;
  salePrice?: number;
  dailyQuantity?: number;
  maxPerCustomer?: number;
}

/**
 * 딥박스 수량 수정 요청
 */
export interface UpdateDdipBoxQuantityRequest {
  remainingQuantity?: number;
  dailyQuantity?: number;
  resetRemaining?: boolean;
}

/**
 * 딥박스 상태 변경 요청
 */
export type UpdateDdipBoxStatusRequest = {
  isActive: boolean;
  reason?: string;
};

/**
 * 딥박스 관리 응답
 */
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

/**
 * 딥박스 목록 응답
 */
export type DdipBoxListResponse = DdipBox[];

/**
 * 딥박스 생성 응답
 */
export type CreateDdipBoxResponse = DdipBoxManagementResponse;

/**
 * 딥박스 수정 응답
 */
export type UpdateDdipBoxResponse = DdipBoxManagementResponse;

/**
 * 딥박스 수량 수정 응답
 */
export type UpdateDdipBoxQuantityResponse = DdipBoxManagementResponse;

/**
 * 딥박스 상태 변경 응답
 */
export type UpdateDdipBoxStatusResponse = DdipBoxManagementResponse;
