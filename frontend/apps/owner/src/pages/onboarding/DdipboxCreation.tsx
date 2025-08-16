import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import {
  Package,
  Tag,
  DollarSign,
  Hash,
  ArrowRight,
  Clock,
  Percent,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useCreateDdipbox } from '@/queries/ddipbox';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import { ROUTE_PATH } from '@/router/route-path';
import type { CreateDdipBoxRequest } from '@/types/ddipbox';

const DdipboxCreation = () => {
  const navigate = useNavigate();
  const createDdipboxMutation = useCreateDdipbox();
  const { setDdipboxCreated } = useOnboardingStore();

  const [formData, setFormData] = useState<CreateDdipBoxRequest>({
    ddipboxName: '',
    description: '',
    category: '',
    originalPrice: 0,
    salePrice: 0,
    dailyQuantity: 1,
    maxPerCustomer: 1,
  });

  const handleInputChange = (
    field: keyof CreateDdipBoxRequest,
    value: string | number,
  ) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
  };

  const getDiscountRate = (): number => {
    if (formData.originalPrice <= 0 || formData.salePrice <= 0) {
      return 0;
    }
    return Math.round(
      ((formData.originalPrice - formData.salePrice) / formData.originalPrice) *
        100,
    );
  };

  const validateForm = (): boolean => {
    if (!formData.ddipboxName.trim()) {
      toast.error('띱박스명을 입력해주세요.');
      return false;
    }
    if (!formData.category.trim()) {
      toast.error('카테고리를 입력해주세요.');
      return false;
    }
    if (formData.originalPrice <= 0) {
      toast.error('원가를 올바르게 입력해주세요.');
      return false;
    }
    if (formData.salePrice <= 0) {
      toast.error('판매가를 올바르게 입력해주세요.');
      return false;
    }
    if (formData.salePrice >= formData.originalPrice) {
      toast.error('판매가는 원가보다 낮아야 합니다.');
      return false;
    }
    if (formData.dailyQuantity <= 0) {
      toast.error('일일 수량을 올바르게 입력해주세요.');
      return false;
    }
    if (formData.maxPerCustomer <= 0) {
      toast.error('1인당 최대 구매 수량을 올바르게 입력해주세요.');
      return false;
    }
    if (formData.maxPerCustomer > formData.dailyQuantity) {
      toast.error('1인당 최대 구매 수량은 일일 수량보다 작거나 같아야 합니다.');
      return false;
    }
    return true;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setDdipboxCreated(true);
    navigate(ROUTE_PATH.ONBOARDING.NOTIFICATION);
  };

  const handleSkip = () => {
    navigate(ROUTE_PATH.ONBOARDING.NOTIFICATION);
  };

  const isFormValid =
    formData.ddipboxName.trim() &&
    formData.category.trim() &&
    formData.originalPrice > 0 &&
    formData.salePrice > 0 &&
    formData.salePrice < formData.originalPrice &&
    formData.dailyQuantity > 0 &&
    formData.maxPerCustomer > 0 &&
    formData.maxPerCustomer <= formData.dailyQuantity;

  return (
    <OnboardingGuard step="ddipbox">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            첫 번째 띱박스를 만들어보세요
          </h1>
          <p className="text-gray-600">
            남는 재료로 특별한 할인 박스를 만들어 새로운 수익을 창출하세요
          </p>
          <p className="mt-1 text-sm text-gray-500">
            나중에 언제든 추가할 수 있습니다
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Package className="h-5 w-5 text-orange-600" />
              띱박스 생성
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-0">
            <form onSubmit={handleSubmit} className="space-y-4">
              {/* 기본 정보 */}
              <div className="space-y-3">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                  <Package className="h-4 w-4" />
                  기본 정보
                </h3>

                <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
                  <div className="space-y-1">
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
                      placeholder="예: 오늘의 특가 띱박스"
                      value={formData.ddipboxName}
                      onChange={e =>
                        handleInputChange('ddipboxName', e.target.value)
                      }
                      className="h-10"
                      maxLength={100}
                      required
                    />
                  </div>

                  <div className="space-y-1">
                    <label
                      htmlFor="category"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Tag className="h-4 w-4" />
                      카테고리 *
                    </label>
                    <Input
                      id="category"
                      type="text"
                      placeholder="예: 한식, 분식, 디저트"
                      value={formData.category}
                      onChange={e =>
                        handleInputChange('category', e.target.value)
                      }
                      className="h-10"
                      maxLength={50}
                      required
                    />
                  </div>
                </div>

                <div className="space-y-1">
                  <label htmlFor="description" className="text-sm font-medium">
                    상품 설명
                  </label>
                  <Textarea
                    id="description"
                    placeholder="띱박스에 포함된 메뉴와 특징을 설명해주세요"
                    value={formData.description}
                    onChange={e =>
                      handleInputChange('description', e.target.value)
                    }
                    rows={2}
                    maxLength={300}
                  />
                </div>
              </div>

              {/* 가격 정보 */}
              <div className="space-y-3">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                  <DollarSign className="h-4 w-4" />
                  가격 정보
                </h3>

                <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
                  <div className="space-y-1">
                    <label
                      htmlFor="originalPrice"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <DollarSign className="h-4 w-4" />
                      원가 *
                    </label>
                    <Input
                      id="originalPrice"
                      type="number"
                      min="1"
                      placeholder="15000"
                      value={formData.originalPrice || ''}
                      onChange={e =>
                        handleInputChange(
                          'originalPrice',
                          parseInt(e.target.value) || 0,
                        )
                      }
                      className="h-10"
                      required
                    />
                  </div>

                  <div className="space-y-1">
                    <label
                      htmlFor="salePrice"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Tag className="h-4 w-4" />
                      판매가 *
                    </label>
                    <Input
                      id="salePrice"
                      type="number"
                      min="1"
                      placeholder="9900"
                      value={formData.salePrice || ''}
                      onChange={e =>
                        handleInputChange(
                          'salePrice',
                          parseInt(e.target.value) || 0,
                        )
                      }
                      className="h-10"
                      required
                    />
                  </div>

                  <div className="flex items-end">
                    <div className="flex h-10 w-full items-center justify-center rounded-lg border bg-gray-50">
                      <div className="flex items-center gap-2 text-sm font-medium">
                        <Percent className="h-4 w-4 text-green-600" />
                        <span className="text-green-600">
                          {getDiscountRate()}% 할인
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              {/* 수량 정보 */}
              <div className="space-y-3">
                <h3 className="flex items-center gap-2 text-base font-semibold text-gray-900">
                  <Hash className="h-4 w-4" />
                  수량 정보
                </h3>

                <div className="grid grid-cols-1 gap-3 md:grid-cols-2">
                  <div className="space-y-1">
                    <label
                      htmlFor="dailyQuantity"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Hash className="h-4 w-4" />
                      일일 판매 수량 *
                    </label>
                    <Input
                      id="dailyQuantity"
                      type="number"
                      min="1"
                      placeholder="10"
                      value={formData.dailyQuantity || ''}
                      onChange={e =>
                        handleInputChange(
                          'dailyQuantity',
                          parseInt(e.target.value) || 0,
                        )
                      }
                      className="h-10"
                      required
                    />
                  </div>

                  <div className="space-y-1">
                    <label
                      htmlFor="maxPerCustomer"
                      className="flex items-center gap-2 text-sm font-medium"
                    >
                      <Hash className="h-4 w-4" />
                      1인당 최대 구매 수량 *
                    </label>
                    <Input
                      id="maxPerCustomer"
                      type="number"
                      min="1"
                      placeholder="2"
                      value={formData.maxPerCustomer || ''}
                      onChange={e =>
                        handleInputChange(
                          'maxPerCustomer',
                          parseInt(e.target.value) || 0,
                        )
                      }
                      className="h-10"
                      required
                    />
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex flex-col gap-3 pt-4 sm:flex-row">
                <Button
                  type="button"
                  variant="secondary"
                  onClick={handleSkip}
                  className="h-12 flex-1 text-base font-medium"
                  disabled={createDdipboxMutation.isPending}
                >
                  <Clock className="mr-2 h-4 w-4" />
                  나중에 설정하기
                </Button>
                <Button
                  type="submit"
                  disabled={!isFormValid || createDdipboxMutation.isPending}
                  className="h-12 flex-1 bg-gradient-to-r from-orange-500 to-red-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-orange-600 hover:to-red-600 hover:shadow-xl disabled:opacity-50"
                >
                  {createDdipboxMutation.isPending ? (
                    '생성 중...'
                  ) : (
                    <>
                      띱박스 생성
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

export default DdipboxCreation;
