// components/layouts/OrderFlowLayout/OrderFlowLayout.tsx
import { ArrowLeft } from 'lucide-react';
import ProgressSteps from '@/components/common/OrderProgress/OrderProgress';
import { useNavigate } from 'react-router-dom';

interface OrderFlowLayoutProps {
  title: string;
  currentStep: 'cart' | 'payment' | 'complete';
  onBack?: () => void;
  children: React.ReactNode;
  bottomButton?: React.ReactNode;
}

const OrderFlowLayout = ({
  title,
  currentStep,
  onBack,
  children,
  bottomButton,
}: OrderFlowLayoutProps) => {
  const navigate = useNavigate();

  const handleBack = () => {
    console.log('handleBack called'); // 디버깅용
    if (onBack && typeof onBack === 'function') {
      onBack();
    } else {
      navigate(-1);
    }
  };

  return (
    <div className="flex min-h-screen flex-col bg-white pb-20">
      {/* 헤더 */}
      <div className="relative flex w-full flex-row items-center p-3">
        <button onClick={handleBack} className="rounded-full p-2">
          <ArrowLeft className="h-6 w-6 text-gray-700" />
        </button>
        <h1 className="absolute left-1/2 -translate-x-1/2 text-lg font-semibold text-gray-700">
          {title}
        </h1>
      </div>

      {/* 전체 카드 */}
      <div className="flex-1 overflow-hidden rounded-[2.5rem] bg-amber-50 pt-8 shadow-lg">
        {/* 진행 상태 표시 */}
        <ProgressSteps currentStep={currentStep} />

        {/* 메인 콘텐츠 스크롤 영역 */}
        <div className="rounded-t-4xl flex-1 overflow-y-auto bg-amber-100 p-6">
          <div className="space-y-3">{children}</div>
        </div>
      </div>

      {/* 하단 버튼 */}
      {bottomButton && (
        <div className="fixed bottom-0 left-0 w-full bg-white px-4 py-4 shadow-md">
          <div className="mx-auto max-w-md">{bottomButton}</div>
        </div>
      )}
    </div>
  );
};

export default OrderFlowLayout;
