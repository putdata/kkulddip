import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import {
  useOnboardingStore,
  type OnboardingStep,
} from '@/stores/onboardingStore';
import { ROUTE_PATH } from '@/router/route-path';

interface OnboardingGuardProps {
  step: OnboardingStep;
  children: React.ReactNode;
}

const OnboardingGuard = ({ step, children }: OnboardingGuardProps) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { canAccessStep, setCurrentStep } = useOnboardingStore();

  useEffect(() => {
    const hasAccess = canAccessStep(step);

    if (!hasAccess) {
      // 접근 권한이 없으면 첫 번째 단계로 리다이렉트
      navigate(ROUTE_PATH.ONBOARDING.STORE, { replace: true });
      return;
    }

    // 현재 단계 업데이트
    setCurrentStep(step);
  }, [step, canAccessStep, setCurrentStep, navigate, location.pathname]);

  // 접근 권한 체크
  if (!canAccessStep(step)) {
    return null; // 리다이렉트 처리 중
  }

  return <>{children}</>;
};

export default OnboardingGuard;
