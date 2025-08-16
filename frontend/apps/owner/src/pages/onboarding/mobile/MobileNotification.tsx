import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import {
  CheckCircle2,
  ArrowLeft,
  Bell,
  ShoppingBag,
  Settings,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Checkbox } from '@/components/ui/checkbox';
import { useOnboardingStore } from '@/stores/onboardingStore';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import { useNotification } from '@/hooks/useNotification';
import { handleOnboardingComplete } from '@/utils/onboardingUtils';

const MobileNotification = () => {
  const navigate = useNavigate();
  const { formData, updateFormData, setCurrentMobileStep, resetForm } =
    useOnboardingStore();
  const { requestPermission } = useNotification();
  const [isCompleting, setIsCompleting] = useState(false);

  const notificationTypes = [
    {
      id: 'orders' as const,
      icon: ShoppingBag,
      title: '주문 알림',
      description: '새로운 주문이 들어왔을 때 알림을 받습니다',
    },
    {
      id: 'system' as const,
      icon: Settings,
      title: '시스템 알림',
      description: '중요한 시스템 업데이트나 공지사항을 받습니다',
    },
  ];

  const handleNotificationToggle = (
    type: 'orders' | 'system',
    checked: boolean,
  ) => {
    updateFormData({
      notifications: {
        ...formData.notifications,
        [type]: checked,
      },
    });
  };

  const handleComplete = async () => {
    setIsCompleting(true);

    try {
      // 알림이 선택되어 있으면 권한 요청
      if (hasSelectedNotifications) {
        const currentPermission = Notification.permission;

        if (currentPermission === 'default') {
          const success = await requestPermission();
          if (success) {
            toast.success('알림이 활성화되었습니다!');
          } else {
            toast.error('알림 권한을 허용해주세요.');
          }
        } else if (currentPermission === 'denied') {
          toast.warning(
            '알림이 차단되어 있습니다. 브라우저 설정에서 알림을 허용해주세요.',
          );
        }
      }

      // 온보딩 완료 처리
      resetForm();
      await handleOnboardingComplete(navigate);
    } catch (error) {
      console.error('Onboarding completion failed:', error);
    } finally {
      setIsCompleting(false);
    }
  };

  const handleBack = () => {
    setCurrentMobileStep('ddipbox-quantity');
    navigate('/onboarding/ddipbox/quantity');
  };

  const handleSkip = () => {
    handleComplete();
  };

  const hasSelectedNotifications = Object.values(formData.notifications).some(
    Boolean,
  );

  return (
    <OnboardingGuard step="notification">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <h1 className="mb-2 text-2xl font-bold text-gray-900">알림 설정</h1>
          <p className="text-gray-600">받고 싶은 알림 유형을 선택해주세요</p>
          <p className="mt-1 text-sm text-gray-500">
            이 단계는 건너뛸 수 있습니다
          </p>
        </div>

        {/* Form */}
        <Card className="border-0 bg-white shadow-lg">
          <CardHeader className="pb-4">
            <CardTitle className="flex items-center gap-2 text-lg">
              <Bell className="h-5 w-5 text-amber-600" />
              알림 유형 선택
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 pt-0">
            {/* 알림 유형 목록 */}
            <div className="space-y-4">
              {notificationTypes.map(type => {
                const Icon = type.icon;
                const isChecked = formData.notifications[type.id];

                return (
                  <div
                    key={type.id}
                    className={`rounded-lg border-2 p-3 transition-all duration-200 ${
                      isChecked
                        ? 'border-amber-500 bg-amber-50'
                        : 'border-gray-200 bg-white hover:border-gray-300'
                    }`}
                  >
                    <div className="flex items-start space-x-3">
                      <Checkbox
                        id={type.id}
                        checked={isChecked}
                        onCheckedChange={checked =>
                          handleNotificationToggle(type.id, checked === true)
                        }
                        className="mt-1"
                      />
                      <div className="flex-1">
                        <div className="mb-1 flex items-center gap-2">
                          <Icon
                            className={`h-4 w-4 ${
                              isChecked ? 'text-amber-600' : 'text-gray-500'
                            }`}
                          />
                          <h3
                            className={`text-sm font-medium ${
                              isChecked ? 'text-amber-900' : 'text-gray-900'
                            }`}
                          >
                            {type.title}
                          </h3>
                        </div>
                        <p
                          className={`text-xs ${
                            isChecked ? 'text-amber-700' : 'text-gray-600'
                          }`}
                        >
                          {type.description}
                        </p>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>

            {/* 선택 상태 안내 */}
            {hasSelectedNotifications && (
              <div className="rounded-lg bg-green-50 p-3">
                <div className="flex items-center gap-2 text-sm text-green-700">
                  <CheckCircle2 className="h-4 w-4" />
                  <span className="font-medium">
                    알림 설정이 완료되었습니다!
                  </span>
                </div>
                <p className="mt-1 text-xs text-green-600">
                  선택한 알림은 설정에서 언제든지 변경할 수 있습니다
                </p>
              </div>
            )}

            {/* 완료 안내 */}
            <div className="rounded-lg bg-blue-50 p-3">
              <div className="flex items-center gap-2 text-sm text-blue-700">
                <CheckCircle2 className="h-4 w-4" />
                <span className="font-medium">온보딩 완료 준비!</span>
              </div>
              <p className="mt-1 text-xs text-blue-600">
                모든 설정이 완료되면 메인 화면으로 이동합니다
              </p>
            </div>

            {/* Navigation Buttons */}
            <div className="space-y-3 pt-4">
              <Button
                onClick={handleComplete}
                disabled={isCompleting}
                className="h-12 w-full bg-gradient-to-r from-green-500 to-emerald-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-green-600 hover:to-emerald-600 hover:shadow-xl disabled:opacity-50"
              >
                {isCompleting ? (
                  '완료 중...'
                ) : (
                  <>
                    <CheckCircle2 className="mr-2 h-4 w-4" />
                    온보딩 완료
                  </>
                )}
              </Button>

              <div className="flex gap-3">
                <Button
                  variant="outline"
                  onClick={handleBack}
                  disabled={isCompleting}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  <ArrowLeft className="mr-2 h-4 w-4" />
                  이전
                </Button>
                <Button
                  variant="secondary"
                  onClick={handleSkip}
                  disabled={isCompleting}
                  className="h-10 flex-1 text-sm font-medium"
                >
                  알림 없이 완료
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Progress Info */}
        <div className="text-center">
          <p className="text-xs text-gray-500">7/7 단계 완료</p>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default MobileNotification;
