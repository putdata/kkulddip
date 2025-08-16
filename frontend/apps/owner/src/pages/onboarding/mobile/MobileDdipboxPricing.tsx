import { useNavigate } from 'react-router-dom';
import { ArrowRight, ArrowLeft, DollarSign, Percent } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';

const MobileDdipboxPricing = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep } =
    useOnboardingStore();

  const handleInputChange = (field: string, value: string) => {
    // 숫자만 입력 가능하도록 처리
    const numericValue = value.replace(/[^0-9]/g, '');
    updateFormData({ [field]: parseInt(numericValue) || 0 });
  };

  const handleNext = () => {
    setCurrentMobileStep('ddipbox-quantity');
    navigate('/onboarding/ddipbox/quantity');
  };

  const handleBack = () => {
    setCurrentMobileStep('ddipbox-basic');
    navigate('/onboarding/ddipbox/basic');
  };

  const handleSkip = () => {
    setCurrentMobileStep('notification');
    navigate('/onboarding/notification');
  };

  const isFormValid = formData.originalPrice > 0 && formData.discountRate >= 0;

  // 할인된 가격 계산
  const discountedPrice =
    formData.originalPrice * (1 - formData.discountRate / 100);
  const discountAmount = formData.originalPrice - discountedPrice;

  return (
    <OnboardingGuard step="ddipbox">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            띱박스 가격 설정
          </h1>
          <p className="text-gray-600">띱박스의 정가와 할인율을 설정해주세요</p>
          <p className="mt-1 text-sm text-gray-500">
            이 단계는 건너뛸 수 있습니다
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <DollarSign className="h-5 w-5 text-amber-600" />
              가격 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 정가 */}
            <div className="space-y-2">
              <label
                htmlFor="originalPrice"
                className="flex items-center gap-2 text-sm font-medium"
              >
                <DollarSign className="h-4 w-4" />
                정가 *
              </label>
              <div className="relative">
                <Input
                  id="originalPrice"
                  type="text"
                  placeholder="10000"
                  value={formData.originalPrice || ''}
                  onChange={e =>
                    handleInputChange('originalPrice', e.target.value)
                  }
                  className="h-12 pr-8 text-base"
                  maxLength={10}
                  required
                />
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-500">
                  원
                </span>
              </div>
              <p className="text-xs text-gray-500">
                띱박스의 원래 가격을 입력해주세요
              </p>
            </div>

            {/* 할인율 */}
            <div className="space-y-2">
              <label
                htmlFor="discountRate"
                className="flex items-center gap-2 text-sm font-medium"
              >
                <Percent className="h-4 w-4" />
                할인율
              </label>
              <div className="relative">
                <Input
                  id="discountRate"
                  type="text"
                  placeholder="20"
                  value={formData.discountRate || ''}
                  onChange={e =>
                    handleInputChange('discountRate', e.target.value)
                  }
                  className="h-12 pr-8 text-base"
                  maxLength={2}
                  max={99}
                />
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-500">
                  %
                </span>
              </div>
              <p className="text-xs text-gray-500">
                0~99% 사이의 할인율을 입력해주세요 (0은 할인 없음)
              </p>
            </div>

            {/* 가격 계산 결과 */}
            {formData.originalPrice > 0 && (
              <div className="space-y-2 rounded-lg bg-blue-50 p-3">
                <h4 className="text-sm font-medium text-blue-900">
                  가격 미리보기
                </h4>
                <div className="space-y-1 text-sm">
                  <div className="flex justify-between">
                    <span className="text-blue-700">정가:</span>
                    <span className="font-medium text-blue-900">
                      {formData.originalPrice.toLocaleString()}원
                    </span>
                  </div>
                  {formData.discountRate > 0 && (
                    <>
                      <div className="flex justify-between">
                        <span className="text-blue-700">할인율:</span>
                        <span className="text-red-600">
                          -{formData.discountRate}%
                        </span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-blue-700">할인 금액:</span>
                        <span className="text-red-600">
                          -{Math.round(discountAmount).toLocaleString()}원
                        </span>
                      </div>
                      <hr className="border-blue-200" />
                      <div className="flex justify-between">
                        <span className="font-medium text-blue-900">
                          최종 가격:
                        </span>
                        <span className="font-bold text-blue-900">
                          {Math.round(discountedPrice).toLocaleString()}원
                        </span>
                      </div>
                    </>
                  )}
                </div>
              </div>
            )}

            {/* Navigation Buttons */}
            <div className="space-y-3 pt-4">
              <Button
                onClick={handleNext}
                disabled={!isFormValid}
                className="h-12 w-full bg-gradient-to-r from-amber-500 to-orange-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl disabled:opacity-50"
              >
                다음
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>

              <div className="flex gap-3">
                <Button
                  variant="outline"
                  onClick={handleBack}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  <ArrowLeft className="mr-2 h-4 w-4" />
                  이전
                </Button>
                <Button
                  variant="secondary"
                  onClick={handleSkip}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  나중에 설정하기
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Progress Info */}
        <div className="text-center">
          <p className="text-xs text-gray-500">6/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileDdipboxPricing;
