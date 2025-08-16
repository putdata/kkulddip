import { Outlet, useLocation } from 'react-router-dom';
import { Progress } from '@/components/ui/progress';
import { CheckCircle2, Store, Package, Bell } from 'lucide-react';
import { useOnboardingStore } from '@/stores/onboardingStore';
import { ROUTE_PATH } from '@/router/route-path';

const OnboardingLayout = () => {
  const location = useLocation();
  const { completedSteps, currentStep } = useOnboardingStore();

  const steps = [
    {
      path: ROUTE_PATH.ONBOARDING.STORE,
      label: '가게 생성',
      icon: Store,
      required: true,
      stepKey: 'store' as const,
    },
    {
      path: ROUTE_PATH.ONBOARDING.DDIPBOX,
      label: '띱박스 생성',
      icon: Package,
      required: false,
      stepKey: 'ddipbox' as const,
    },
    {
      path: ROUTE_PATH.ONBOARDING.NOTIFICATION,
      label: '알림 설정',
      icon: Bell,
      required: false,
      stepKey: 'notification' as const,
    },
  ];

  const getCurrentStep = () => {
    const currentPath = location.pathname;
    return steps.findIndex(step => step.path === currentPath);
  };

  const currentStepIndex = getCurrentStep();
  const progress = ((currentStepIndex + 1) / steps.length) * 100;

  return (
    <div className="min-h-full bg-gradient-to-br from-amber-50 via-white to-orange-50">
      {/* Header */}
      <header className="border-b bg-white/80 backdrop-blur-md">
        <div className="mx-auto max-w-4xl px-6 py-4">
          <div className="flex items-center gap-3">
            <div className="rounded-lg bg-gradient-to-r from-amber-500 to-orange-500 p-2">
              <Store className="h-6 w-6 text-white" />
            </div>
            <div>
              <h1 className="bg-gradient-to-r from-amber-600 to-orange-600 bg-clip-text text-2xl font-bold text-transparent">
                꿀띱
              </h1>
              <p className="text-sm text-gray-600">가게 등록을 시작해보세요</p>
            </div>
          </div>
        </div>
      </header>

      {/* Progress Section */}
      <div className="bg-white/50 py-8">
        <div className="mx-auto max-w-4xl px-6">
          <div className="mb-8">
            <div className="mb-4 flex items-center justify-between">
              <h2 className="text-xl font-semibold text-gray-900">
                설정 진행 상황
              </h2>
              <span className="text-sm text-gray-600">
                {currentStepIndex + 1} / {steps.length}
              </span>
            </div>
            <Progress value={progress} size="default" />
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

      {/* Main Content */}
      <main className="py-8">
        <div className="mx-auto max-w-4xl px-6">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default OnboardingLayout;
