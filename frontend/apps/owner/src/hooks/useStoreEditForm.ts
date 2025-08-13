import { useState, useEffect } from 'react';
import { useUpdateStore } from '@/queries/store';
import type { UpdateStoreRequest, Store } from '@/types/store';

interface UseStoreEditFormOptions {
  store: Store | null;
  isOpen: boolean;
  onSuccess?: () => void;
}

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

  const handleSubmit = () => {
    if (!store) {
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
