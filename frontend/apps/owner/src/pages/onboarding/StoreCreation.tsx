import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import {
  Store,
  MapPin,
  Clock,
  Phone,
  FileText,
  Hash,
  Building,
  ArrowRight,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useCreateStore } from '@/queries/store';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import LocationPicker from '@/components/onboarding/LocationPicker';
import { ROUTE_PATH } from '@/router/route-path';
import type { CreateStoreRequest } from '@/types/store';

const StoreCreation = () => {
  const navigate = useNavigate();
  const createStoreMutation = useCreateStore();
  const { setStoreCreated } = useOnboardingStore();

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

  // 실제로 위치가 선택되었는지 확인하는 상태
  const [isLocationSelected, setIsLocationSelected] = useState(false);

  const handleInputChange = (
    field: keyof CreateStoreRequest,
    value: string | number,
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  // 위치 정보 업데이트 핸들러
  const handleLocationSelect = (locationData: {
    address: string;
    latitude: number;
    longitude: number;
  }) => {
    setFormData(prev => ({
      ...prev,
      storeAddress: locationData.address,
      latitude: locationData.latitude,
      longitude: locationData.longitude,
    }));
    // 실제 위치가 선택되었음을 표시
    setIsLocationSelected(true);
  };

  // HTML5 validation을 위한 추가 검증만 수행
  const validateBusinessLogic = (): boolean => {
    if (!isLocationSelected || !formData.latitude || !formData.longitude) {
      toast.error('지도에서 가게 위치를 선택해주세요.');
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

    // HTML5 validation이 통과된 후 추가 비즈니스 로직 검증
    if (!validateBusinessLogic()) {
      return;
    }

    createStoreMutation.mutate(formData, {
      onSuccess: () => {
        toast.success('가게가 성공적으로 등록되었습니다!');
        setStoreCreated(true);
        navigate(ROUTE_PATH.ONBOARDING.DDIPBOX);
      },
      onError: error => {
        console.error('Store creation failed:', error);
        toast.error('가게 등록 중 오류가 발생했습니다.');
      },
    });
  };

  // HTML5 validation이 기본 검증을 처리하므로 제거

  return (
    <OnboardingGuard step="store">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            가게 정보를 등록해주세요
          </h1>
          <p className="text-gray-600">
            꿀띱에서 사용할 가게의 기본 정보를 입력해주세요
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Store className="h-5 w-5 text-amber-600" />
              가게 등록
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-0">
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* 기본 정보 섹션 */}
              <div className="space-y-3">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                  <Building className="h-4 w-4" />
                  기본 정보
                </h3>

                <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
                  <div className="space-y-1">
                    <label
                      htmlFor="storeName"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Store className="h-4 w-4" />
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
                  </div>

                  <div className="space-y-1">
                    <label
                      htmlFor="businessNumber"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Hash className="h-4 w-4" />
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
                  </div>
                </div>

                <div className="space-y-1">
                  <label
                    htmlFor="storeAddress"
                    className="flex items-center gap-2 text-sm font-medium"
                  >
                    <MapPin className="h-4 w-4" />
                    주소 *
                  </label>
                  <Input
                    id="storeAddress"
                    type="text"
                    placeholder="지도에서 위치를 선택하면 자동으로 입력됩니다"
                    value={formData.storeAddress}
                    onChange={e =>
                      handleInputChange('storeAddress', e.target.value)
                    }
                    className="h-10"
                    maxLength={100}
                    required
                  />
                </div>

                <div className="space-y-1">
                  <label
                    htmlFor="description"
                    className="flex items-center gap-2 text-sm font-medium"
                  >
                    <FileText className="h-4 w-4" />
                    가게 설명
                  </label>
                  <Textarea
                    id="description"
                    placeholder="가게에 대한 간단한 설명을 입력하세요"
                    value={formData.description}
                    onChange={e =>
                      handleInputChange('description', e.target.value)
                    }
                    rows={2}
                    maxLength={500}
                  />
                </div>
              </div>

              {/* 연락처 정보 섹션 */}
              <div className="space-y-3">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                  <Phone className="h-4 w-4" />
                  연락처 정보
                </h3>

                <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
                  <div className="space-y-1">
                    <label
                      htmlFor="phone"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Phone className="h-4 w-4" />
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
                  </div>

                  <div className="space-y-1">
                    <label
                      htmlFor="operatingHours"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Clock className="h-4 w-4" />
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
                      maxLength={50}
                    />
                  </div>
                </div>
              </div>

              {/* 위치 정보 섹션 */}
              <div className="space-y-3">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                  <MapPin className="h-4 w-4" />
                  위치 정보 *
                </h3>

                <div className="space-y-4">
                  <p className="text-sm text-gray-600">
                    지도에서 가게 위치를 클릭하여 선택해주세요. 주소가 자동으로
                    입력됩니다.
                  </p>

                  <div className="rounded-lg border">
                    <LocationPicker
                      onLocationSelect={handleLocationSelect}
                      initialLatitude={formData.latitude || 37.5563}
                      initialLongitude={formData.longitude || 126.922}
                    />
                  </div>

                  {isLocationSelected &&
                    formData.latitude &&
                    formData.longitude && (
                      <div className="grid grid-cols-2 gap-4 rounded-lg bg-green-50 p-3">
                        <div>
                          <span className="text-xs text-green-700">위도</span>
                          <p className="text-sm font-medium text-green-900">
                            {formData.latitude.toFixed(6)}
                          </p>
                        </div>
                        <div>
                          <span className="text-xs text-green-700">경도</span>
                          <p className="text-sm font-medium text-green-900">
                            {formData.longitude.toFixed(6)}
                          </p>
                        </div>
                      </div>
                    )}
                </div>
              </div>

              {/* Submit Button */}
              <div className="pt-4">
                <Button
                  type="submit"
                  disabled={createStoreMutation.isPending}
                  className="h-12 w-full bg-gradient-to-r from-amber-500 to-orange-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl disabled:opacity-50"
                >
                  {createStoreMutation.isPending ? (
                    '등록 중...'
                  ) : (
                    <>
                      다음 단계
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </>
                  )}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      </div>
    </OnboardingGuard>
  );
};

export default StoreCreation;
