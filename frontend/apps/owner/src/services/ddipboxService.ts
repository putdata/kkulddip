import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  DdipBoxListResponse,
  CreateDdipBoxRequest,
  CreateDdipBoxResponse,
  UpdateDdipBoxRequest,
  UpdateDdipBoxResponse,
  UpdateDdipBoxQuantityRequest,
  UpdateDdipBoxQuantityResponse,
  UpdateDdipBoxStatusRequest,
  UpdateDdipBoxStatusResponse,
} from '@/types/ddipbox';

/**
 * DdipBox 관련 API 서비스
 */
export const ddipboxService = {
  /**
   * 특정 가게의 딥박스 목록 조회
   * 가게에 등록된 모든 딥박스를 조회합니다.
   */
  getDdipboxList: (
    storeId: number,
    params?: { cursor?: string; limit?: number },
  ): Promise<DdipBoxListResponse> => {
    return apiClient.get<DdipBoxListResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.LIST(storeId),
      params,
    );
  },

  /**
   * 딥박스 등록
   * 새로운 딥박스를 등록합니다. 사장님만 사용할 수 있습니다.
   */
  createDdipbox: (
    storeId: number,
    data: CreateDdipBoxRequest,
  ): Promise<CreateDdipBoxResponse> => {
    return apiClient.post<CreateDdipBoxResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.CREATE(storeId),
      data,
    );
  },

  /**
   * 딥박스 정보 수정
   * 기존 딥박스의 정보를 수정합니다. 사장님만 사용할 수 있습니다.
   */
  updateDdipbox: (
    storeId: number,
    ddipboxId: number,
    data: UpdateDdipBoxRequest,
  ): Promise<UpdateDdipBoxResponse> => {
    return apiClient.put<UpdateDdipBoxResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.UPDATE(storeId, ddipboxId),
      data,
    );
  },

  /**
   * 딥박스 재고 수량 업데이트
   * 딥박스의 재고 수량을 업데이트합니다.
   */
  updateDdipboxQuantity: (
    storeId: number,
    ddipboxId: number,
    data: UpdateDdipBoxQuantityRequest,
  ): Promise<UpdateDdipBoxQuantityResponse> => {
    return apiClient.patch<UpdateDdipBoxQuantityResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.UPDATE_QUANTITY(storeId, ddipboxId),
      data,
    );
  },

  /**
   * 딥박스 삭제
   * 딥박스를 삭제합니다. 사장님만 사용할 수 있습니다.
   */
  deleteDdipbox: (storeId: number, ddipboxId: number): Promise<void> => {
    return apiClient.delete<void>(
      API_PATH.DDIPBOX_MANAGEMENT.DELETE(storeId, ddipboxId),
    );
  },

  /**
   * 딥박스 활성화/비활성화 토글
   * 딥박스의 판매 상태를 변경합니다. 사장님만 사용할 수 있습니다.
   */
  toggleDdipboxStatus: (
    storeId: number,
    ddipboxId: number,
    data: UpdateDdipBoxStatusRequest,
  ): Promise<UpdateDdipBoxStatusResponse> => {
    return apiClient.patch<UpdateDdipBoxStatusResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.TOGGLE_STATUS(storeId, ddipboxId),
      data,
    );
  },
};
