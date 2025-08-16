import { useNavigate } from 'react-router-dom';
import { ArrowRight, ArrowLeft, Phone, Clock } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';

const MobileStoreContact = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep } =
    useOnboardingStore();

  const handleInputChange = (field: string, value: string) => {
    updateFormData({ [field]: value });
  };

  const handleNext = () => {
    setCurrentMobileStep('store-description');
    navigate('/onboarding/store/description');
  };

  const handleBack = () => {
    setCurrentMobileStep('store-location');
    navigate('/onboarding/store/location');
  };

  const isFormValid = formData.phone.trim();

  return (
    <OnboardingGuard step="store">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">연락처 정보</h1>
          <p className="text-gray-600">
            고객이 연락할 수 있는 정보를 입력해주세요
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Phone className="h-5 w-5 text-amber-600" />
              연락처 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 전화번호 */}
            <div className="space-y-2">
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
                className="h-12 text-base"
                maxLength={15}
                required
              />
            </div>

            {/* 운영시간 */}
            <div className="space-y-2">
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
                className="h-12 text-base"
                maxLength={50}
              />
              <p className="text-xs text-gray-500">
                예: 08:00-22:00, 월-금 09:00-18:00
              </p>
            </div>

            {/* Navigation Buttons */}
            <div className="flex gap-3 pt-6">
              <Button
                variant="outline"
                onClick={handleBack}
                className="h-12 flex-1 text-base font-medium"
              >
                <ArrowLeft className="mr-2 h-4 w-4" />
                이전
              </Button>
              <Button
                onClick={handleNext}
                disabled={!isFormValid}
                className="h-12 flex-1 bg-gradient-to-r from-amber-500 to-orange-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl disabled:opacity-50"
              >
                다음
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Progress Info */}
        <div className="text-center">
          <p className="text-xs text-gray-500">3/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileStoreContact;
