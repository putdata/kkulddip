import { useState, useEffect } from 'react';
import { toast } from 'sonner';
import { useUpdateStore } from '@/queries/store';
import type { UpdateStoreRequest, Store } from '@/types/store';

interface UseStoreEditFormOptions {
  store: Store | null;
  isOpen: boolean;
  onSuccess?: () => void;
}

/**
 * 매장 정보 수정 폼 관리 Hook
 *
 * @description
 * 매장 정보 수정 다이얼로그의 폼 데이터와 제출 로직을 관리합니다.
 */
export const useStoreEditForm = ({
  store,
  isOpen,
  onSuccess,
}: UseStoreEditFormOptions) => {
  const updateStoreMutation = useUpdateStore();
  const [formData, setFormData] = useState<UpdateStoreRequest>({});

  // 다이얼로그가 열릴 때 store 데이터로 폼 초기화
  useEffect(() => {
    if (store && isOpen) {
      setFormData({
        storeName: store.storeName,
        storeAddress: store.storeAddress,
        description: store.description,
        operatingHours: store.operatingHours,
        phone: store.phone,
        latitude: store.latitude,
        longitude: store.longitude,
      });
    }
  }, [store, isOpen]);

  // 폼 필드 업데이트 헬퍼 함수들
  const updateField =
    (field: keyof UpdateStoreRequest) => (value: string | number) => {
      setFormData(prev => ({
        ...prev,
        [field]: value,
      }));
    };

  const validateForm = (): boolean => {
    // 위도 검증
    if (formData.latitude !== undefined) {
      if (formData.latitude < -90 || formData.latitude > 90) {
        toast.error('위도는 -90도에서 90도 사이의 값이어야 합니다.');
        return false;
      }
    }

    // 경도 검증
    if (formData.longitude !== undefined) {
      if (formData.longitude < -180 || formData.longitude > 180) {
        toast.error('경도는 -180도에서 180도 사이의 값이어야 합니다.');
        return false;
      }
    }

    return true;
  };

  const handleSubmit = () => {
    if (!store) {
      toast.error('매장 정보를 불러올 수 없습니다.');
      return;
    }

    if (!validateForm()) {
      return;
    }

    updateStoreMutation.mutate(
      {
        storeId: store.storeId,
        data: formData,
      },
      {
        onSuccess: () => {
          setFormData({});
          onSuccess?.();
        },
      },
    );
  };

  const resetForm = () => {
    setFormData({});
  };

  return {
    formData,
    updateField,
    handleSubmit,
    resetForm,
    isSubmitting: updateStoreMutation.isPending,
  };
};
