import { useNavigate } from 'react-router-dom';
import { ArrowRight, ArrowLeft, FileText, CheckCircle2 } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import { useCreateStore } from '@/queries/store';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import { toast } from 'sonner';
import type { CreateStoreRequest } from '@/types/store';

const MobileStoreDescription = () => {
  const navigate = useNavigate();
  const {
    formData,
    updateFormData,
    setCurrentMobileStep,
    setStoreCreated,
    getStoreFormData,
  } = useOnboardingStore();
  const createStoreMutation = useCreateStore();

  const handleInputChange = (value: string) => {
    updateFormData({ description: value });
  };

  const handleNext = async () => {
    // 스토어 생성 API 호출
    const storeData = getStoreFormData();

    // 필수 필드 검증
    if (!storeData.storeName || !storeData.businessNumber) {
      toast.error('필수 정보가 누락되었습니다.');
      return;
    }

    createStoreMutation.mutate(storeData as CreateStoreRequest, {
      onSuccess: () => {
        toast.success('가게가 성공적으로 등록되었습니다!');
        setStoreCreated(true);
        setCurrentMobileStep('ddipbox-basic');
        navigate('/onboarding/ddipbox/basic');
      },
      onError: error => {
        console.error('Store creation failed:', error);
        toast.error('가게 등록 중 오류가 발생했습니다.');
      },
    });
  };

  const handleBack = () => {
    setCurrentMobileStep('store-contact');
    navigate('/onboarding/store/contact');
  };

  const handleSkip = () => {
    handleNext(); // 설명 없이도 가게 생성 진행
  };

  return (
    <OnboardingGuard step="store">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">가게 소개</h1>
          <p className="text-gray-600">
            고객에게 보여질 가게 설명을 작성해주세요
          </p>
          <p className="mt-1 text-sm text-gray-500">
            이 단계는 건너뛸 수 있습니다
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <FileText className="h-5 w-5 text-amber-600" />
              가게 설명
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 가게 설명 */}
            <div className="space-y-2">
              <label htmlFor="description" className="text-sm font-medium">
                가게 소개 (선택사항)
              </label>
              <Textarea
                id="description"
                placeholder="우리 가게만의 특별한 점이나 대표 메뉴를 소개해주세요"
                value={formData.description}
                onChange={e => handleInputChange(e.target.value)}
                rows={4}
                maxLength={300}
                className="resize-none text-base"
              />
              <div className="text-right text-xs text-gray-500">
                {formData.description?.length || 0}/300
              </div>
            </div>

            {/* 완료 안내 */}
            <div className="rounded-lg bg-green-50 p-3">
              <div className="flex items-center gap-2 text-sm text-green-700">
                <CheckCircle2 className="h-4 w-4" />
                <span className="font-medium">가게 등록 준비 완료!</span>
              </div>
              <p className="mt-1 text-xs text-green-600">
                다음 단계로 진행하면 가게가 등록됩니다
              </p>
            </div>

            {/* Navigation Buttons */}
            <div className="space-y-3 pt-4">
              <Button
                onClick={handleNext}
                disabled={createStoreMutation.isPending}
                className="h-12 w-full bg-gradient-to-r from-amber-500 to-orange-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-amber-600 hover:to-orange-600 hover:shadow-xl disabled:opacity-50"
              >
                {createStoreMutation.isPending ? (
                  '가게 등록 중...'
                ) : (
                  <>
                    가게 등록하고 다음
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </>
                )}
              </Button>

              <div className="flex gap-3">
                <Button
                  variant="outline"
                  onClick={handleBack}
                  disabled={createStoreMutation.isPending}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  <ArrowLeft className="mr-2 h-4 w-4" />
                  이전
                </Button>
                <Button
                  variant="secondary"
                  onClick={handleSkip}
                  disabled={createStoreMutation.isPending}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  설명 없이 등록
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Progress Info */}
        <div className="text-center">
          <p className="text-xs text-gray-500">4/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileStoreDescription;
