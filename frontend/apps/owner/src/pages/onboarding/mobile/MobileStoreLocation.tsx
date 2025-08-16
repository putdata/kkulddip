import { useNavigate } from 'react-router-dom';
import { ArrowRight, ArrowLeft, MapPin } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import LocationPicker from '@/components/onboarding/LocationPicker';

const MobileStoreLocation = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep } =
    useOnboardingStore();

  const handleLocationSelect = (locationData: {
    address: string;
    latitude: number;
    longitude: number;
  }) => {
    updateFormData({
      storeAddress: locationData.address,
      latitude: locationData.latitude,
      longitude: locationData.longitude,
    });
  };

  const handleNext = () => {
    setCurrentMobileStep('store-contact');
    navigate('/onboarding/store/contact');
  };

  const handleBack = () => {
    setCurrentMobileStep('store-basic');
    navigate('/onboarding/store/basic');
  };

  const isFormValid =
    formData.storeAddress.trim() &&
    formData.latitude !== 0 &&
    formData.longitude !== 0;

  return (
    <OnboardingGuard step="store">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            가게 위치 선택
          </h1>
          <p className="text-gray-600">
            지도에서 정확한 가게 위치를 선택해주세요
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <MapPin className="h-5 w-5 text-amber-600" />
              위치 정보
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-0">
            <LocationPicker
              initialAddress={formData.storeAddress}
              initialLatitude={formData.latitude || 37.5665}
              initialLongitude={formData.longitude || 126.978}
              onLocationSelect={handleLocationSelect}
            />

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
          <p className="text-xs text-gray-500">2/8 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileStoreLocation;
