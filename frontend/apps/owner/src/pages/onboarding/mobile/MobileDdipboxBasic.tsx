import { useNavigate } from 'react-router-dom';
import { ArrowRight, ArrowLeft, Package, FileText } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';

const MobileDdipboxBasic = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep } =
    useOnboardingStore();

  const handleInputChange = (field: string, value: string) => {
    updateFormData({ [field]: value });
  };

  const handleNext = () => {
    setCurrentMobileStep('ddipbox-pricing');
    navigate('/onboarding/ddipbox/pricing');
  };

  const handleBack = () => {
    setCurrentMobileStep('store-description');
    navigate('/onboarding/store/description');
  };

  const handleSkip = () => {
    setCurrentMobileStep('notification');
    navigate('/onboarding/notification');
  };

  const isFormValid = formData.ddipboxName.trim();

  return (
    <OnboardingGuard step="ddipbox">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            띱박스 기본 정보
          </h1>
          <p className="text-gray-600">
            고객이 주문할 수 있는 띱박스의 기본 정보를 입력해주세요
          </p>
          <p className="mt-1 text-sm text-gray-500">
            이 단계는 건너뛸 수 있습니다
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Package className="h-5 w-5 text-amber-600" />
              띱박스 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 띱박스명 */}
            <div className="space-y-2">
              <label
                htmlFor="ddipboxName"
                className="flex items-center gap-2 text-sm font-medium"
              >
                <Package className="h-4 w-4" />
                띱박스명 *
              </label>
              <Input
                id="ddipboxName"
                type="text"
                placeholder="예: 홍대김밥 스페셜 박스"
                value={formData.ddipboxName}
                onChange={e => handleInputChange('ddipboxName', e.target.value)}
                className="h-12 text-base"
                maxLength={100}
                required
              />
            </div>

            {/* 띱박스 설명 */}
            <div className="space-y-2">
              <label
                htmlFor="ddipboxDescription"
                className="flex items-center gap-2 text-sm font-medium"
              >
                <FileText className="h-4 w-4" />
                띱박스 설명
              </label>
              <Textarea
                id="ddipboxDescription"
                placeholder="띱박스에 포함된 메뉴나 특징을 설명해주세요"
                value={formData.ddipboxDescription}
                onChange={e =>
                  handleInputChange('ddipboxDescription', e.target.value)
                }
                rows={3}
                maxLength={200}
                className="resize-none text-base"
              />
              <div className="text-right text-xs text-gray-500">
                {formData.ddipboxDescription?.length || 0}/200
              </div>
              <p className="text-xs text-gray-500">
                예: 김밥 3종류와 음료가 포함된 세트 상품
              </p>
            </div>

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
          <p className="text-xs text-gray-500">5/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileDdipboxBasic;
