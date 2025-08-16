import { useState, useCallback } from 'react';
import { toast } from 'sonner';
import type {
  CreateDdipBoxRequest,
  UpdateDdipBoxRequest,
  DdipBox,
} from '@/types/ddipbox';

type DdipboxFormData = CreateDdipBoxRequest | UpdateDdipBoxRequest;

interface UseDdipboxFormOptions {
  initialData?: Partial<DdipboxFormData>;
  mode: 'create' | 'edit';
  originalDdipbox?: DdipBox;
}

/**
 * 띱박스 폼 관리를 위한 커스텀 훅
 *
 * @description
 * 띱박스 생성/편집 폼의 상태 관리, 입력 처리, 유효성 검증을 담당합니다.
 */
export const useDdipboxForm = ({
  initialData,
  mode,
  originalDdipbox,
}: UseDdipboxFormOptions) => {
  /**
   * 초기 폼 데이터 생성
   */
  const getInitialFormData = useCallback((): DdipboxFormData => {
    if (mode === 'create') {
      return {
        ddipboxName: '',
        description: '',
        category: '',
        originalPrice: 0,
        salePrice: 0,
        dailyQuantity: 1,
        maxPerCustomer: 1,
        ...initialData,
      } as CreateDdipBoxRequest;
    } else {
      return {
        ddipboxName: originalDdipbox?.ddipboxName || '',
        description: originalDdipbox?.description || '',
        category: originalDdipbox?.category || '',
        originalPrice: originalDdipbox?.originalPrice || 0,
        salePrice: originalDdipbox?.salePrice || 0,
        dailyQuantity: originalDdipbox?.dailyQuantity || 1,
        maxPerCustomer: originalDdipbox?.maxPerCustomer || 1,
        ...initialData,
      } as UpdateDdipBoxRequest;
    }
  }, [mode, initialData, originalDdipbox]);

  /**
   * 폼 데이터 상태
   */
  const [formData, setFormData] = useState<DdipboxFormData>(getInitialFormData);

  /**
   * 폼 입력값 변경 처리
   */
  const handleInputChange = useCallback(
    (field: keyof DdipboxFormData, value: string | number) => {
      setFormData(prev => ({
        ...prev,
        [field]:
          field.includes('Price') ||
          field.includes('Quantity') ||
          field === 'maxPerCustomer'
            ? mode === 'edit' && value === ''
              ? undefined
              : Number(value) || 0
            : value,
      }));
    },
    [mode],
  );

  /**
   * 폼 유효성 검증
   */
  const validateForm = useCallback((): boolean => {
    if (mode === 'create') {
      const data = formData as CreateDdipBoxRequest;

      // 필수 필드 검증
      if (!data.ddipboxName?.trim()) {
        toast.error('띱박스 이름을 입력해주세요.');
        return false;
      }
      if (!data.category?.trim()) {
        toast.error('카테고리를 입력해주세요.');
        return false;
      }
      if (data.originalPrice <= 0) {
        toast.error('정가는 0원보다 커야 합니다.');
        return false;
      }
      if (data.salePrice <= 0) {
        toast.error('판매가는 0원보다 커야 합니다.');
        return false;
      }
      if (data.dailyQuantity < 1) {
        toast.error('일일 수량은 1개 이상이어야 합니다.');
        return false;
      }
      if (data.maxPerCustomer < 1) {
        toast.error('고객당 최대 구매 수량은 1개 이상이어야 합니다.');
        return false;
      }

      // 비즈니스 로직 검증
      if (data.salePrice > data.originalPrice) {
        toast.error('판매가는 정가보다 클 수 없습니다.');
        return false;
      }
      if (data.maxPerCustomer > data.dailyQuantity) {
        toast.error('고객당 최대 구매 수량은 일일 수량보다 클 수 없습니다.');
        return false;
      }

      // 길이 제한 검증
      if (data.ddipboxName.length > 100) {
        toast.error('띱박스 이름은 100자 이내로 입력해주세요.');
        return false;
      }
      if (data.description && data.description.length > 1000) {
        toast.error('설명은 1000자 이내로 입력해주세요.');
        return false;
      }
      if (data.category.length > 50) {
        toast.error('카테고리는 50자 이내로 입력해주세요.');
        return false;
      }

      return true;
    } else {
      const data = formData as UpdateDdipBoxRequest;

      // 수정 모드에서는 undefined 값 허용하되, 값이 있으면 유효성 검증
      if (data.ddipboxName !== undefined && !data.ddipboxName.trim()) {
        toast.error('띱박스 이름을 입력해주세요.');
        return false;
      }
      if (data.category !== undefined && !data.category.trim()) {
        toast.error('카테고리를 입력해주세요.');
        return false;
      }
      if (data.originalPrice !== undefined && data.originalPrice <= 0) {
        toast.error('정가는 0원보다 커야 합니다.');
        return false;
      }
      if (data.salePrice !== undefined && data.salePrice <= 0) {
        toast.error('판매가는 0원보다 커야 합니다.');
        return false;
      }
      if (data.dailyQuantity !== undefined && data.dailyQuantity < 1) {
        toast.error('일일 수량은 1개 이상이어야 합니다.');
        return false;
      }
      if (data.maxPerCustomer !== undefined && data.maxPerCustomer < 1) {
        toast.error('고객당 최대 구매 수량은 1개 이상이어야 합니다.');
        return false;
      }

      // 비즈니스 로직 검증 (현재값과 새값 조합)
      const originalPrice =
        data.originalPrice ?? originalDdipbox?.originalPrice ?? 0;
      const salePrice = data.salePrice ?? originalDdipbox?.salePrice ?? 0;
      const dailyQuantity =
        data.dailyQuantity ?? originalDdipbox?.dailyQuantity ?? 1;
      const maxPerCustomer =
        data.maxPerCustomer ?? originalDdipbox?.maxPerCustomer ?? 1;

      if (salePrice > originalPrice) {
        toast.error('판매가는 정가보다 클 수 없습니다.');
        return false;
      }
      if (maxPerCustomer > dailyQuantity) {
        toast.error('고객당 최대 구매 수량은 일일 수량보다 클 수 없습니다.');
        return false;
      }

      // 길이 제한 검증
      if (data.ddipboxName && data.ddipboxName.length > 100) {
        toast.error('띱박스 이름은 100자 이내로 입력해주세요.');
        return false;
      }
      if (data.description && data.description.length > 1000) {
        toast.error('설명은 1000자 이내로 입력해주세요.');
        return false;
      }
      if (data.category && data.category.length > 50) {
        toast.error('카테고리는 50자 이내로 입력해주세요.');
        return false;
      }

      return true;
    }
  }, [formData, mode, originalDdipbox]);

  /**
   * 수정 모드에서 변경사항이 있는지 확인
   */
  const hasChanges = useCallback((): boolean => {
    if (mode === 'create' || !originalDdipbox) {
      return true;
    }

    const data = formData as UpdateDdipBoxRequest;
    return (
      data.ddipboxName !== originalDdipbox.ddipboxName ||
      data.description !== originalDdipbox.description ||
      data.category !== originalDdipbox.category ||
      data.originalPrice !== originalDdipbox.originalPrice ||
      data.salePrice !== originalDdipbox.salePrice ||
      data.dailyQuantity !== originalDdipbox.dailyQuantity ||
      data.maxPerCustomer !== originalDdipbox.maxPerCustomer
    );
  }, [formData, mode, originalDdipbox]);

  /**
   * 할인율 계산
   */
  const getDiscountRate = useCallback((): number => {
    const originalPrice = formData.originalPrice || 0;
    const salePrice = formData.salePrice || 0;

    if (originalPrice === 0) {
      return 0;
    }

    return Math.round(((originalPrice - salePrice) / originalPrice) * 100);
  }, [formData.originalPrice, formData.salePrice]);

  /**
   * 폼 초기화
   */
  const resetForm = useCallback(() => {
    setFormData(getInitialFormData());
  }, [getInitialFormData]);

  /**
   * 제출용 데이터 가져오기 (수정 모드에서는 변경된 필드만)
   */
  const getSubmitData = useCallback(() => {
    if (mode === 'create') {
      const data = formData as CreateDdipBoxRequest;
      return {
        ...data,
        ddipboxName: data.ddipboxName.trim(),
        category: data.category.trim(),
        description: data.description?.trim() || undefined,
      };
    } else {
      const data = formData as UpdateDdipBoxRequest;
      const updateData: UpdateDdipBoxRequest = {};

      // 변경된 필드만 포함
      if (
        data.ddipboxName !== originalDdipbox?.ddipboxName &&
        data.ddipboxName
      ) {
        updateData.ddipboxName = data.ddipboxName.trim();
      }
      if (data.description !== originalDdipbox?.description) {
        updateData.description = data.description?.trim() || undefined;
      }
      if (data.category !== originalDdipbox?.category && data.category) {
        updateData.category = data.category.trim();
      }
      if (
        data.originalPrice !== originalDdipbox?.originalPrice &&
        data.originalPrice !== undefined
      ) {
        updateData.originalPrice = data.originalPrice;
      }
      if (
        data.salePrice !== originalDdipbox?.salePrice &&
        data.salePrice !== undefined
      ) {
        updateData.salePrice = data.salePrice;
      }
      if (
        data.dailyQuantity !== originalDdipbox?.dailyQuantity &&
        data.dailyQuantity !== undefined
      ) {
        updateData.dailyQuantity = data.dailyQuantity;
      }
      if (
        data.maxPerCustomer !== originalDdipbox?.maxPerCustomer &&
        data.maxPerCustomer !== undefined
      ) {
        updateData.maxPerCustomer = data.maxPerCustomer;
      }

      return updateData;
    }
  }, [formData, mode, originalDdipbox]);

  return {
    formData,
    handleInputChange,
    validateForm,
    hasChanges,
    getDiscountRate,
    resetForm,
    getSubmitData,
  };
};
