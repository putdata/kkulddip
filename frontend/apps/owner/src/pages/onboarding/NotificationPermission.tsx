import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import {
  Bell,
  BellRing,
  ShoppingCart,
  AlertTriangle,
  CheckCircle2,
  Clock,
  Shield,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { useNotification } from '@/hooks/useNotification';
import OnboardingGuard from '@/components/onboarding/OnboardingGuard';
import { handleOnboardingComplete } from '@/utils/onboardingUtils';

const NotificationPermission = () => {
  const navigate = useNavigate();
  const { requestPermission } = useNotification();
  const [isProcessing, setIsProcessing] = useState(false);

  const notificationTypes = [
    {
      icon: ShoppingCart,
      title: '주문 알림',
      description: '새로운 주문이 들어올 때 즉시 알림을 받으세요',
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
    },
    {
      icon: AlertTriangle,
      title: '시스템 알림',
      description: '중요한 업데이트나 공지사항을 알려드립니다',
      color: 'text-red-600',
      bgColor: 'bg-red-100',
    },
  ];

  const handleAllowNotifications = async () => {
    setIsProcessing(true);
    try {
      // 현재 알림 권한 상태 확인
      const currentPermission = Notification.permission;

      if (currentPermission === 'granted') {
        toast.success('알림이 이미 허용되어 있습니다!');
        handleComplete();
        return;
      }

      if (currentPermission === 'denied') {
        toast.error(
          '알림이 차단되어 있습니다. 브라우저 설정에서 알림을 허용해주세요.',
        );
        setIsProcessing(false);
        return;
      }

      // 권한 요청 (default 상태일 때만)
      const success = await requestPermission();
      if (success) {
        toast.success('알림 권한이 허용되었습니다!');
        handleComplete();
      } else {
        toast.error('알림 권한을 허용해주세요.');
      }
    } catch (error) {
      console.error('Notification permission failed:', error);
      toast.error('알림 설정 중 오류가 발생했습니다.');
    } finally {
      setIsProcessing(false);
    }
  };

  const handleSkip = () => {
    handleComplete();
  };

  const handleComplete = async () => {
    // 온보딩 완료 후 적절한 페이지로 이동
    await handleOnboardingComplete(navigate);
  };

  return (
    <OnboardingGuard step="notification">
      <div className="space-y-6">
        {/* Header */}
        <div className="text-center">
          <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-gradient-to-r from-blue-500 to-purple-500">
            <BellRing className="h-8 w-8 text-white" />
          </div>
          <h1 className="mb-2 text-2xl font-bold text-gray-900">
            알림을 허용하시겠어요?
          </h1>
          <p className="text-gray-600">
            중요한 주문과 재고 정보를 실시간으로 받아보세요
          </p>
          <p className="mt-1 text-sm text-gray-500">
            나중에 설정에서 언제든 변경할 수 있습니다
          </p>
        </div>

        {/* Notification Benefits */}
        <div className="grid gap-3 md:grid-cols-2">
          {notificationTypes.map((type, index) => (
            <Card key={index} className="border-0 shadow-sm">
              <CardContent className="p-3 text-center">
                <div
                  className={`mx-auto mb-2 flex h-8 w-8 items-center justify-center rounded-full ${type.bgColor}`}
                >
                  <type.icon className={`h-4 w-4 ${type.color}`} />
                </div>
                <h3 className="mb-1 text-sm font-semibold text-gray-900">
                  {type.title}
                </h3>
                <p className="text-xs text-gray-600">{type.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Privacy Notice */}
        <Card className="border border-blue-200 bg-blue-50">
          <CardContent className="p-4">
            <div className="flex items-start gap-3">
              <div className="flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full bg-blue-500">
                <Shield className="h-4 w-4 text-white" />
              </div>
              <div>
                <h3 className="mb-1 text-sm font-semibold text-blue-900">
                  개인정보 보호
                </h3>
                <p className="text-xs text-blue-700">
                  비즈니스 운영에 필요한 정보만 전송되며, 언제든 설정에서 변경
                  가능합니다.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Action Buttons */}
        <div className="space-y-3">
          <Button
            onClick={handleAllowNotifications}
            disabled={isProcessing}
            className="h-12 w-full bg-gradient-to-r from-blue-500 to-purple-500 text-base font-semibold text-white shadow-lg transition-all duration-300 hover:from-blue-600 hover:to-purple-600 hover:shadow-xl disabled:opacity-50"
          >
            {isProcessing ? (
              '설정 중...'
            ) : (
              <>
                <Bell className="mr-2 h-4 w-4" />
                알림 허용하기
                <CheckCircle2 className="ml-2 h-4 w-4" />
              </>
            )}
          </Button>

          <Button
            variant="secondary"
            onClick={handleSkip}
            className="h-12 w-full text-base font-medium"
            disabled={isProcessing}
          >
            <Clock className="mr-2 h-4 w-4" />
            나중에 설정하기
          </Button>
        </div>
      </div>
    </OnboardingGuard>
  );
};

export default NotificationPermission;
