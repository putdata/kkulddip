import { useState } from 'react';
import { toast } from 'sonner';
import {
  Plus,
  Store,
  MapPin,
  Clock,
  Phone,
  FileText,
  Hash,
  Building,
} from 'lucide-react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { useCreateStore } from '@/queries/store';
import type { CreateStoreRequest } from '@/types/store';

const AddStoreDialog = () => {
  const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState<CreateStoreRequest>({
    storeName: '',
    storeAddress: '',
    description: '',
    operatingHours: '',
    phone: '',
    businessNumber: '',
    latitude: 0,
    longitude: 0,
  });

  const createStoreMutation = useCreateStore();

  const handleInputChange = (
    field: keyof CreateStoreRequest,
    value: string | number,
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const resetForm = () => {
    setFormData({
      storeName: '',
      storeAddress: '',
      description: '',
      operatingHours: '',
      phone: '',
      businessNumber: '',
      latitude: 0,
      longitude: 0,
    });
  };

  const validateForm = (): boolean => {
    if (!formData.storeName.trim()) {
      toast.error('가게명을 입력해주세요.');
      return false;
    }
    if (!formData.storeAddress.trim()) {
      toast.error('주소를 입력해주세요.');
      return false;
    }
    if (!formData.phone.trim()) {
      toast.error('전화번호를 입력해주세요.');
      return false;
    }
    if (!formData.businessNumber.trim()) {
      toast.error('사업자등록번호를 입력해주세요.');
      return false;
    }
    if (formData.latitude === 0 || formData.longitude === 0) {
      toast.error('위치 정보(위도/경도)를 입력해주세요.');
      return false;
    }
    if (formData.latitude < -90 || formData.latitude > 90) {
      toast.error('위도는 -90도에서 90도 사이의 값이어야 합니다.');
      return false;
    }
    if (formData.longitude < -180 || formData.longitude > 180) {
      toast.error('경도는 -180도에서 180도 사이의 값이어야 합니다.');
      return false;
    }
    return true;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    createStoreMutation.mutate(formData, {
      onSuccess: () => {
        setOpen(false);
        resetForm();
      },
    });
  };

  const handleCancel = () => {
    setOpen(false);
    resetForm();
  };

  const isFormValid =
    formData.storeName.trim() &&
    formData.storeAddress.trim() &&
    formData.phone.trim() &&
    formData.businessNumber.trim() &&
    formData.latitude !== 0 &&
    formData.longitude !== 0;

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button
          variant="ghost"
          size="sm"
          className="h-9 w-full justify-start gap-2 text-sm font-normal"
        >
          <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-6 items-center justify-center rounded-md">
            <Plus className="size-3" />
          </div>
          새 가게 추가
        </Button>
      </DialogTrigger>

      <DialogContent className="max-h-[90vh] w-full max-w-[95vw] p-0 sm:max-w-[600px]">
        <div className="flex h-full max-h-[90vh] flex-col">
          {/* Header - 고정 */}
          <DialogHeader className="shrink-0 p-6 pb-4">
            <DialogTitle className="flex items-center gap-2 text-lg sm:text-xl">
              <Store className="size-5 sm:size-6" />새 가게 등록
            </DialogTitle>
            <DialogDescription className="text-sm sm:text-base">
              새로운 가게 정보를 입력해주세요. 필수 항목(*)을 모두 작성해야
              등록이 가능합니다.
            </DialogDescription>
          </DialogHeader>

          {/* Form - 스크롤 영역 */}
          <div className="flex-1 overflow-y-auto px-6">
            <form onSubmit={handleSubmit} className="space-y-6 pb-4">
              {/* 기본 정보 섹션 */}
              <div className="space-y-4">
                <h3 className="text-muted-foreground flex items-center gap-2 text-sm font-semibold">
                  <Building className="size-4" />
                  기본 정보
                </h3>

                <div className="space-y-2">
                  <label
                    htmlFor="storeName"
                    className="flex items-center gap-2 text-sm font-medium"
                  >
                    <Store className="size-4" />
                    가게명 *
                  </label>
                  <Input
                    id="storeName"
                    type="text"
                    placeholder="예: 홍대 김밥천국"
                    value={formData.storeName}
                    onChange={e =>
                      handleInputChange('storeName', e.target.value)
                    }
                    className="h-10"
                    maxLength={255}
                    required
                  />
                  <div className="text-muted-foreground text-right text-xs">
                    {formData.storeName.length}/255
                  </div>
                </div>

                <div className="space-y-2">
                  <label
                    htmlFor="storeAddress"
                    className="flex items-center gap-2 text-sm font-medium"
                  >
                    <MapPin className="size-4" />
                    주소 *
                  </label>
                  <Input
                    id="storeAddress"
                    type="text"
                    placeholder="예: 서울특별시 마포구 홍익로 15"
                    value={formData.storeAddress}
                    onChange={e =>
                      handleInputChange('storeAddress', e.target.value)
                    }
                    className="h-10"
                    maxLength={100}
                    required
                  />
                  <div className="text-muted-foreground text-right text-xs">
                    {formData.storeAddress.length}/100
                  </div>
                </div>

                <div className="space-y-2">
                  <label
                    htmlFor="description"
                    className="flex items-center gap-2 text-sm font-medium"
                  >
                    <FileText className="size-4" />
                    가게 설명
                  </label>
                  <Textarea
                    id="description"
                    placeholder="가게에 대한 상세한 설명을 입력하세요"
                    value={formData.description}
                    onChange={e =>
                      handleInputChange('description', e.target.value)
                    }
                    onKeyDown={e => {
                      if (e.key === 'Enter' && !e.shiftKey) {
                        e.stopPropagation();
                      }
                    }}
                    rows={4}
                    maxLength={1000}
                  />
                  <div className="text-muted-foreground text-right text-xs">
                    {formData.description?.length || 0}/1000
                  </div>
                </div>
              </div>

              {/* 연락처 정보 섹션 */}
              <div className="space-y-4">
                <h3 className="text-muted-foreground flex items-center gap-2 text-sm font-semibold">
                  <Phone className="size-4" />
                  연락처 정보
                </h3>

                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <label
                      htmlFor="phone"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Phone className="size-4" />
                      전화번호 *
                    </label>
                    <Input
                      id="phone"
                      type="tel"
                      placeholder="02-312-1234"
                      value={formData.phone}
                      onChange={e => handleInputChange('phone', e.target.value)}
                      className="h-10"
                      maxLength={15}
                      required
                    />
                    <div className="text-muted-foreground text-right text-xs">
                      {formData.phone?.length || 0}/15
                    </div>
                  </div>

                  <div className="space-y-2">
                    <label
                      htmlFor="operatingHours"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Clock className="size-4" />
                      운영시간
                    </label>
                    <Input
                      id="operatingHours"
                      type="text"
                      placeholder="08:00-22:00"
                      value={formData.operatingHours}
                      onChange={e =>
                        handleInputChange('operatingHours', e.target.value)
                      }
                      className="h-10"
                      maxLength={100}
                    />
                    <div className="text-muted-foreground text-right text-xs">
                      {formData.operatingHours?.length || 0}/100
                    </div>
                  </div>
                </div>
              </div>

              {/* 사업자 정보 섹션 */}
              <div className="space-y-4">
                <h3 className="text-muted-foreground flex items-center gap-2 text-sm font-semibold">
                  <Hash className="size-4" />
                  사업자 정보
                </h3>

                <div className="space-y-2">
                  <label
                    htmlFor="businessNumber"
                    className="flex items-center gap-2 text-sm font-medium"
                  >
                    <Hash className="size-4" />
                    사업자등록번호 *
                  </label>
                  <Input
                    id="businessNumber"
                    type="text"
                    placeholder="123-45-67890"
                    value={formData.businessNumber}
                    onChange={e =>
                      handleInputChange('businessNumber', e.target.value)
                    }
                    className="h-10"
                    maxLength={100}
                    required
                  />
                  <div className="text-muted-foreground text-right text-xs">
                    {formData.businessNumber?.length || 0}/100
                  </div>
                </div>
              </div>

              {/* 위치 정보 섹션 */}
              <div className="space-y-4">
                <h3 className="text-muted-foreground flex items-center gap-2 text-sm font-semibold">
                  <MapPin className="size-4" />
                  위치 정보 *
                </h3>

                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                  <div className="space-y-2">
                    <label htmlFor="latitude" className="text-sm font-medium">
                      위도 *
                    </label>
                    <Input
                      id="latitude"
                      type="number"
                      step="any"
                      min="-90"
                      max="90"
                      placeholder="37.5563"
                      value={formData.latitude || ''}
                      onChange={e =>
                        handleInputChange(
                          'latitude',
                          parseFloat(e.target.value) || 0,
                        )
                      }
                      className="h-10"
                      required
                    />
                  </div>

                  <div className="space-y-2">
                    <label htmlFor="longitude" className="text-sm font-medium">
                      경도 *
                    </label>
                    <Input
                      id="longitude"
                      type="number"
                      step="any"
                      min="-180"
                      max="180"
                      placeholder="126.922"
                      value={formData.longitude || ''}
                      onChange={e =>
                        handleInputChange(
                          'longitude',
                          parseFloat(e.target.value) || 0,
                        )
                      }
                      className="h-10"
                      required
                    />
                  </div>
                </div>
              </div>
            </form>
          </div>

          {/* Footer - 고정 */}
          <DialogFooter className="bg-background shrink-0 border-t p-6 pt-4">
            <div className="flex w-full flex-col gap-2 sm:w-auto sm:flex-row">
              <Button
                type="button"
                variant="outline"
                onClick={handleCancel}
                disabled={createStoreMutation.isPending}
                className="w-full sm:w-auto"
              >
                취소
              </Button>
              <Button
                type="submit"
                onClick={handleSubmit}
                disabled={!isFormValid || createStoreMutation.isPending}
                className="w-full sm:w-auto"
              >
                {createStoreMutation.isPending ? '등록 중...' : '가게 등록'}
              </Button>
            </div>
          </DialogFooter>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default AddStoreDialog;
