interface ProgressStepsProps {
  currentStep: 'cart' | 'payment' | 'complete';
}

const OrderProgress = ({ currentStep }: ProgressStepsProps) => {
  return (
    <div className="px-6 pb-6">
      <div className="flex items-center justify-between">
        {/* 장바구니 단계 */}
        <div className="flex flex-col items-center">
          <div
            className={`mb-2 flex h-12 w-12 items-center justify-center rounded-2xl ${
              currentStep === 'cart' ? 'bg-orange-300' : 'bg-gray-200'
            }`}
          >
            <span
              className={`text-lg ${
                currentStep === 'cart' ? 'text-white' : 'text-gray-400'
              }`}
            >
              🛒
            </span>
          </div>
          <span
            className={`text-xs ${
              currentStep === 'cart' ? 'text-gray-500' : 'text-gray-400'
            }`}
          >
            장바구니
          </span>
        </div>

        {/* 연결선 */}
        <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

        {/* 결제 단계 */}
        <div className="flex flex-col items-center">
          <div
            className={`mb-2 flex h-12 w-12 items-center justify-center rounded-2xl ${
              currentStep === 'payment' ? 'bg-orange-300' : 'bg-gray-200'
            }`}
          >
            <span
              className={`pb-2 text-2xl ${
                currentStep === 'payment' ? 'text-white' : 'text-gray-500'
              }`}
            >
              💳
            </span>
          </div>
          <span
            className={`text-xs ${
              currentStep === 'payment' ? 'text-gray-500' : 'text-gray-400'
            }`}
          >
            결제
          </span>
        </div>

        {/* 연결선 */}
        <div className="mx-4 h-0.5 flex-1 bg-gray-200"></div>

        {/* 완료 단계 */}
        <div className="flex flex-col items-center">
          <div
            className={`mb-2 flex h-12 w-12 items-center justify-center rounded-2xl ${
              currentStep === 'complete' ? 'bg-green-500' : 'bg-gray-200'
            }`}
          >
            <span
              className={`text-lg ${
                currentStep === 'complete' ? 'text-white' : 'text-gray-400'
              }`}
            >
              ✓
            </span>
          </div>
          <span
            className={`text-xs ${
              currentStep === 'complete'
                ? 'font-medium text-gray-600'
                : 'text-gray-400'
            }`}
          >
            완료
          </span>
        </div>
      </div>
    </div>
  );
};

export default OrderProgress;
