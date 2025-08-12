import { apiClient } from 'common';
import { API_PATH } from '@/constants/api-path';
import type {
  DdipBox,
  DdipBoxListResponse,
  CreateDdipBoxRequest,
  CreateDdipBoxResponse,
  UpdateDdipBoxRequest,
  UpdateDdipBoxResponse,
  ToggleDdipBoxStatusRequest,
  ToggleDdipBoxStatusResponse,
  BulkUpdateDdipBoxRequest,
  BulkUpdateDdipBoxResponse,
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
   * 딥박스 상세 조회
   * 특정 딥박스의 상세 정보를 조회합니다.
   */
  getDdipbox: (ddipboxId: number): Promise<DdipBox> => {
    return apiClient.get<DdipBox>(
      API_PATH.DDIPBOX_MANAGEMENT.DETAIL(ddipboxId),
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
    ddipboxId: number,
    data: UpdateDdipBoxRequest,
  ): Promise<UpdateDdipBoxResponse> => {
    return apiClient.put<UpdateDdipBoxResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.UPDATE(ddipboxId),
      data,
    );
  },

  /**
   * 딥박스 삭제
   * 딥박스를 삭제합니다. 사장님만 사용할 수 있습니다.
   */
  deleteDdipbox: (ddipboxId: number): Promise<void> => {
    return apiClient.delete<void>(
      API_PATH.DDIPBOX_MANAGEMENT.DELETE(ddipboxId),
    );
  },

  /**
   * 딥박스 활성화/비활성화 토글
   * 딥박스의 판매 상태를 변경합니다. 사장님만 사용할 수 있습니다.
   */
  toggleDdipboxStatus: (
    ddipboxId: number,
    data: ToggleDdipBoxStatusRequest,
  ): Promise<ToggleDdipBoxStatusResponse> => {
    return apiClient.put<ToggleDdipBoxStatusResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.TOGGLE_STATUS(ddipboxId),
      data,
    );
  },

  /**
   * 딥박스 일괄 수정
   * 여러 딥박스의 정보를 한 번에 수정합니다. 사장님만 사용할 수 있습니다.
   */
  bulkUpdateDdipboxes: (
    storeId: number,
    data: BulkUpdateDdipBoxRequest,
  ): Promise<BulkUpdateDdipBoxResponse> => {
    return apiClient.put<BulkUpdateDdipBoxResponse>(
      API_PATH.DDIPBOX_MANAGEMENT.BULK_UPDATE(storeId),
      data,
    );
  },
};
