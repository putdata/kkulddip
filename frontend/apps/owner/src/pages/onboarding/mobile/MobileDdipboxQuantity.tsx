import { useNavigate } from 'react-router-dom';
import {
  ArrowRight,
  ArrowLeft,
  Package2,
  Clock,
  CheckCircle2,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import { useCreateDdipbox } from '@/queries/ddipbox';
import { toast } from 'sonner';

const MobileDdipboxQuantity = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep, setDdipboxCreated } =
    useOnboardingStore();
  const createDdipboxMutation = useCreateDdipbox();

  const handleInputChange = (field: string, value: string) => {
    // 숫자만 입력 가능하도록 처리
    const numericValue = value.replace(/[^0-9]/g, '');
    updateFormData({ [field]: parseInt(numericValue) || 0 });
  };

  const handleNext = async () => {
    // 띱박스 생성 임시 처리 (실제로는 가게 생성 후에 진행)
    toast.success('띱박스가 성공적으로 등록되었습니다!');
    setDdipboxCreated(true);
    setCurrentMobileStep('notification');
    navigate('/onboarding/notification');
  };

  const handleBack = () => {
    setCurrentMobileStep('ddipbox-pricing');
    navigate('/onboarding/ddipbox/pricing');
  };

  const handleSkip = () => {
    setCurrentMobileStep('notification');
    navigate('/onboarding/notification');
  };

  const isFormValid = formData.quantity > 0;

  return (
    <OnboardingGuard step="ddipbox">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            띱박스 수량 설정
          </h1>
          <p className="text-gray-600">
            하루에 판매할 띱박스 수량을 설정해주세요
          </p>
          <p className="mt-1 text-sm text-gray-500">
            이 단계는 건너뛸 수 있습니다
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Package2 className="h-5 w-5 text-amber-600" />
              수량 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 일일 판매 수량 */}
            <div className="space-y-2">
              <label
                htmlFor="quantity"
                className="flex items-center gap-2 text-sm font-medium"
              >
                <Package2 className="h-4 w-4" />
                일일 판매 수량 *
              </label>
              <div className="relative">
                <Input
                  id="quantity"
                  type="text"
                  placeholder="50"
                  value={formData.quantity || ''}
                  onChange={e => handleInputChange('quantity', e.target.value)}
                  className="h-12 pr-8 text-base"
                  maxLength={5}
                  required
                />
                <span className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-500">
                  개
                </span>
              </div>
              <p className="text-xs text-gray-500">
                하루에 준비할 수 있는 띱박스 개수를 입력해주세요
              </p>
            </div>

            {/* 판매 시간 */}
            <div className="space-y-2">
              <label
                htmlFor="saleTime"
                className="flex items-center gap-2 text-sm font-medium"
              >
                <Clock className="h-4 w-4" />
                판매 시간
              </label>
              <Input
                id="saleTime"
                type="text"
                placeholder="11:00-15:00"
                value={formData.saleTime}
                onChange={e => updateFormData({ saleTime: e.target.value })}
                className="h-12 text-base"
                maxLength={50}
              />
              <p className="text-xs text-gray-500">
                띱박스를 판매할 시간대를 입력해주세요 (예: 11:00-15:00)
              </p>
            </div>

            {/* 수량 설정 안내 */}
            {formData.quantity > 0 && (
              <div className="space-y-2 rounded-lg bg-green-50 p-3">
                <h4 className="text-sm font-medium text-green-900">
                  수량 설정 완료
                </h4>
                <div className="space-y-1 text-sm">
                  <div className="flex justify-between">
                    <span className="text-green-700">일일 판매량:</span>
                    <span className="font-medium text-green-900">
                      {formData.quantity}개
                    </span>
                  </div>
                  {formData.saleTime && (
                    <div className="flex justify-between">
                      <span className="text-green-700">판매 시간:</span>
                      <span className="font-medium text-green-900">
                        {formData.saleTime}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            )}

            {/* 완료 안내 */}
            <div className="rounded-lg bg-blue-50 p-3">
              <div className="flex items-center gap-2 text-sm text-blue-700">
                <CheckCircle2 className="h-4 w-4" />
                <span className="font-medium">띱박스 등록 준비 완료!</span>
              </div>
              <p className="mt-1 text-xs text-blue-600">
                다음 단계로 진행하면 띱박스가 등록됩니다
              </p>
            </div>

            {/* Navigation Buttons */}
            <div className="space-y-3 pt-4">
              <Button
                onClick={handleNext}
                disabled={!isFormValid || createDdipboxMutation.isPending}
                className="h-12 w-full bg-gradient-to-r from-amber-500 to-orange-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl disabled:opacity-50"
              >
                {createDdipboxMutation.isPending ? (
                  '띱박스 등록 중...'
                ) : (
                  <>
                    띱박스 등록하고 다음
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </>
                )}
              </Button>

              <div className="flex gap-3">
                <Button
                  variant="outline"
                  onClick={handleBack}
                  disabled={createDdipboxMutation.isPending}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  <ArrowLeft className="mr-2 h-4 w-4" />
                  이전
                </Button>
                <Button
                  variant="secondary"
                  onClick={handleSkip}
                  disabled={createDdipboxMutation.isPending}
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
          <p className="text-xs text-gray-500">7/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileDdipboxQuantity;
