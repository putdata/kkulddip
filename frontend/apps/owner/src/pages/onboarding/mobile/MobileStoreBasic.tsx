import { useNavigate } from 'react-router-dom';
import { ArrowRight, Store, Hash } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
// import { ROUTE_PATH } from '@/router/route-path';

const MobileStoreBasic = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep } =
    useOnboardingStore();

  const handleInputChange = (field: string, value: string) => {
    updateFormData({ [field]: value });
  };

  const handleNext = () => {
    setCurrentMobileStep('store-location');
    navigate('/onboarding/store/location');
  };

  const isFormValid =
    formData.storeName.trim() && formData.businessNumber.trim();

  return (
    <OnboardingGuard step="store">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            가게 기본 정보
          </h1>
          <p className="text-gray-600">
            가게명과 사업자등록번호를 입력해주세요
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Store className="h-5 w-5 text-amber-600" />
              기본 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 가게명 */}
            <div className="space-y-2">
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
                onChange={e => handleInputChange('storeName', e.target.value)}
                className="h-12 text-base"
                maxLength={255}
                required
              />
            </div>

            {/* 사업자등록번호 */}
            <div className="space-y-2">
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
                className="h-12 text-base"
                maxLength={100}
                required
              />
            </div>

            {/* Next Button */}
            <div className="pt-6">
              <Button
                onClick={handleNext}
                disabled={!isFormValid}
                className="h-12 w-full bg-gradient-to-r from-amber-500 to-orange-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl disabled:opacity-50"
              >
                다음
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Progress Info */}
        <div className="text-center">
          <p className="text-xs text-gray-500">1/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileStoreBasic;
