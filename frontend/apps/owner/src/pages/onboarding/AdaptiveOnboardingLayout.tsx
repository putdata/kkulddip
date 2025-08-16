import { useEffect, useMemo } from 'react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { Progress } from '@/components/ui/progress';
import { CheckCircle2, Store, Package, Bell } from 'lucide-react';
import { useOnboardingStore } from '@/stores/onboardingStore';
import { useIsMobile } from '@/hooks/use-mobile';
import { ROUTE_PATH } from '@/router/route-path';

const AdaptiveOnboardingLayout = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { completedSteps, currentStep, setCurrentStep, setCurrentMobileStep } =
    useOnboardingStore();
  const isMobile = useIsMobile();

  // Define desktop and mobile route mappings
  const desktopRoutes = useMemo(
    () => ({
      store: ROUTE_PATH.ONBOARDING.STORE,
      ddipbox: ROUTE_PATH.ONBOARDING.DDIPBOX,
      notification: ROUTE_PATH.ONBOARDING.NOTIFICATION,
    }),
    [],
  );

  const mobileRoutes = useMemo(
    () => ({
      'store-basic': ROUTE_PATH.ONBOARDING.MOBILE.STORE.BASIC,
      'store-location': ROUTE_PATH.ONBOARDING.MOBILE.STORE.LOCATION,
      'store-contact': ROUTE_PATH.ONBOARDING.MOBILE.STORE.CONTACT,
      'store-description': ROUTE_PATH.ONBOARDING.MOBILE.STORE.DESCRIPTION,
      'ddipbox-basic': ROUTE_PATH.ONBOARDING.MOBILE.DDIPBOX.BASIC,
      'ddipbox-pricing': ROUTE_PATH.ONBOARDING.MOBILE.DDIPBOX.PRICING,
      'ddipbox-quantity': ROUTE_PATH.ONBOARDING.MOBILE.DDIPBOX.QUANTITY,
      'notification': ROUTE_PATH.ONBOARDING.MOBILE.NOTIFICATION,
    }),
    [],
  );

  const steps = [
    {
      path: desktopRoutes.store,
      label: '가게 생성',
      icon: Store,
      required: true,
      stepKey: 'store' as const,
    },
    {
      path: desktopRoutes.ddipbox,
      label: '띱박스 생성',
      icon: Package,
      required: false,
      stepKey: 'ddipbox' as const,
    },
    {
      path: desktopRoutes.notification,
      label: '알림 설정',
      icon: Bell,
      required: false,
      stepKey: 'notification' as const,
    },
  ];

  // Handle responsive transitions
  useEffect(() => {
    const currentPath = location.pathname;

    if (isMobile) {
      // If on desktop route but mobile view, redirect to appropriate mobile step
      if (
        Object.values(desktopRoutes).includes(
          currentPath as (typeof desktopRoutes)[keyof typeof desktopRoutes],
        )
      ) {
        if (currentPath === desktopRoutes.store) {
          navigate(mobileRoutes['store-basic']);
          setCurrentMobileStep('store-basic');
        } else if (currentPath === desktopRoutes.ddipbox) {
          navigate(mobileRoutes['ddipbox-basic']);
          setCurrentMobileStep('ddipbox-basic');
        } else if (currentPath === desktopRoutes.notification) {
          navigate(mobileRoutes.notification);
          setCurrentMobileStep('notification');
        }
      }
    } else {
      // If on mobile route but desktop view, redirect to appropriate desktop step
      if (
        Object.values(mobileRoutes).includes(
          currentPath as (typeof mobileRoutes)[keyof typeof mobileRoutes],
        )
      ) {
        if (currentPath.includes('/store/')) {
          navigate(desktopRoutes.store);
          setCurrentStep('store');
        } else if (currentPath.includes('/ddipbox/')) {
          navigate(desktopRoutes.ddipbox);
          setCurrentStep('ddipbox');
        } else if (currentPath.includes('/notification')) {
          navigate(desktopRoutes.notification);
          setCurrentStep('notification');
        }
      }
    }
  }, [
    isMobile,
    location.pathname,
    navigate,
    setCurrentStep,
    setCurrentMobileStep,
    desktopRoutes,
    mobileRoutes,
  ]);

  const getCurrentStep = () => {
    const currentPath = location.pathname;
    if (isMobile) {
      // Mobile progress calculation
      const mobileStepOrder = [
        'store-basic',
        'store-location',
        'store-contact',
        'store-description',
        'ddipbox-basic',
        'ddipbox-pricing',
        'ddipbox-quantity',
        'notification',
      ];
      const currentMobileStepIndex = mobileStepOrder.findIndex(
        step => mobileRoutes[step as keyof typeof mobileRoutes] === currentPath,
      );
      return currentMobileStepIndex >= 0
        ? Math.floor(currentMobileStepIndex / 3)
        : 0;
    } else {
      // Desktop progress calculation
      return steps.findIndex(step => step.path === currentPath);
    }
  };

  const currentStepIndex = getCurrentStep();

  const getProgress = () => {
    if (isMobile) {
      // Mobile: 8 total steps - 시작점에서 점만 살짝 채움
      const currentIndex = Object.values(mobileRoutes).findIndex(
        route => route === location.pathname,
      );
      if (currentIndex === -1) {
        return 3; // 첫 번째 점만 아주 살짝 채우기
      }
      return 2 + currentIndex * 12.25; // 2% + 각 단계마다 12.25%씩 증가 (최대 97.75%)
    } else {
      // Desktop: 3 total steps - 시작점에서 점만 살짝 채움
      if (currentStepIndex === -1) {
        return 3; // 첫 번째 점만 아주 살짝 채우기
      }
      return 2 + currentStepIndex * 49; // 2% + 각 단계마다 49%씩 증가 (최대 100%)
    }
  };

  const progress = getProgress();

  const getMobileStepProgress = () => {
    const mobileStepOrder = [
      'store-basic',
      'store-location',
      'store-contact',
      'store-description',
      'ddipbox-basic',
      'ddipbox-pricing',
      'ddipbox-quantity',
      'notification',
    ];
    const currentIndex = mobileStepOrder.findIndex(
      step =>
        mobileRoutes[step as keyof typeof mobileRoutes] === location.pathname,
    );
    return currentIndex >= 0 ? currentIndex + 1 : 1;
  };

  const getCurrentMobileStepName = () => {
    const stepNames = {
      'store-basic': '가게 기본정보',
      'store-location': '가게 위치선택',
      'store-contact': '연락처 정보',
      'store-description': '가게 소개',
      'ddipbox-basic': '띱박스 기본정보',
      'ddipbox-pricing': '가격 설정',
      'ddipbox-quantity': '수량 설정',
      'notification': '알림 설정',
    };

    const currentPath = location.pathname;
    const currentStep = Object.entries(mobileRoutes).find(
      ([, path]) => path === currentPath,
    );

    return currentStep
      ? stepNames[currentStep[0] as keyof typeof stepNames]
      : '가게 기본정보';
  };

  return (
    <div className="min-h-full bg-gradient-to-br from-amber-50 via-white to-orange-50">
      {/* Progress Section */}
      {!isMobile && (
        <div className="bg-gradient-to-b from-white/80 to-white/50 py-8 backdrop-blur-sm">
          <div className="mx-auto max-w-4xl px-6">
            <div className="mb-8">
              <div className="mb-6 flex items-center justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">
                    설정 진행 상황
                  </h2>
                  <p className="mt-1 text-sm text-gray-600">
                    온보딩을 완료하여 꿀띱을 시작해보세요
                  </p>
                </div>
                <div className="text-right">
                  <div className="text-2xl font-bold text-amber-600">
                    {currentStepIndex + 1}
                    <span className="text-lg text-gray-400">
                      /{steps.length}
                    </span>
                  </div>
                </div>
              </div>

              {/* Enhanced Progress Bar */}
              <div className="relative">
                <Progress
                  value={progress}
                  variant="gradient"
                  size="xl"
                  className="shadow-lg"
                />
              </div>
            </div>

            {/* Step Indicators */}
            <div className="flex items-center justify-between">
              {steps.map(step => {
                const isCompleted = completedSteps.has(step.stepKey);
                const isCurrent = currentStep === step.stepKey;
                const isUpcoming = !isCompleted && !isCurrent;

                return (
                  <div
                    key={step.path}
                    className={`flex flex-col items-center gap-2 ${
                      isUpcoming ? 'opacity-50' : ''
                    }`}
                  >
                    <div
                      className={`flex h-12 w-12 items-center justify-center rounded-full border-2 transition-all ${
                        isCompleted
                          ? 'border-green-500 bg-green-500 text-white'
                          : isCurrent
                            ? 'border-amber-500 bg-amber-500 text-white'
                            : 'border-gray-300 bg-white text-gray-400'
                      }`}
                    >
                      {isCompleted ? (
                        <CheckCircle2 className="h-6 w-6" />
                      ) : (
                        <step.icon className="h-6 w-6" />
                      )}
                    </div>
                    <div className="text-center">
                      <p
                        className={`text-sm font-medium ${
                          isCompleted || isCurrent
                            ? 'text-gray-900'
                            : 'text-gray-500'
                        }`}
                      >
                        {step.label}
                      </p>
                      {!step.required && (
                        <p className="text-xs text-gray-400">(선택사항)</p>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}

      {/* Mobile Progress Bar */}
      {isMobile && (
        <div className="border-b border-amber-100 bg-gradient-to-r from-amber-50 via-white to-orange-50 py-4">
          <div className="mx-auto px-6">
            <div className="mb-4">
              <div className="mb-3 flex items-center justify-between">
                <div>
                  <h2 className="text-lg font-semibold text-gray-900">
                    진행 상황
                  </h2>
                  <p className="mt-0.5 text-xs text-gray-600">
                    단계별로 정보를 입력해주세요
                  </p>
                </div>
                <div className="text-right">
                  <div className="text-lg font-bold text-amber-600">
                    {getMobileStepProgress()}
                    <span className="text-sm text-gray-400">/8</span>
                  </div>
                </div>
              </div>

              {/* Mobile Enhanced Progress */}
              <div className="relative">
                <Progress
                  value={progress}
                  variant="gradient"
                  size="lg"
                  className="shadow-md"
                />
              </div>

              {/* Current step indicator */}
              <div className="mt-2 text-center">
                <span className="inline-flex items-center rounded-full bg-amber-100 px-2 py-1 text-xs font-medium text-amber-800">
                  현재 단계: {getCurrentMobileStepName()}
                </span>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Main Content */}
      <main className={`py-8 ${isMobile ? 'py-4' : ''}`}>
        <div
          className={`mx-auto px-6 ${isMobile ? 'max-w-full' : 'max-w-4xl'}`}
        >
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AdaptiveOnboardingLayout;
